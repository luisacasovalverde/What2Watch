package com.disainin.what2watch;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;

public class DisplayItemActivity extends AppCompatActivity {


    private String LANG = "es", POSTER_PATH_ORIGINAL, BASE_PATH_IMG = null;
    private ImageView header_img;
    private NestedScrollView display_item_nested;
    private CoordinatorLayout coordinator;
    private CollapsingToolbarLayout collapsing;
    private AppBarLayout appbar;
    private TextView title, overview;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        if (!Utility.isNetworkConnected(getApplicationContext())) {
            finish();
        } else {

            BASE_PATH_IMG = Utility.getConfigurationTMDBAPI(getApplicationContext()).getSecureBaseUrl();
            if (BASE_PATH_IMG == null) {
                //AQUI PONER UNA IMAGEN PLACEHOLDER EN LA CABECERA
            }

            toolbar = (Toolbar) findViewById(R.id.toolbar_display_item);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Intent openingMovie = getIntent();
            Bundle bundle = openingMovie.getExtras();
            POSTER_PATH_ORIGINAL = bundle.getString("display_item_poster_path");


            new MovieImagesTaskTMDBAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle.getInt("display_item_id"));
            new MovieTaskTMDBAPI().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle.getInt("display_item_id"));

            loadViewsToolbar();
            loadActionsToolbar();

            loadViews();
        }

    }

    private void fadeInActivity() {
        display_item_nested.setVisibility(View.VISIBLE);
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

    private void loadViewsToolbar() {
        header_img = (ImageView) findViewById(R.id.display_item_img);
        collapsing = (CollapsingToolbarLayout) findViewById(R.id.display_item_collapsing);
//        collapsing.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_item_recyclerview_serie));
        appbar = (AppBarLayout) findViewById(R.id.display_item_appbar);
    }

    private void loadActionsToolbar() {
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
    }

    private void loadViews() {
        title = (TextView) findViewById(R.id.display_movie_title);
        overview = (TextView) findViewById(R.id.display_movie_overview);
        display_item_nested = (NestedScrollView) findViewById(R.id.display_item_nested);
        display_item_nested.setVisibility(View.GONE);
    }


    private class MovieImagesTaskTMDBAPI extends AsyncTask<Integer, Integer, List<Artwork>> {

        @Override
        protected void onPreExecute() {
        }

        protected List<Artwork> doInBackground(Integer... code) {
            TmdbMovies movies = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getMovies();
            MovieImages movie_imgs = movies.getImages(code[0], null);

            return movie_imgs.getBackdrops();
        }

        @Override
        protected void onPostExecute(List<Artwork> imgs) {

            String path;

            if (imgs != null && imgs.size() > 0) {
                path = imgs.get(new Random().nextInt(imgs.size())).getFilePath();
            } else {
                path = POSTER_PATH_ORIGINAL;
            }

            while (BASE_PATH_IMG.equals("")) {

            }

            Picasso.with(getApplicationContext()).load(BASE_PATH_IMG + "w600" + path).into(header_img, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    fadeInActivity();
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    private class MovieTaskTMDBAPI extends AsyncTask<Integer, Integer, MovieDb> {

        @Override
        protected void onPreExecute() {
        }

        protected MovieDb doInBackground(Integer... code) {
            TmdbMovies movies = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getMovies();
            MovieDb movie = movies.getMovie(code[0], LANG);

            return movie;
        }

        @Override
        protected void onPostExecute(MovieDb movie) {
            if (movie != null) {
                title.setText(movie.getTitle());
                overview.setText(movie.getOverview());
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
