package io.github.marcelbraghetto.sunshinewatch.features.forecast.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.sunshinewatch.R;
import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherUtils;
import io.github.marcelbraghetto.sunshinewatch.features.application.MainApp;
import io.github.marcelbraghetto.sunshinewatch.framework.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;

import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_DATE;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_MAX_TEMP;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_MIN_TEMP;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_WEATHER_ID;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private final Context mContext;
    private final StringsProvider mStringsProvider;
    private final View mEmptyView;
    private final WeatherForecast mForecast;
    private final Map<String, Integer> mColumnMap;

    private Cursor mCursor;

    public ForecastAdapter(@NonNull Context context, @NonNull View emptyView) {
        mContext = context;
        mEmptyView = emptyView;

        mColumnMap = new HashMap<>();
        mColumnMap.put(COLUMN_DATE, 1);
        mColumnMap.put(COLUMN_WEATHER_ID, 3);
        mColumnMap.put(COLUMN_MIN_TEMP, 4);
        mColumnMap.put(COLUMN_MAX_TEMP, 5);

        mForecast = new WeatherForecast();

        mStringsProvider = MainApp.getInjector().getStringsProvider();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId = -1;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        mForecast.populateFromCursor(mCursor, mColumnMap);

        int weatherId = mForecast.getWeatherId();
        int defaultImage;
        boolean useLongToday;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_TODAY:
                defaultImage = WeatherUtils.getArtResourceForWeatherCondition(weatherId);
                useLongToday = true;
                break;
            default:
                defaultImage = WeatherUtils.getIconResourceForWeatherCondition(weatherId);
                useLongToday = false;
        }

        viewHolder.iconImageView.setImageResource(defaultImage);
        viewHolder.dateTextView.setText(WeatherUtils.getFriendlyDayString(mContext, mForecast.getDate(), useLongToday));
        viewHolder.descriptionTextView.setText(WeatherUtils.getStringForWeatherCondition(mContext, weatherId));
        viewHolder.maxTemperatureTextView.setText(mStringsProvider.getString(R.string.format_temperature, mForecast.getMaxTemperature()));
        viewHolder.minTemperatureTextView.setText(mStringsProvider.getString(R.string.format_temperature, mForecast.getMinTemperature()));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.list_item_icon) ImageView iconImageView;
        @Bind(R.id.list_item_date_textview) TextView dateTextView;
        @Bind(R.id.list_item_forecast_textview) TextView descriptionTextView;
        @Bind(R.id.list_item_high_textview) TextView maxTemperatureTextView;
        @Bind(R.id.list_item_low_textview) TextView minTemperatureTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
