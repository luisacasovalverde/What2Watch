package com.disainin.what2watch;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class MultiAdapter extends RecyclerView.Adapter<MultiAdapter.MyViewHolder> {

    private List<Multi> items;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView multi_item_img;
        private TextView multi_item_headtext, multi_item_middletext, multi_item_score;
        private RelativeLayout layout_item_recyclerview;

        public MyViewHolder(View view) {
            super(view);

            multi_item_img = (ImageView) view.findViewById(R.id.multi_item_img);
            multi_item_headtext = (TextView) view.findViewById(R.id.multi_item_headtext);
            multi_item_middletext = (TextView) view.findViewById(R.id.multi_item_middletext);
            multi_item_score = (TextView) view.findViewById(R.id.multi_item_score);
            layout_item_recyclerview = (RelativeLayout) view.findViewById(R.id.layout_item_recyclerview);
        }
    }


    public MultiAdapter(List<Multi> items) {
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multi_item_layout, parent, false);


        return new MyViewHolder(itemView);
    }

    private class CastTaskTMDBAPI extends AsyncTask<Integer, Integer, String> {

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
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Multi item = items.get(position);

        //PARA QUITAR LAS IMAGENES EN CACHE AÑADIR ESTO ANTES DE .into -> .memoryPolicy(MemoryPolicy.NO_CACHE)

        switch (item.getMediaType().ordinal()) {
            case 0:
                MovieDb movie = (MovieDb) item;

//                holder.layout_item_recyclerview.setBackgroundColor(holder.layout_item_recyclerview.getContext().getResources().getColor(R.color.bg_item_recyclerview_movie));
//                Picasso.with(this.context).cancelRequest(holder.imageView);
                Picasso.with(holder.multi_item_img.getContext()).load("https://image.tmdb.org/t/p/w150_and_h225_bestv2" + movie.getPosterPath()).into(holder.multi_item_img);
                holder.multi_item_headtext.setText(movie.getTitle());
                holder.multi_item_middletext.setText(movie.getReleaseDate().substring(0, 4));
                holder.multi_item_score.setText("" + Math.round(movie.getVoteAverage() * 10.0) / 10.0);
                holder.multi_item_score.setText(String.format(Locale.US, "%.1f", movie.getVoteAverage()));


//                new CastTaskTMDBAPI(holder).execute(movie.getId());


//                final MyViewHolder holder_cast = holder;
//
//                new AsyncTask<Integer, Integer, List<PersonCast>>() {
//
//                    @Override
//                    protected List<PersonCast> doInBackground(Integer... integers) {
//                        TmdbMovies items = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getMovies();
//                        return items.getMovie(integers[0], "es").getCredits().getCast();
//                    }
//
//                    @Override
//                    protected void onPostExecute(List<PersonCast> person) {
//                        for (int i = 0; i < person.size(); i++) {
//                            holder_cast.multi_item_middletext.append(", " + person.get(i).getName());
//                            if (i == 1) {
//                                break;
//                            }
//                        }
//                    }
//
//                }.execute(movie.getId());


//                DatosAdapter cast = new DatosAdapter(DatosAdapter.CAST, DatosAdapter.MOVIE, movie.getId());
//                cast.execute();
//                cast.

//                    holder.multi_item_middletext.append(", (2) - " + cast.get().size());


//                    for (int i = 0; i < cast.size(); i++) {
//                        holder.multi_item_middletext.append(", " + cast.get(i).getName());
//                        if (i == 1) {
//                            break;
//                        }
//                    }


                break;
//                    case 1:
//                        Person person = (Person) items;
//                        break;
            case 2:
                TvSeries serie = (TvSeries) item;

//                holder.layout_item_recyclerview.setBackgroundColor(holder.layout_item_recyclerview.getContext().getResources().getColor(R.color.bg_item_recyclerview_serie));
                Picasso.with(holder.multi_item_img.getContext()).load("https://image.tmdb.org/t/p/w150_and_h225_bestv2" + serie.getPosterPath()).into(holder.multi_item_img);
                holder.multi_item_headtext.setText(serie.getName());
                holder.multi_item_middletext.setText(serie.getFirstAirDate().substring(0, 4));
                holder.multi_item_score.setText(String.format(Locale.US, "%.1f", serie.getVoteAverage()));

                break;
        }

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