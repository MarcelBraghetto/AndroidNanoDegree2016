package io.github.marcelbraghetto.football.features.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract;

/**
 * Created by Marcel Braghetto on 7/12/15.
 *
 * Widget provider for the football home
 * screen widget.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FootballWidgetProvider extends AppWidgetProvider {
    public static String ACTION_CLICK = "io.github.marcelbraghetto.football.features.widget.CLICK";
    public static String ACTION_UPDATE = "io.github.marcelbraghetto.football.features.widget.UPDATE";

    private static FootballDataProviderObserver sDataObserver;
    private static Handler sWorkerQueue;

    public FootballWidgetProvider() {
        HandlerThread sWorkerThread = new HandlerThread("FootballWidgetProvider-WorkerThread");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onEnabled(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        if (sDataObserver == null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, FootballWidgetProvider.class);
            sDataObserver = new FootballDataProviderObserver(appWidgetManager, componentName, sWorkerQueue);
            contentResolver.registerContentObserver(FootballDatabaseContract.Games.getContentUriAllGames(), true, sDataObserver);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_CLICK.equals(action)) {
            Intent homeIntent = new Intent(context, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(homeIntent);
        }

        if(ACTION_UPDATE.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, FootballWidgetProvider.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews layout = buildLayout(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, layout);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews buildLayout(Context context, int appWidgetId) {
        RemoteViews remoteViews;

        Intent intent = new Intent(context, FootballWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.football_widget_layout);
        remoteViews.setRemoteAdapter(appWidgetId, R.id.football_scores_list_view, intent);
        remoteViews.setEmptyView(R.id.football_scores_list_view, R.id.football_scores_empty_view);

        Intent listItemIntent = new Intent(context, FootballWidgetProvider.class);
        listItemIntent.setAction(FootballWidgetProvider.ACTION_CLICK);
        listItemIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        listItemIntent.setData(Uri.parse(listItemIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent listItemPendingIntent = PendingIntent.getBroadcast(context, 0, listItemIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.football_scores_list_view, listItemPendingIntent);

        Intent headerIntent = new Intent(context, FootballWidgetProvider.class);
        headerIntent.setAction(FootballWidgetProvider.ACTION_CLICK);
        PendingIntent headerPendingIntent = PendingIntent.getBroadcast(context, 0, headerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.football_widget_header_view, headerPendingIntent);

        return remoteViews;
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class FootballDataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

    FootballDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @Override
    public void onChange(boolean selfChange) {
        mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.football_scores_list_view);
    }
}