package io.github.marcelbraghetto.sunshinewatch.features.forecast.logic;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.github.marcelbraghetto.sunshinewatch.framework.core.BasePresenter;
import io.github.marcelbraghetto.sunshinewatch.weather.contracts.WeatherProvider;

/**
 * Created by Marcel Braghetto on 16/04/16.
 *
 * Presentation logic for configuring the display of the weather forecast starting from today.
 */
public class ForecastPresenter extends BasePresenter<ForecastPresenter.Delegate> {
    //region Private fields
    private final WeatherProvider mWeatherProvider;
    //endregion

    //region Public methods
    @Inject
    public ForecastPresenter(@NonNull WeatherProvider weatherProvider) {
        super(Delegate.class);
        mWeatherProvider = weatherProvider;
    }

    @Override
    public void connect(@Nullable Delegate delegate) {
        super.connect(delegate);
        mDelegate.setDataSource(mWeatherProvider.getWeatherForecastUri());
    }
    //endregion

    //region Controller delegate
    public interface Delegate {
        /**
         * Connect the given Uri as a data source to the host loader and adapter.
         * @param dataSourceUri to use as a data source.
         */
        void setDataSource(@NonNull Uri dataSourceUri);
    }
    //endregion
}
