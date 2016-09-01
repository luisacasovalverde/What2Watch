package com.disainin.what2watch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class MultiAdapter extends RecyclerView.Adapter<MultiAdapter.MyViewHolder> {

    private List<Multi> items;
    private AppCompatActivity activity;
    private String URL_DIMENSION_IMG = "w150_and_h225_bestv2", URL_BASE = "https://image.tmdb.org/t/p/";


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView multi_item_img;
        private TextView multi_item_title, multi_item_date;
//        private RelativeLayout layout_item_recyclerview;

        public MyViewHolder(View view) {
            super(view);

            multi_item_img = (ImageView) view.findViewById(R.id.multi_item_img);
            multi_item_title = (TextView) view.findViewById(R.id.multi_item_top);
            multi_item_date = (TextView) view.findViewById(R.id.multi_item_bottom);
//            layout_item_recyclerview = (RelativeLayout) view.findViewById(R.id.layout_item_recyclerview);
        }
    }


    public MultiAdapter(List<Multi> items, AppCompatActivity activity) {
        this.items = items;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multi_item_layout, parent, false);


        return new MyViewHolder(itemView);
    }

/*    private class CastTaskTMDBAPI extends AsyncTask<Integer, Integer, String> {

        private MyViewHolder holder;

        public CastTaskTMDBAPI(MyViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            TmdbMovies items = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getMovies();
            MovieDb movie = items.getMovie(integers[0], "es");
            return movie.getCredits().getCast().get(0).getName();
        }

        @Override
        protected void onPostExecute(String person) {
            getHolder().multi_item_middletext.append(", " + person);
//            for (int i = 0; i < person.size(); i++) {
//                getHolder().multi_item_middletext.append(", " + person.get(i).getName());
//                if (i == 1) {
//                    break;
//                }
//            }
        }

        public MyViewHolder getHolder() {
            return holder;
        }
    }*/


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String POSTER_PATH = "";

        final Multi item = items.get(position);

        //PARA QUITAR LAS IMAGENES EN CACHE AÑADIR ESTO ANTES DE .into -> .memoryPolicy(MemoryPolicy.NO_CACHE)

        switch (item.getMediaType().ordinal()) {
            case 0:
                MovieDb movie = (MovieDb) item;
                POSTER_PATH = movie.getPosterPath();

                holder.multi_item_title.setText(movie.getTitle());
                holder.multi_item_date.setText(movie.getReleaseDate().substring(0, 4));

                break;
//                    case 1:
//                        Person person = (Person) items;
//                        break;
            case 2:
                TvSeries serie = (TvSeries) item;
                POSTER_PATH = serie.getPosterPath();

                holder.multi_item_title.setText(serie.getName());
                holder.multi_item_date.setText(serie.getFirstAirDate().substring(0, 4));

                break;
        }

        Picasso.with(holder.multi_item_img.getContext()).load(URL_BASE + URL_DIMENSION_IMG + POSTER_PATH).transform(new Transformation() {

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
        });


        holder.multi_item_img.setOnClickListener(new View.OnClickListener() {
            int CODE;
            String POSTER_PATH, BACKDROP_PATH;

            @Override
            public void onClick(View view) {
                switch (item.getMediaType().ordinal()) {
                    case 0:
                        MovieDb movie = (MovieDb) item;
                        CODE = movie.getId();
                        POSTER_PATH = movie.getPosterPath();
                        BACKDROP_PATH = movie.getBackdropPath();
                        break;
//                    case 1:
//                        Person person = (Person) items;
//                        break;
                    case 2:
                        TvSeries serie = (TvSeries) item;
                        CODE = serie.getId();
                        POSTER_PATH = serie.getPosterPath();
                        BACKDROP_PATH = serie.getBackdropPath();
                        break;
                }

                Intent openMovieData = new Intent(activity.getApplicationContext(), DisplayItemActivity.class);
                openMovieData.putExtra("display_item_id", CODE);
                openMovieData.putExtra("display_item_poster_path", POSTER_PATH);
                openMovieData.putExtra("display_item_backdrop_path", BACKDROP_PATH);
                openMovieData.putExtra("display_item_type", item.getMediaType().ordinal());
                activity.startActivity(openMovieData);
                activity.overridePendingTransition(R.animator.pull_right, R.animator.push_left);
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