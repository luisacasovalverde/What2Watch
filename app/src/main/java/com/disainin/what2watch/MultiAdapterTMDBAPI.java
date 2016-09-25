package com.disainin.what2watch;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.disainin.what2watch.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.PersonPeople;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class MultiAdapterTMDBAPI extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List items;
    private AppCompatActivity activity;
    private String URL_DIMENSION_IMG = "w300_and_h450_bestv2", URL_BASE = "https://image.tmdb.org/t/p/";
    private static final int TYPE_ITEM = 0,
            TYPE_FOOTER_LOADER = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean loading;

    private class MultiItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView multi_item_img;
        private TextView multi_item_top, multi_item_bottom;
        private RelativeLayout multi_item_layout;

        public MultiItemViewHolder(View view) {
            super(view);

            multi_item_layout = (RelativeLayout) view.findViewById(R.id.multi_item_layout);
            multi_item_img = (ImageView) view.findViewById(R.id.multi_item_img);
            multi_item_top = (TextView) view.findViewById(R.id.multi_item_top);
            multi_item_bottom = (TextView) view.findViewById(R.id.multi_item_bottom);
        }
    }


    private class FooterViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout loading_layout;

        public FooterViewHolder(View view) {
            super(view);

            loading_layout = (RelativeLayout) itemView.findViewById(R.id.multi_item_loading_layout);
        }
    }


    public MultiAdapterTMDBAPI(List items, AppCompatActivity activity) {
        this.items = items;
        this.activity = activity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER_LOADER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_item_loading, parent, false);
            return new FooterViewHolder(itemView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_item_layout, parent, false);
            return new MultiItemViewHolder(itemView);
        }
        return null;


//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_item_layout, parent, false);
//        return new MultiItemViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MultiItemViewHolder/* && items.get(position) != null*/) {
            MultiItemViewHolder holder = (MultiItemViewHolder) viewHolder;
            String IMAGE_PATH = "";
            Integer PLACEHOLDER_PROFILE = R.drawable.ic_placeholder_image_w150_h225;

            final Multi item = (Multi) items.get(position);

            switch (item.getMediaType().ordinal()) {
                case Common.TMDB_CODE_MOVIES:
                    MovieDb movie = (MovieDb) item;
                    IMAGE_PATH = movie.getPosterPath();

                    holder.multi_item_top.setText(movie.getTitle());
                    holder.multi_item_bottom.setText(movie.getReleaseDate().substring(0, 4));

                    break;
                case Common.TMDB_CODE_PEOPLE:
                    PersonPeople person = (PersonPeople) item;
                    IMAGE_PATH = person.getProfilePath();
                    PLACEHOLDER_PROFILE = R.drawable.ic_placeholder_profile_w150_h225;

                    holder.multi_item_top.setText(person.getName());
                    holder.multi_item_bottom.setText("");

                    break;
                case Common.TMDB_CODE_SERIES:
                    TvSeries serie = (TvSeries) item;
                    IMAGE_PATH = serie.getPosterPath();

                    holder.multi_item_top.setText(serie.getName());
                    holder.multi_item_bottom.setText(serie.getFirstAirDate().substring(0, 4));

                    break;
            }

            Utility.getImageRounded(activity.getApplicationContext(), URL_BASE + URL_DIMENSION_IMG + IMAGE_PATH, 2, 0, holder.multi_item_img, PLACEHOLDER_PROFILE);

            holder.multi_item_layout.setOnClickListener(new View.OnClickListener() {
                int CODE;
                String IMG_PRIMARY_PATH, IMG_SECONDARY_PATH;

                @Override
                public void onClick(View view) {
                    try {
                        switch (item.getMediaType().ordinal()) {
                            case Common.TMDB_CODE_MOVIES:
                                MovieDb movie = (MovieDb) item;
                                CODE = movie.getId();
                                IMG_PRIMARY_PATH = movie.getPosterPath();
                                IMG_SECONDARY_PATH = movie.getBackdropPath();
                                break;
                            case Common.TMDB_CODE_PEOPLE:
                                PersonPeople person = (PersonPeople) item;
                                CODE = person.getId();
                                IMG_PRIMARY_PATH = person.getProfilePath();
                                IMG_SECONDARY_PATH = null;
                                break;
                            case Common.TMDB_CODE_SERIES:
                                TvSeries serie = (TvSeries) item;
                                CODE = serie.getId();
                                IMG_PRIMARY_PATH = serie.getPosterPath();
                                IMG_SECONDARY_PATH = serie.getBackdropPath();
                                break;
                        }

                        Intent openItemData = new Intent(activity.getApplicationContext(), DisplayItemActivity.class);
                        openItemData.putExtra("display_item_id", CODE);
                        openItemData.putExtra("display_item_type", item.getMediaType().ordinal());

                        openItemData.putExtra("display_item_poster_path", IMG_PRIMARY_PATH);
                        openItemData.putExtra("display_item_backdrop_path", IMG_SECONDARY_PATH);

                        if (activity instanceof SearchActivity) {
                            openItemData.putExtra("display_item_search", true);
                        } else {
                            openItemData.putExtra("display_item_search", false);
                        }

                        activity.startActivity(openItemData);
                        activity.overridePendingTransition(R.animator.pull_right, R.animator.push_left);
                    } catch (ClassCastException ignored) {

                    }

                }
            });

        }/* else if (viewHolder instanceof FooterViewHolder) {
            final FooterViewHolder holder = (FooterViewHolder) viewHolder;
//            holder.loading_layout.setBackgroundColor(Color.BLACK);
        }*/

        if (position == items.size() - 1) {
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void insert(Multi data) {
        items.add(data);
        notifyItemInserted(items.size());
    }

    public void createLoader() {
        setLoading(true);
        items.add(null);
        notifyItemInserted(items.size() - 1);
    }

    public void removeLoader() {
        items.remove(items.size() - 1);
        notifyItemRemoved(items.size());
        setLoading(false);
    }

//    public void removeAt(int position) {
//        items.remove(position);
//        notifyItemRemoved(position);
////        notifyItemRangeChanged(position, items.size());
//    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? TYPE_FOOTER_LOADER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void setItems(List items) {
        this.items = items;
    }

    public List getItems() {
        return items;
    }

    public boolean isLoading() {
        return loading;
    }

    private void setLoading(boolean loading) {
        this.loading = loading;
    }
}