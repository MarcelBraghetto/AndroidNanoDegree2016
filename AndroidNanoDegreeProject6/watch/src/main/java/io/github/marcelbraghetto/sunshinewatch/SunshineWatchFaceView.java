package io.github.marcelbraghetto.sunshinewatch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;
import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherUtils;

/**
 * Created by Marcel Braghetto on 24/04/16.
 *
 * Custom view to encapsulate the watch face using a layout rather than manually painting.
 *
 * Inspired by this post: https://sterlingudell.wordpress.com/2015/05/10/layout-based-watch-faces-for-android-wear/
 */
//region Watch Face View
/* package */ class SunshineWatchFaceView extends RelativeLayout {
    @Bind(R.id.watch_face_container) ViewGroup mRoot;

    private final Point mDisplaySize = new Point();
    private int mSpecWidth;
    private int mSpecHeight;

    private ActiveLayout mActiveLayout;
    private AmbientLayout mAmbientLayout;
    private WeatherForecast mWeatherForecast;

    private enum RenderMode {
        None,
        Active,
        Ambient
    }

    private final DateTimeFormatter mTimeFormatter;
    private RenderMode mRenderMode;
    private DateTime mNow;

    public SunshineWatchFaceView(Context context) {
        super(context);

        inflate(getContext(), R.layout.watch_face_root, this);
        ButterKnife.bind(this);

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(mDisplaySize);
        updateMeasureSpec();

        // Android Wear devices don't appear to respect the standard resource qualifiers - it
        // is not possible to tell the difference between a 280x280 vs a 360x360 device using
        // the resource system - so unfortunately we have to use the pixel dimension of the
        // screen to programmatically decide on what resources to apply.
        boolean isLarge = mDisplaySize.x >= 360;

        mActiveLayout = new ActiveLayout(getContext());
        mAmbientLayout = new AmbientLayout(getContext());

        if(isLarge) {
            mActiveLayout.initLargeUI();
            mAmbientLayout.initLargeUI();
        }

        mTimeFormatter = DateTimeFormat.forPattern("h:mm");
        mRenderMode = RenderMode.None;
        mNow = DateTime.now();
    }

    public void setWeatherForecast(@Nullable WeatherForecast weatherForecast) {
        mWeatherForecast = weatherForecast;
    }

    public void updateMeasureSpec() {
        mSpecWidth = View.MeasureSpec.makeMeasureSpec(mDisplaySize.x, View.MeasureSpec.EXACTLY);
        mSpecHeight = View.MeasureSpec.makeMeasureSpec(mDisplaySize.y, View.MeasureSpec.EXACTLY);
    }

    public void render(@NonNull Canvas canvas, boolean ambient) {
        RenderMode renderMode = getRenderMode(ambient);

        // If we encounter a render mode that differs from our current mode, we need to adopt it
        // then assign the correct child view to represent that mode.
        if(mRenderMode != renderMode) {
            mRenderMode = renderMode;
            mRoot.removeAllViews();
            switch (mRenderMode) {
                case Active:
                    mRoot.addView(mActiveLayout);
                    break;
                case Ambient:
                    mRoot.addView(mAmbientLayout);
                    break;
            }
        }

        mNow = DateTime.now();
        switch (mRenderMode) {
            case Active:
                mActiveLayout.updateUI(mTimeFormatter.print(mNow), mNow, mWeatherForecast);
                break;
            case Ambient:
                mAmbientLayout.updateUI(mTimeFormatter.print(mNow));
                break;
        }

        render(canvas);
    }

    private void render(Canvas canvas) {
        measure(mSpecWidth, mSpecHeight);
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        draw(canvas);
    }

    private RenderMode getRenderMode(boolean ambient) {
        return ambient ? SunshineWatchFaceView.RenderMode.Ambient : SunshineWatchFaceView.RenderMode.Active;
    }
    //endregion

    //region Ambient Watch Face
    /* package */ static class AmbientLayout extends RelativeLayout {
        @Bind(R.id.watch_face_time_text) TextView mTimeTextView;

        public AmbientLayout(Context context) {
            super(context);
            inflate(getContext(), R.layout.watch_face_ambient, this);
            ButterKnife.bind(this);
            mTimeTextView.getPaint().setAntiAlias(false);
        }

        public void initLargeUI() {

        }

        public void updateUI(@NonNull String timeText) {
            mTimeTextView.setText(timeText);
        }

    }
    //endregion

    //region Active Watch Face
    /* package */ static class ActiveLayout extends RelativeLayout {
        @Bind(R.id.watch_face_logo) View mLogoView;
        @Bind(R.id.watch_face_time_text) TextView mTimeTextView;
        @Bind(R.id.watch_face_date_text) TextView mDateTextView;
        @Bind(R.id.watch_face_weather_container) View mForecastContainer;
        @Bind(R.id.watch_face_weather_icon) ImageView mForecastIcon;
        @Bind(R.id.watch_face_weather_temperature_max) TextView mMaxTempTextView;
        @Bind(R.id.watch_face_weather_temperature_min) TextView mMinTempTextView;

        private final DateTimeFormatter mDateFormatter;

        public ActiveLayout(Context context) {
            super(context);
            mDateFormatter = DateTimeFormat.forPattern("EEE, MMM d yyyy");
            inflate(getContext(), R.layout.watch_face_active, this);
            ButterKnife.bind(this);
        }

        public void initLargeUI() {
            Resources res = getContext().getResources();

            mLogoView.getLayoutParams().height = res.getDimensionPixelSize(R.dimen.WatchFaceLogoHeightLarge);
            mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.WatchFaceTimeTextSizeLarge));
            mDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.WatchFaceDateTextSizeLarge));
            mForecastIcon.getLayoutParams().width = res.getDimensionPixelSize(R.dimen.WatchFaceIconSizeLarge);
            mForecastIcon.getLayoutParams().height = res.getDimensionPixelSize(R.dimen.WatchFaceIconSizeLarge);
            mMaxTempTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.WatchFaceTemperatureTextSizeLarge));
            mMinTempTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.WatchFaceTemperatureTextSizeLarge));
        }

        public void updateUI(@NonNull String timeText, @NonNull DateTime now, @Nullable WeatherForecast weatherForecast) {
            mTimeTextView.setText(timeText);
            mDateTextView.setText(mDateFormatter.print(now).toUpperCase(Locale.ENGLISH));

            if(weatherForecast == null) {
                mForecastContainer.setVisibility(GONE);
                return;
            }

            mForecastContainer.setVisibility(VISIBLE);
            mForecastIcon.setImageResource(WeatherUtils.getIconResourceForWeatherCondition(weatherForecast.getWeatherId()));
            mMaxTempTextView.setText(getContext().getString(R.string.format_temperature, weatherForecast.getMaxTemperature()));
            mMinTempTextView.setText(getContext().getString(R.string.format_temperature, weatherForecast.getMinTemperature()));
        }
    }
    //endregion
}
