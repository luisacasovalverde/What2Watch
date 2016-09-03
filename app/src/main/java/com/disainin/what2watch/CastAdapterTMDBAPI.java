package com.disainin.what2watch;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonPeople;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class CastAdapterTMDBAPI extends RecyclerView.Adapter<CastAdapterTMDBAPI.MyViewHolder> {

    private List<PersonCast> items;
    private AppCompatActivity activity;
    private String URL_DIMENSION_IMG = "w264_and_h264_bestv2", URL_BASE = "https://image.tmdb.org/t/p/";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView multi_item_img;
        private TextView multi_item_top, multi_item_bottom;
        private RelativeLayout multi_item_layout;

        public MyViewHolder(View view) {
            super(view);

            multi_item_img = (ImageView) view.findViewById(R.id.multi_item_img);
            multi_item_top = (TextView) view.findViewById(R.id.multi_item_top);
            multi_item_bottom = (TextView) view.findViewById(R.id.multi_item_bottom);
            multi_item_layout = (RelativeLayout) view.findViewById(R.id.multi_item_layout);


            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((250), RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = 10;

            multi_item_layout.setLayoutParams(lp);
        }
    }


    public CastAdapterTMDBAPI(List items, AppCompatActivity activity) {
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
        final PersonCast person = items.get(position);

        holder.multi_item_top.setText(person.getName());
        holder.multi_item_bottom.setText(person.getCharacter());

        Utility.getImageRounded(activity.getApplicationContext(), URL_BASE + URL_DIMENSION_IMG + person.getProfilePath(), 2, 0, holder.multi_item_img, R.drawable.ic_placeholder_profile_w240_h240);

        holder.multi_item_layout.setOnClickListener(new View.OnClickListener() {
            int CODE;
            String IMG_PRIMARY_PATH, IMG_SECONDARY_PATH;

            @Override
            public void onClick(View view) {
                CODE = person.getId();
                IMG_PRIMARY_PATH = person.getProfilePath();
                IMG_SECONDARY_PATH = null;


                Intent openPersonData = new Intent(activity.getApplicationContext(), DisplayItemActivity.class);
                openPersonData.putExtra("display_item_id", CODE);
                openPersonData.putExtra("display_item_type", 1);

                openPersonData.putExtra("display_item_poster_path", IMG_PRIMARY_PATH);
                openPersonData.putExtra("display_item_backdrop_path", IMG_SECONDARY_PATH);

                openPersonData.putExtra("display_item_search", false);

                activity.startActivity(openPersonData);
                activity.overridePendingTransition(R.animator.pull_right, R.animator.push_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}