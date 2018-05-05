package io.github.marcelbraghetto.dailydeviations.features.collection.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.databinding.ObservableField;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.lang.ref.WeakReference;
import java.util.Map;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.databinding.CollectionItemBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.content.ArtworksDatabaseContract;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.core.WeakWrapper;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.device.contracts.DeviceProvider;

import static io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode.MultiColumn;
import static io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode.SingleColumn;

/**
 * Created by Marcel Braghetto on 29/02/16.
 *
 * The collection adapter is responsible for displaying the artworks collection from a cursor.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
    private WeakReference<Activity> mHostActivity;
    private GridItemDelegate mGridItemDelegate = WeakWrapper.wrapEmpty(GridItemDelegate.class);
    private Cursor mCursor;
    private Map<String, Integer> mAllArtworksColumnMap;
    private DeviceProvider mDeviceProvider;
    private CollectionDisplayMode mCollectionMode;
    private int mMaximumImageDimension;

    public interface GridItemDelegate {
        /**
         * When an artwork is selected from the collection, its guid and potentially a scene
         * transition bundle will be reported.
         * @param artwork that was selected.
         * @param sceneTransitionBundle transition bundle for Lollipop transitions.
         */
        void onArtworkSelected(@NonNull Artwork artwork, @Nullable Bundle sceneTransitionBundle);

        /**
         * An artwork has been selected as a favourite (or unfavourited).
         * @param artwork that was selected.
         * @param isFavourite whether it should be marked as a favourite or not.
         */
        void onToggleFavourite(@NonNull Artwork artwork, boolean isFavourite);

        /**
         * Called if a cursor is set to the adapter which doesn't contain any items.
         */
        void onCollectionChanged(int numItems);
    }

    public CollectionAdapter(@NonNull Activity hostActivity) {
        mAllArtworksColumnMap = ArtworksDatabaseContract.Artworks.getAllColumnsIndexMap();
        mDeviceProvider = MainApp.getDagger().getDeviceProvider();
        mHostActivity = new WeakReference<>(hostActivity);
        mCollectionMode = MultiColumn;
        mMaximumImageDimension = hostActivity.getResources().getInteger(R.integer.maximum_image_dimension);
    }

    public void setGridItemDelegate(@Nullable GridItemDelegate delegate) {
        mGridItemDelegate = WeakWrapper.wrap(delegate, GridItemDelegate.class);
    }

    /**
     * Configure the collection mode to use when displaying the content.
     * @param collectionMode to use.
     */
    public void setCollectionMode(@NonNull CollectionDisplayMode collectionMode) {
        if(collectionMode != mCollectionMode) {
            mCollectionMode = collectionMode;
            notifyDataSetChanged();
        }
    }

    public Cursor swapCursor(@Nullable Cursor cursor) {
        if(mCursor == cursor) {
            return null;
        }

        Cursor oldCursor = mCursor;

        mCursor = cursor;

        if(mCursor != null) {
            notifyDataSetChanged();
        }

        mGridItemDelegate.onCollectionChanged(getItemCount());

        return oldCursor;
    }

    @Override
    public CollectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new CollectionViewHolder(CollectionItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(CollectionViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        Artwork artwork = new Artwork();
        artwork.populateFromCursor(mCursor, mAllArtworksColumnMap);
        viewHolder.setSingleColumn(mCollectionMode == SingleColumn);
        viewHolder.populateFromArtwork(artwork);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {
        private final CollectionItemBinding mViews;

        private Artwork mArtwork;
        private boolean mInteractionReady;

        public final Glue glue = new Glue();
        public class Glue {
            public final ObservableField<String> title = new ObservableField<>("");
            public final ObservableField<Boolean> isFavourite = new ObservableField<>(false);
        }

        public CollectionViewHolder(@NonNull CollectionItemBinding views) {
            super(views.getRoot());
            mViews = views;
            mViews.setViewHolder(this);
        }

        public void populateFromArtwork(@NonNull Artwork artwork) {
            mInteractionReady = false;
            mArtwork = artwork;

            glue.title.set(mArtwork.getTitle());
            glue.isFavourite.set(mArtwork.isFavourite());

            // We need to tell our custom image view how big it should be based on the width
            // and height values that are provided by the original data feed. This step is critical
            // to give our staggered grid a nice tailored fit size for each image so it doesn't
            // need to be cropped or scaled against its aspect ratio at all.
            mViews.collectionItemImage.setSourceSize(mArtwork.getImageWidth(), mArtwork.getImageHeight());

            // It is important that we allow Glide to load the image and cache its full original
            // bitmap, so it can be used seamlessly for the Lollipop shared element transitions.
            DrawableRequestBuilder<String> request = Glide.with(mViews.collectionItemImage.getContext())
                    .load(mArtwork.getImageUrl())
                    .override(mMaximumImageDimension, mMaximumImageDimension)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mInteractionReady = true;
                            return false;
                        }
                    });

            request.into(mViews.collectionItemImage);

            mViews.executePendingBindings();
        }

        public void setSingleColumn(boolean value) {
            ((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(value);
        }

        public void toggleFavouriteClicked() {
            mGridItemDelegate.onToggleFavourite(mArtwork, !mArtwork.isFavourite());
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void imageClicked() {
            if(!mInteractionReady) {
                return;
            }

            Activity hostActivity = mHostActivity.get();
            if(hostActivity == null) {
                return;
            }

            Bundle sceneTransitionBundle = null;

            // If we are running on at least Lollipop, then we can create the bundle that is
            // required to perform shared element transitions.
            if(mDeviceProvider.isAtLeastLollipop()) {
                sceneTransitionBundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(hostActivity, mViews.collectionItemImage, mViews.collectionItemImage.getTransitionName())
                        .toBundle();
            }

            mGridItemDelegate.onArtworkSelected(mArtwork, sceneTransitionBundle);
        }
    }
}