package com.disainin.what2watch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.PersonPeople;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class MultiAdapterTMDBAPI extends RecyclerView.Adapter<MultiAdapterTMDBAPI.MyViewHolder> {

    private List<Multi> items;
    private AppCompatActivity activity;
    private String URL_DIMENSION_IMG = "w300_and_h450_bestv2", URL_BASE = "https://image.tmdb.org/t/p/";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView multi_item_img;
        private TextView multi_item_top, multi_item_bottom;
        private RelativeLayout multi_item_layout;

        public MyViewHolder(View view) {
            super(view);

            multi_item_layout = (RelativeLayout) view.findViewById(R.id.multi_item_layout);
            multi_item_img = (ImageView) view.findViewById(R.id.multi_item_img);
            multi_item_top = (TextView) view.findViewById(R.id.multi_item_top);
            multi_item_bottom = (TextView) view.findViewById(R.id.multi_item_bottom);
        }
    }


    public MultiAdapterTMDBAPI(List items, AppCompatActivity activity) {
        this.items = items;
        this.activity = activity;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multi_item_layout, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String POSTER_PATH = "";
        Integer PLACEHOLDER_PROFILE = R.drawable.ic_placeholder_image_w150_h225;

        final Multi item = items.get(position);

        switch (item.getMediaType().ordinal()) {
            case 0:
                MovieDb movie = (MovieDb) item;
                POSTER_PATH = movie.getPosterPath();

                holder.multi_item_top.setText(movie.getTitle());
                holder.multi_item_bottom.setText(movie.getReleaseDate().substring(0, 4));

                break;
            case 1:
                PersonPeople person = (PersonPeople) item;
                POSTER_PATH = person.getProfilePath();
                PLACEHOLDER_PROFILE = R.drawable.ic_placeholder_profile_w150_h225;

                holder.multi_item_top.setText(person.getName());
                holder.multi_item_bottom.setText("");

                break;
            case 2:
                TvSeries serie = (TvSeries) item;
                POSTER_PATH = serie.getPosterPath();

                holder.multi_item_top.setText(serie.getName());
                holder.multi_item_bottom.setText(serie.getFirstAirDate().substring(0, 4));

                break;
        }

        Utility.getImageRounded(activity.getApplicationContext(), URL_BASE + URL_DIMENSION_IMG + POSTER_PATH, 2, 0, holder.multi_item_img, PLACEHOLDER_PROFILE);


/*        Picasso.with(holder.multi_item_img.getContext()).load(URL_BASE + URL_DIMENSION_IMG + POSTER_PATH).transform(new Transformation() {

            private final int radius = 2;
            private final int margin = 0;

            @Override
            public Bitmap transform(final Bitmap source) {
                final Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

                Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

                if (source != output) {
                    source.recycle();
                }

                return output;
            }

            @Override
            public String key() {
                return "rounded";
            }
        }).into(holder.multi_item_img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
//                holder.multi_item_img.setBackgroundResource(R.drawable.rounded_multi_item_img);
//                holder.multi_item_img.setBackgroundColor(Color.argb(20, 0, 0, 0));
            }

            @Override
            public void onError() {

            }
        });*/


        holder.multi_item_layout.setOnClickListener(new View.OnClickListener() {
            int CODE;
            String IMG_PRIMARY_PATH, IMG_SECONDARY_PATH;

            @Override
            public void onClick(View view) {
                try {
                    switch (item.getMediaType().ordinal()) {
                        case 0:
                            MovieDb movie = (MovieDb) item;
                            CODE = movie.getId();
                            IMG_PRIMARY_PATH = movie.getPosterPath();
                            IMG_SECONDARY_PATH = movie.getBackdropPath();
                            break;
                        case 1:
                            PersonPeople person = (PersonPeople) item;
                            CODE = person.getId();
                            IMG_PRIMARY_PATH = person.getProfilePath();
                            IMG_SECONDARY_PATH = null;
                            break;
                        case 2:
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

                    if (activity instanceof BuscarActivity) {
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


//        notifyDataSetChanged();

    }

    public void removeAt(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}