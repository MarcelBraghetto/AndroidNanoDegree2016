package io.github.marcelbraghetto.football.features.scores.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract;
import io.github.marcelbraghetto.football.framework.providers.football.models.FootballGame;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 4/12/15.
 *
 * Adapter for displaying games for the given content selection.
 */
public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {
    private final AppStringsProvider mAppStringsProvider;

    private Map<String, Integer> mGameColumnMap;
    private FootballGame mGame;
    private Delegate mDelegate;
    private Cursor mCursor;

    /**
     * Interaction delegate for actions taken
     * within adapter items.
     */
    public interface Delegate {
        /**
         * User selected the 'share' action for
         * a game in the adapter.
         * @param text of the selected game to share.
         */
        void shareSelected(@NonNull String text);
    }

    public ScoresAdapter(@NonNull Delegate delegate) {
        mDelegate = delegate;
        mGame = new FootballGame();
        mGameColumnMap = FootballDatabaseContract.Games.getAllColumnsIndexMap();
        mAppStringsProvider = MainApp.getDagger().getAppStringsProvider();
    }

    @Nullable
    public Cursor swapCursor(@Nullable Cursor cursor) {
        if(mCursor == cursor) {
            return null;
        }

        Cursor oldCursor = mCursor;

        mCursor = cursor;

        if(mCursor != null) {
            notifyDataSetChanged();
        }

        return oldCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scores_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        mGame.populateFromCursor(mCursor, mGameColumnMap);

        holder.shareText = mGame.getShareText();
        holder.homeTextView.setText(mGame.getHomeTeamName());
        holder.awayTextView.setText(mGame.getAwayTeamName());

        if(mGame.hasGoals()) {
            holder.scoreTextView.setText(mAppStringsProvider.getString(R.string.scores_list_item_goals, mGame.getHomeTeamGoals(), mGame.getAwayTeamGoals()));
            holder.scoreTextView.setVisibility(View.VISIBLE);
        } else {
            holder.scoreTextView.setVisibility(View.GONE);
        }

        holder.timeTextView.setText(mGame.getFormattedGameTime());
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.scores_list_item_toolbar) Toolbar toolbar;
        @Bind(R.id.scores_list_item_title_home) TextView homeTextView;
        @Bind(R.id.scores_list_item_title_away) TextView awayTextView;
        @Bind(R.id.scores_list_item_score) TextView scoreTextView;
        @Bind(R.id.scores_list_item_time) TextView timeTextView;

        String shareText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            toolbar.inflateMenu(R.menu.football_scores_list_item);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    boolean handled = true;

                    switch (item.getItemId()) {
                        case R.id.toolbar_action_share:
                            if (mDelegate != null) {
                                mDelegate.shareSelected(shareText);
                            }
                            break;
                        default:
                            handled = false;
                            break;
                    }

                    return handled;
                }
            });
        }
    }
}
