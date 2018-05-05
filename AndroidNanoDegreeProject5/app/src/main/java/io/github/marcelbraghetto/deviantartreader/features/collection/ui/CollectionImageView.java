package io.github.marcelbraghetto.deviantartreader.features.collection.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Marcel Braghetto on 5/03/16.
 *
 * Custom image view that can resize itself maintaining its aspect ratio given a width and height.
 */
public class CollectionImageView extends ImageView {
    private float mSourceWidth;
    private float mSourceHeight;

    public CollectionImageView(Context context) {
        super(context);
    }

    public CollectionImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollectionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Adjust the image view to align with the aspect ratio size of the given source.
     * @param sourceWidth to adhere to when calculating the ratio size.
     * @param sourceHeight to adhere to when calculating the ratio size.
     */
    public void setSourceSize(int sourceWidth, int sourceHeight) {
        mSourceWidth = sourceWidth;
        mSourceHeight = sourceHeight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // We don't want divide by zero errors now do we?
        if(mSourceWidth <= 0) {
            return;
        }

        // Determine the appropriate height of the image view given the available layout width
        // computed against the size dimensions of the source image.
        float measuredWidth = getMeasuredWidth();
        float ratio = measuredWidth / mSourceWidth;
        int newHeight = (int) Math.floor(mSourceHeight * ratio);
        setMeasuredDimension((int)measuredWidth, newHeight);
    }
}
