package io.github.marcelbraghetto.dailydeviations.features.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;

/**
 * Created by Marcel Braghetto on 7/12/15.
 *
 * Remote views service for the home screen widget.
 */
public class HomeWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new HomeWidgetWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}

class HomeWidgetWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Map<String, Integer> mColumnIndexMap;
    private Artwork mArtwork;
    private Context mContext;
    private Cursor mCursor;

    public HomeWidgetWidgetRemoteViewsFactory(@NonNull Context context) {
        mArtwork = new Artwork();
        mColumnIndexMap = ArtworksDatabaseContract.Artworks.getAllColumnsIndexMap();
        mContext = context;
    }

    @Override
    public void onCreate() { }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        // I was getting security permissions when trying to use the content provider from here.
        // Found a workaround according to the following SO post:
        // http://stackoverflow.com/questions/9497270/widget-with-content-provider-impossible-to-use-readpermission
        long token = Binder.clearCallingIdentity();
        try {
            mCursor = mContext.getContentResolver().query(ArtworksDatabaseContract.Artworks.getContentUriLatestArtworks(), null, null, null, null);
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
            mArtwork.populateFromCursor(mCursor, mColumnIndexMap);
        }

        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.home_widget_item);

        remoteViews.setTextViewText(R.id.widget_list_item_title, mArtwork.getTitle());
        remoteViews.setTextViewText(R.id.widget_list_item_author, mArtwork.getAuthor());

        Intent intent = new Intent();
        mArtwork.putInto(intent);
        remoteViews.setOnClickFillInIntent(R.id.widget_list_item_root_view, intent);

        Bitmap artworkIcon;

        try {
            artworkIcon = Glide.with(mContext)
                    .load(mArtwork.getImageUrl())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(128, 128)
                    .into(128, 128)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            artworkIcon = null;
        }

        if(artworkIcon == null) {
            remoteViews.setImageViewResource(R.id.widget_list_item_image, R.drawable.logo_big);
        } else {
            remoteViews.setImageViewBitmap(R.id.widget_list_item_image, artworkIcon);
        }

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
