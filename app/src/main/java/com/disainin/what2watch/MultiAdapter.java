package com.disainin.what2watch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
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

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Multi item = items.get(position);

        switch (item.getMediaType().ordinal()) {
            case 0:
                MovieDb movie = (MovieDb) item;

//                holder.layout_item_recyclerview.setBackgroundColor(holder.layout_item_recyclerview.getContext().getResources().getColor(R.color.bg_item_recyclerview_movie));
//                Picasso.with(this.context).cancelRequest(holder.imageView);
                Picasso.with(holder.multi_item_img.getContext()).load("https://image.tmdb.org/t/p/w300_and_h450_bestv2" + movie.getPosterPath()).into(holder.multi_item_img);
                holder.multi_item_headtext.setText(movie.getTitle());
                holder.multi_item_middletext.setText(movie.getReleaseDate().substring(0, 4));
                holder.multi_item_score.setText("" + movie.getVoteAverage());


                break;
//                    case 1:
//                        Person person = (Person) items;
//                        break;
            case 2:
                TvSeries serie = (TvSeries) item;

//                holder.layout_item_recyclerview.setBackgroundColor(holder.layout_item_recyclerview.getContext().getResources().getColor(R.color.bg_item_recyclerview_serie));
                Picasso.with(holder.multi_item_img.getContext()).load("https://image.tmdb.org/t/p/w300_and_h450_bestv2" + serie.getPosterPath()).into(holder.multi_item_img);
                holder.multi_item_headtext.setText(serie.getName());
                holder.multi_item_middletext.setText(serie.getFirstAirDate().substring(0, 4));
                holder.multi_item_score.setText("" + serie.getVoteAverage());

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