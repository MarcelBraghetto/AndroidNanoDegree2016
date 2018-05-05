package io.github.marcelbraghetto.dailydeviations.features.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity;
import io.github.marcelbraghetto.dailydeviations.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.service.ArtworksDataService;

/**
 * Created by Marcel Braghetto on 7/12/15.
 *
 * Widget provider for the football home screen widget.
 */
public class HomeWidgetProvider extends AppWidgetProvider {
    public static String ACTION_CLICK_LIST_ITEM = "io.github.marcelbraghetto.dailydeviations.features.widget.CLICK_LIST_ITEM";
    public static String ACTION_OPEN_APP = "io.github.marcelbraghetto.dailydeviations.features.widget.OPEN_APP";
    public static String ACTION_UPDATE = "io.github.marcelbraghetto.dailydeviations.features.widget.UPDATE";
    public static String ACTION_REFRESH = "io.github.marcelbraghetto.dailydeviations.features.widget.REFRESH";
    public static String ACTION_WALLPAPER = "io.github.marcelbraghetto.dailydeviations.features.widget.WALLPAPER";

    /**
     * Cause any running home widgets to refresh themselves, typically called after a data
     * refresh completes successfully.
     * @param context to use for sending a refresh broadcast.
     */
    public static void triggerRefresh(@NonNull Context context) {
        context.getApplicationContext().sendBroadcast(new Intent(ACTION_UPDATE));
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_OPEN_APP.equals(action)) {
            Intent homeIntent = new Intent(context, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(homeIntent);
        }

        if (ACTION_CLICK_LIST_ITEM.equals(action)) {
            Intent listItemIntent = new Intent(context, DetailActivity.class);
            listItemIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            listItemIntent.putExtras(intent.getExtras());
            context.startActivity(listItemIntent);
        }

        if(ACTION_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, HomeWidgetProvider.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }

        if(ACTION_REFRESH.equals(action)) {
            context.startService(ArtworksDataService.createServiceIntent(context, ArtworksDataService.RefreshReason.ForceRefresh));
        }

        if(ACTION_WALLPAPER.equals(action)) {
            Artwork artwork = MainApp.getDagger().getArtworksProvider().getRandomArtwork();
            if(artwork != null) {
                MainApp.getDagger().getWallpaperProvider().setPhoneWallpaper(artwork.getImageUrl(), true);
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews layout = buildLayout(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, layout);
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.home_widget_list_view);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews buildLayout(Context context, int appWidgetId) {
        RemoteViews remoteViews;

        Intent intent = new Intent(context, HomeWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.home_widget);
        remoteViews.setRemoteAdapter(appWidgetId, R.id.home_widget_list_view, intent);
        remoteViews.setEmptyView(R.id.home_widget_list_view, R.id.home_widget_empty_view);

        String lastUpdateText = DateTimeFormat.shortDateTime().print(DateTime.now());
        remoteViews.setTextViewText(R.id.home_widget_subtitle, lastUpdateText);

        Intent listItemIntent = new Intent(context, HomeWidgetProvider.class);
        listItemIntent.setAction(HomeWidgetProvider.ACTION_CLICK_LIST_ITEM);
        listItemIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        listItemIntent.setData(Uri.parse(listItemIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent listItemPendingIntent = PendingIntent.getBroadcast(context, 0, listItemIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.home_widget_list_view, listItemPendingIntent);

        Intent wallpaperIntent = new Intent(context, HomeWidgetProvider.class);
        wallpaperIntent.setAction(HomeWidgetProvider.ACTION_WALLPAPER);
        PendingIntent wallpaperPendingIntent = PendingIntent.getBroadcast(context, 0, wallpaperIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.home_widget_wallpaper_button, wallpaperPendingIntent);

        Intent refreshIntent = new Intent(context, HomeWidgetProvider.class);
        refreshIntent.setAction(HomeWidgetProvider.ACTION_REFRESH);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.home_widget_refresh_button, refreshPendingIntent);

        Intent openAppIntent = new Intent(context, HomeWidgetProvider.class);
        openAppIntent.setAction(HomeWidgetProvider.ACTION_OPEN_APP);
        PendingIntent headerPendingIntent = PendingIntent.getBroadcast(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.home_widget_open_app_button, headerPendingIntent);

        return remoteViews;
    }
}
