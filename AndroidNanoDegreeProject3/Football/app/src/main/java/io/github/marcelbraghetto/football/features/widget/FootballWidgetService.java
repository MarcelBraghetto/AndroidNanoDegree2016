package io.github.marcelbraghetto.football.features.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.joda.time.DateTime;

import java.util.Map;

import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract;
import io.github.marcelbraghetto.football.framework.providers.football.models.FootballGame;

/**
 * Created by Marcel Braghetto on 7/12/15.
 *
 * Remote views service for the football
 * home screen widget.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FootballWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FootballWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class FootballWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Map<String, Integer> mColumnIndexMap;
    private FootballGame mGame;
    private Context mContext;
    private Cursor mCursor;

    public FootballWidgetRemoteViewsFactory(@NonNull Context context) {
        mGame = new FootballGame();
        mColumnIndexMap = FootballDatabaseContract.Games.getAllColumnsIndexMap();
        mContext = context;
    }

    @Override
    public void onCreate() { }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        DateTime now = DateTime.now();

        int year = now.getYear();
        int month = now.getMonthOfYear();
        int day = now.getDayOfMonth();

        // I was getting security permissions when trying to use the content provider from here.
        // Found a workaround according to the following SO post:
        // http://stackoverflow.com/questions/9497270/widget-with-content-provider-impossible-to-use-readpermission
        long token = Binder.clearCallingIdentity();
        try {
            mCursor = mContext.getContentResolver().query(FootballDatabaseContract.Games.getContentUriGames(year, month, day), null, null, null, null);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 :mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(mCursor.moveToPosition(position)) {
            mGame.populateFromCursor(mCursor, mColumnIndexMap);
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.football_widget_list_item);

        remoteViews.setTextViewText(R.id.widget_list_item_title_home, mGame.getHomeTeamName());
        remoteViews.setTextViewText(R.id.widget_list_item_title_away, mGame.getAwayTeamName());

        if(mGame.hasGoals()) {
            remoteViews.setTextViewText(R.id.widget_list_item_score, mContext.getString(R.string.scores_list_item_goals, mGame.getHomeTeamGoals(), mGame.getAwayTeamGoals()));
            remoteViews.setViewVisibility(R.id.widget_list_item_score, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.widget_list_item_score, View.GONE);
        }

        remoteViews.setTextViewText(R.id.widget_list_item_time, mGame.getFormattedGameTime());
        remoteViews.setOnClickFillInIntent(R.id.widget_list_item_root_view, new Intent());

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
