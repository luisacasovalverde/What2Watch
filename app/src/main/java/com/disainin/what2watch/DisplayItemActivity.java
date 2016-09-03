package com.disainin.what2watch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonPeople;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class DisplayItemActivity extends AppCompatActivity {

    private List<PersonPeople> personas;
    private String LANG = Common.DEVICE_LANG, POSTER_PATH_ORIGINAL, BACKDROP_PATH_ORIGINAL, BASE_PATH_IMG = null;
    private final int IMG_WIDTH = 500;
    private ImageView header_img;
    private RelativeLayout header_box;
    private NestedScrollView display_item_nested;
    private CoordinatorLayout coordinator;
    private CollapsingToolbarLayout collapsing;
    private AppBarLayout appbar;
    private TextView title, overview, vote_avg, release_date;
    private Toolbar toolbar;
    private boolean INCOMMING_SEARCH;
    private RecyclerView display_item_recyclerview_people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        if (!Utility.isNetworkConnected(getApplicationContext())) {
            finish();
        } else {


            try {
                BASE_PATH_IMG = Utility.getConfigurationTMDBAPI(getApplicationContext()).getSecureBaseUrl();
            } catch (NullPointerException e) {
                if (BASE_PATH_IMG == null) {
                    //AQUI PONER UNA IMAGEN PLACEHOLDER EN LA CABECERA
                }
            }

            toolbar = (Toolbar) findViewById(R.id.toolbar_display_item);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Intent openingMovie = getIntent();
            Bundle bundle = openingMovie.getExtras();

            INCOMMING_SEARCH = bundle.getBoolean("display_item_search");

            POSTER_PATH_ORIGINAL = bundle.getString("display_item_poster_path");
            BACKDROP_PATH_ORIGINAL = bundle.getString("display_item_backdrop_path");


            new ItemImagesTaskTMDBAPI(bundle.getInt("display_item_type")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle.getInt("display_item_id"));
            new ItemTaskTMDBAPI(bundle.getInt("display_item_type")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle.getInt("display_item_id"));

            loadViewsToolbar();
            loadActionsToolbar();

            loadViews();
        }

    }

    private void setColorType(int type) {
        switch (type) {
            case 0:
                header_box.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_display_item_movie));
                break;
            case 1:
                header_box.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_display_item_person));
                break;
            case 2:
                header_box.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_display_item_tvserie));
                break;
            default:
                header_box.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_display_item_movie));
        }
    }

    private void fadeInActivity() {
        toolbar.setBackgroundResource(R.drawable.bg_toolbar_display_item);
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
        appbar.setExpanded(true);
    }

    private void loadActionsToolbar() {
/*        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (collapsing.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsing)) {
                    toolbar.setBackgroundColor(Color.argb(0, 0, 0, 0));
                } else {
                    toolbar.setBackgroundResource(R.drawable.bg_toolbar_display_item);
                }
            }
        });*/
    }

    private void loadViews() {
        header_box = (RelativeLayout) findViewById(R.id.display_item_box_header);

        title = (TextView) findViewById(R.id.display_item_title);
        overview = (TextView) findViewById(R.id.display_item_overview);
        vote_avg = (TextView) findViewById(R.id.display_item_vote_avg);
        release_date = (TextView) findViewById(R.id.display_item_release_date);

        display_item_nested = (NestedScrollView) findViewById(R.id.display_item_nested);
        display_item_nested.setVisibility(View.GONE);

        display_item_recyclerview_people = (RecyclerView) findViewById(R.id.display_item_people);

        display_item_recyclerview_people.setHasFixedSize(true);
        display_item_recyclerview_people.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//        display_item_recyclerview_people.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        display_item_recyclerview_people.setItemAnimator(new DefaultItemAnimator());
    }


    private class ItemImagesTaskTMDBAPI extends AsyncTask<Integer, Integer, List<Artwork>> {
        int type;

        public ItemImagesTaskTMDBAPI(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
        }

        protected List<Artwork> doInBackground(Integer... code) {
            switch (type) {
                case 0:
                    TmdbMovies movies = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getMovies();
                    MovieImages imgs = movies.getImages(code[0], null);

                    return imgs.getBackdrops();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Artwork> imgs) {

            String path;

            if (imgs != null && imgs.size() > 0) {
                path = imgs.get(new Random().nextInt(imgs.size())).getFilePath();
            } else {
                path = POSTER_PATH_ORIGINAL;

                if (type == 2 && BACKDROP_PATH_ORIGINAL != null) {
                    path = BACKDROP_PATH_ORIGINAL;
                }
            }

            while (BASE_PATH_IMG.equals("")) {

            }

            Picasso.with(getApplicationContext()).load(BASE_PATH_IMG + "w" + IMG_WIDTH + path).into(header_img, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
//                    fadeInActivity();
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    private class ItemTaskTMDBAPI extends AsyncTask<Integer, Integer, Multi> {

        private int type;
        private float DATA_VOTE_AVG;
        private String DATA_TITLE, DATA_OVERVIEW, DATA_RELEASE_DATE;
        private List<PersonCast> LIST_CAST;


        public ItemTaskTMDBAPI(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
        }

        protected Multi doInBackground(Integer... code) {
            switch (type) {
                case 0:
                    TmdbMovies movies = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getMovies();
                    MovieDb movie = movies.getMovie(code[0], LANG);

                    setDATA_TITLE(movie.getTitle());
                    if (movie.getOverview() == null || movie.getOverview().equals("")) {
                        setDATA_OVERVIEW(getString(R.string.warning_no_lang_overview));
                    } else {
                        setDATA_OVERVIEW(movie.getOverview());
                    }
                    setDATA_RELEASE_DATE(movie.getReleaseDate().substring(0, 4));
                    setDATA_VOTE_AVG(movie.getVoteAverage());
                    setLIST_CAST(movies.getCredits(code[0]).getCast());

                    return movie;
                case 1:
                    TmdbPeople people = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getPeople();
                    PersonPeople person = people.getPersonInfo(code[0], LANG);

                    setDATA_TITLE(person.getName());
                    setDATA_OVERVIEW("");
//                    if (person.getBiography() == null || person.getBiography().equals("")) {
//                        setDATA_OVERVIEW(getString(R.string.warning_no_lang_overview));
//                    } else {
//                        setDATA_OVERVIEW(person.getBiography());
//                    }

                    setDATA_RELEASE_DATE("");
                    setDATA_VOTE_AVG(-1);

                    return person;
                case 2:
                    TmdbTV series = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getTvSeries();
                    TvSeries serie = series.getSeries(code[0], LANG);

                    setDATA_TITLE(serie.getName());

                    if (serie.getOverview() == null || serie.getOverview().equals("")) {
                        setDATA_OVERVIEW(getString(R.string.warning_no_lang_overview));
                    } else {
                        setDATA_OVERVIEW(serie.getOverview());
                    }

                    setDATA_RELEASE_DATE(serie.getFirstAirDate().substring(0, 4));
                    setDATA_VOTE_AVG(serie.getVoteAverage());
                    setLIST_CAST(series.getCredits(code[0], LANG).getCast());

                    return serie;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Multi item) {
            if (item != null) {
                setColorType(type);

                title.setText(getDATA_TITLE());
                overview.setText(getDATA_OVERVIEW());
                vote_avg.setText(setIfScoreInt(getDATA_VOTE_AVG()));
                release_date.setText(getDATA_RELEASE_DATE());

//                if (type == 1) {
//                    SimpleDateFormat to = new SimpleDateFormat("dd/MM/yyyy");
//                    SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd");
//
//                    try {
//                        release_date.setText(to.format(from.parse(getDATA_RELEASE_DATE())));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    release_date.setText(getDATA_RELEASE_DATE().substring(0, 4));
//                }


                if (type != 1) {
                    CastAdapterTMDBAPI mAdapter = new CastAdapterTMDBAPI(getLIST_CAST(), DisplayItemActivity.this);
                    display_item_recyclerview_people.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }


                fadeInActivity();
            }
        }

        private String setIfScoreInt(float n) {
            if (n == (int) n) {
                if (n < 0) {
                    return "";
                }

                return String.format(Locale.FRANCE, "%.0f", n);
            }

            return String.format(Locale.FRANCE, "%.1f", n);
        }

        public List<PersonCast> getLIST_CAST() {
            return LIST_CAST;
        }

        public void setLIST_CAST(List<PersonCast> LIST_CAST) {
            this.LIST_CAST = LIST_CAST;
        }

        public float getDATA_VOTE_AVG() {
            return DATA_VOTE_AVG;
        }

        public void setDATA_VOTE_AVG(float DATA_VOTE_AVG) {
            this.DATA_VOTE_AVG = DATA_VOTE_AVG;
        }

        public String getDATA_TITLE() {
            return DATA_TITLE;
        }

        public void setDATA_TITLE(String DATA_TITLE) {
            this.DATA_TITLE = DATA_TITLE;
        }

        public String getDATA_OVERVIEW() {
            return DATA_OVERVIEW;
        }

        public void setDATA_OVERVIEW(String DATA_OVERVIEW) {
            this.DATA_OVERVIEW = DATA_OVERVIEW;
        }

        public String getDATA_RELEASE_DATE() {
            return DATA_RELEASE_DATE;
        }

        public void setDATA_RELEASE_DATE(String DATA_RELEASE_DATE) {
            this.DATA_RELEASE_DATE = DATA_RELEASE_DATE;
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (INCOMMING_SEARCH) {
            menu.findItem(R.id.action_search).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_display_item_menu, menu);
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
