package io.github.marcelbraghetto.jokemachine.lib.android.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import io.github.marcelbraghetto.jokemachine.lib.android.utils.FontUtils;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Custom text view that simply adopts the app font.
 */
public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(FontUtils.Instance.getAppTypeface());
    }
}
