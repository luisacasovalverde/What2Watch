package com.disainin.what2watch;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

public class DisplayMovieActivity extends AppCompatActivity {


    private String LANG = "es";
    private ImageView header_img;
    private NestedScrollView base_layout;
    private CollapsingToolbarLayout collapsing;
    private AppBarLayout appbar;
    private TextView title;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        if (!Utility.isNetworkConnected(getApplicationContext())) {
            finish();
        } else {
            toolbar = (Toolbar) findViewById(R.id.toolbar_display_item);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Intent openingMovie = getIntent();
            Bundle bundle = openingMovie.getExtras();

            new MovieTaskTMDBAPI().execute(bundle.getInt("movie_id"));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.animator.pull_left, R.animator.push_right);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadViews() {
        header_img = (ImageView) findViewById(R.id.display_item_img);
        title = (TextView) findViewById(R.id.display_movie_title);
        base_layout = (NestedScrollView) findViewById(R.id.display_item_nested);
        base_layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_item_recyclerview_serie));
        collapsing = (CollapsingToolbarLayout) findViewById(R.id.display_item_collapsing);
        collapsing.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_item_recyclerview_serie));
        appbar = (AppBarLayout) findViewById(R.id.display_item_appbar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (collapsing.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsing)) {
                    toolbar.setBackgroundColor(Color.argb(80, 0, 0, 0));
                } else {
                    toolbar.setBackgroundResource(R.drawable.bg_toolbar_display_item);
                }
            }
        });

//        toolbar.setBackgroundColor(Color.argb(80, 0, 0, 0));
    }


    private class MovieTaskTMDBAPI extends AsyncTask<Integer, Integer, MovieDb> {

        @Override
        protected void onPreExecute() {
        }

        protected MovieDb doInBackground(Integer... code) {
            TmdbMovies movies = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getMovies();
            return movies.getMovie(code[0], LANG);
        }

        @Override
        protected void onPostExecute(MovieDb movie) {
            loadViews();

            if (movie != null) {
                Picasso.with(getApplicationContext()).load("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + movie.getPosterPath()).into(header_img);
                title.setText(movie.getTitle());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, BuscarActivity.class));
                this.overridePendingTransition(R.animator.pull_right, R.animator.push_left);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
