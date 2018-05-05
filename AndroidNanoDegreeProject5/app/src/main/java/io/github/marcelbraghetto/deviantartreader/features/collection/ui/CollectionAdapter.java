package io.github.marcelbraghetto.deviantartreader.features.collection.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionMode;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.device.contracts.DeviceProvider;

/**
 * Created by Marcel Braghetto on 29/02/16.
 *
 * The collection adapter is responsible for displaying the artworks collection from a cursor.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private Activity mHostActivity;
    private GridItemDelegate mGridItemDelegate;
    private Cursor mCursor;
    private Map<String, Integer> mAllArtworksColumnMap;
    private DeviceProvider mDeviceProvider;
    private CollectionMode mCollectionMode;
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

    public CollectionAdapter(@NonNull Activity hostActivity, @NonNull GridItemDelegate gridItemDelegate) {
        mAllArtworksColumnMap = ArtworksDatabaseContract.Artworks.getAllColumnsIndexMap();
        mDeviceProvider = MainApp.getDagger().getDeviceProvider();
        mGridItemDelegate = gridItemDelegate;
        mHostActivity = hostActivity;
        mCollectionMode = CollectionMode.MultiColumn;
        mMaximumImageDimension = hostActivity.getResources().getInteger(R.integer.maximum_image_dimension);
    }

    /**
     * Configure the collection mode to use when displaying the content.
     * @param collectionMode to use.
     */
    public void setCollectionMode(@NonNull CollectionMode collectionMode) {
        mCollectionMode = collectionMode;
        notifyDataSetChanged();
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

        if(mGridItemDelegate != null) {
            mGridItemDelegate.onCollectionChanged(getItemCount());
        }

        return oldCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.collection_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        Artwork artwork = new Artwork();
        artwork.populateFromCursor(mCursor, mAllArtworksColumnMap);

        switch (mCollectionMode) {
            case SingleColumn:
                viewHolder.setSingleColumn(true);
                break;
            default:
                viewHolder.setSingleColumn(false);
                break;
        }

        viewHolder.populateFromArtwork(artwork);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.collection_item_image) CollectionImageView mImageView;
        @Bind(R.id.collection_item_favourite_icon) ImageView mFavouritesIcon;
        @Bind(R.id.collection_item_title) TextView mTitleTextView;

        private Artwork mArtwork;
        private boolean mIsFavourite;
        private boolean mInteractionReady;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            mImageView.setOnClickListener(mImageClickListener);
            mFavouritesIcon.setOnClickListener(mFavouritesIconClickListener);
        }

        public void populateFromArtwork(@NonNull Artwork artwork) {
            mInteractionReady = false;

            mArtwork = artwork;
            mIsFavourite = artwork.isFavourite();
            mTitleTextView.setText(mArtwork.getTitle());

            if(mIsFavourite) {
                mFavouritesIcon.setImageResource(R.drawable.icon_favourite_on);
            } else {
                mFavouritesIcon.setImageResource(R.drawable.icon_favourite_off);
            }

            // We need to tell our custom image view how big it should be based on the width
            // and height values that are provided by the original data feed. This step is critical
            // to give our staggered grid a nice tailored fit size for each image so it doesn't
            // need to be cropped or scaled against its aspect ratio at all.
            mImageView.setSourceSize(mArtwork.getImageWidth(), mArtwork.getImageHeight());

            // It is important that we allow Glide to load the image and cache its full original
            // bitmap, so it can be used seamlessly for the Lollipop shared element transitions.
            DrawableRequestBuilder<String> request = Glide.with(itemView.getContext())
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

            request.into(mImageView);
        }

        public void setSingleColumn(boolean value) {
            ((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(value);
        }

        private View.OnClickListener mFavouritesIconClickListener = new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(mGridItemDelegate != null) {
                    mGridItemDelegate.onToggleFavourite(mArtwork, !mIsFavourite);
                }
            }
        };

        private View.OnClickListener mImageClickListener = new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(!mInteractionReady) {
                    return;
                }

                Bundle sceneTransitionBundle = null;

                // If we are running on at least Lollipop, then we can create the bundle that is
                // required to perform shared element transitions.
                if(mDeviceProvider.isAtLeastLollipop()) {
                    sceneTransitionBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mHostActivity, mImageView, mImageView.getTransitionName()).toBundle();
                }

                mGridItemDelegate.onArtworkSelected(mArtwork, sceneTransitionBundle);
            }
        };
    }
}