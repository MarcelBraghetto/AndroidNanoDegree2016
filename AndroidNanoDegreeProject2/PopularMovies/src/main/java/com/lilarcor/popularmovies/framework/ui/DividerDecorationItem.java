package com.lilarcor.popularmovies.framework.ui;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lilarcor.popularmovies.R;

/**
 * Created by Marcel Braghetto on 9/08/15.
 *
 * RecyclerView needs some help to draw things like
 * dividing lines between items (which ListView gave
 * for free).
 */
public class DividerDecorationItem extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public DividerDecorationItem(Resources resources) {
        mDivider = resources.getDrawable(R.drawable.default_list_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}