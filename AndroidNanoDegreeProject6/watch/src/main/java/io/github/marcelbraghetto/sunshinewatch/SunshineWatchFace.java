package io.github.marcelbraghetto.sunshinewatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 *
 * http://developer.android.com/training/wearables/apps/creating.html
 *
 * adb -d forward tcp:5601 tcp:5601
 */
public class SunshineWatchFace extends CanvasWatchFaceService {
    /* package */ static final String DATA_UPDATE_EVENT_ID = "DataUpdateEvent";

    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<SunshineWatchFace.Engine> mWeakReference;

        public EngineHandler(SunshineWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SunshineWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mAmbient;
        WeatherForecast mWeatherForecast;
        SunshineWatchFaceView mWatchFaceView;

        final BroadcastReceiver mDataUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WeatherForecast weatherForecast = WeatherForecast.getFrom(intent);
                if(weatherForecast != null) {
                    mWeatherForecast = weatherForecast;
                    invalidate();
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SunshineWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            mWatchFaceView = new SunshineWatchFaceView(SunshineWatchFace.this);

            LocalBroadcastManager
                    .getInstance(SunshineWatchFace.this)
                    .registerReceiver(mDataUpdatedReceiver, new IntentFilter(DATA_UPDATE_EVENT_ID));

            requestWeatherForecastData();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);

            LocalBroadcastManager
                    .getInstance(SunshineWatchFace.this)
                    .unregisterReceiver(mDataUpdatedReceiver);

            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            updateTimer();
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            mWatchFaceView.updateMeasureSpec();
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                invalidate();
            }

            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mWatchFaceView.setWeatherForecast(mWeatherForecast);
            mWatchFaceView.render(canvas, mAmbient);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        /**
         * Send a message to the phone to request weather forecast data.
         */
        private void requestWeatherForecastData() {
            new RequestWeatherForecastDataTask(getApplicationContext()).execute();
        }
    }

    /**
     * Task used for requesting the host phone device to fetch weather forecast data.
     */
    private static class RequestWeatherForecastDataTask extends AsyncTask<Void, Void, Void> {
        private final Context mApplicationContext;

        public RequestWeatherForecastDataTask(@NonNull Context applicationContext) {
            mApplicationContext = applicationContext;
        }

        @Override
        protected Void doInBackground(Void... params) {
            GoogleApiClient client = new GoogleApiClient.Builder(mApplicationContext)
                    .addApi(Wearable.API)
                    .build();

            client.blockingConnect(5, TimeUnit.SECONDS);

            if(!client.isConnected()) {
                return null;
            }

            // Attempt to find our paired phone node.
            NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(client).await(5, TimeUnit.SECONDS);
            Node appNode = null;
            for(Node node : nodesResult.getNodes()) {
                if(node.isNearby()) {
                    appNode = node;
                    break;
                }
            }

            // If we found a node that identifies itself as nearby then use it to send a message
            // requesting a data refresh - which should be picked up by the app service to trigger
            // a sync adapter refresh. In this situation we are doing a fire and forget - don't
            // care whether it succeeded or not.
            if(appNode != null) {
                String path = mApplicationContext.getString(R.string.wear_path_request_data_refresh);
                Wearable.MessageApi.sendMessage(client, appNode.getId(), path, null);
            }

            client.disconnect();

            return null;
        }
    }
}
