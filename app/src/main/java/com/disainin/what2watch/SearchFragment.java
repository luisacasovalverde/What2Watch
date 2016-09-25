package com.disainin.what2watch;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.PersonPeople;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class SearchFragment extends Fragment implements View.OnClickListener {


    private TaskCallbacks mCallbacks;
    private String lastQuery;
    private boolean running = false, firstSearch = true;
    private int totalPagesResults = 1, actualPageResults = 1;
    private RecyclerView search_recyclerview;
    private MultiAdapterTMDBAPI multiAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("RUNNING", isRunning());
        outState.putInt("LAST_PAGE_LOADED", getActualPageResults());
        outState.putInt("TOTAL_PAGES", getTotalPagesResults());
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mCallbacks = (TaskCallbacks) context;
////        if (context instanceof AppCompatActivity) {
////        } else {
////            throw new RuntimeException(context.toString()
////                    + " must implement TaskCallback");
////        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (TaskCallbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            setRunning(savedInstanceState.getBoolean("RUNNING"));
            setActualPageResults(savedInstanceState.getInt("LAST_PAGE_LOADED"));
            setTotalPagesResults(savedInstanceState.getInt("TOTAL_PAGES"));
        }

        multiAdapter = new MultiAdapterTMDBAPI(new ArrayList<>(), (SearchActivity) getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        search_recyclerview.setAdapter(multiAdapter);
    }

    private void loadViewsSearchFragment() {
    }


    public void search(String query) {
        new SearchTaskTMDBAPI().execute(query);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View searchFragmentInflater = inflater.inflate(R.layout.fragment_search, container, false);


        search_recyclerview = (RecyclerView) searchFragmentInflater.findViewById(R.id.fragment_search_recyclerview);
//        search_recyclerview.setBackgroundColor(Color.RED);
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager search_recyclerview_lm = new GridLayoutManager(getActivity(), Utility.getColumnsFromWidth(getContext()));
////        search_recyclerview_lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
////            @Override
////            public int getSpanSize(int position) {
////                switch (getMultiAdapter().getItemViewType(position)) {
////                    case MultiAdapterTMDBAPI.TYPE_FOOTER_LOADER:
////                        if ((getMultiAdapter().getItemCount() - 1) % 2 == 0) {
////                            return Utility.getColumnsFromWidth(getContext());
////                        }
////                    default:
////                        return 1;
////                }
////            }
////        });
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        search_recyclerview.setLayoutManager(search_recyclerview_lm);
        search_recyclerview.setAdapter(getMultiAdapter());


//        if (searchFragmentInflater instanceof RecyclerView) {
//            Context context = searchFragmentInflater.getContext();
//            RecyclerView recyclerView = (RecyclerView) searchFragmentInflater;
//            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
//
//            recyclerView.setAdapter(multiAdapter);
//        }


        return searchFragmentInflater;
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//
//        }
    }

//    private class DummyTask extends AsyncTask<Void, Integer, Void> {
//
//        @Override
//        protected void onPreExecute() {
////            if (mCallbacks != null) {
////                mCallbacks.onPreExecute();
////            }
//            sf_btn_init.setEnabled(false);
//            running = true;
//        }
//
//        /**
//         * Note that we do NOT call the callback object's methods
//         * directly from the background thread, as this could result
//         * in a race condition.
//         */
//        @Override
//        protected Void doInBackground(Void... ignore) {
//            for (int i = 0; !isCancelled() && i < 100; i++) {
//                SystemClock.sleep(100);
//                publishProgress(i);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... percent) {
////            if (mCallbacks != null) {
////                mCallbacks.onProgressUpdate(percent[0]);
////            }
//            sf_pb.setProgress(percent[0]);
//            sf_tv.setText(++percent[0] + " %");
//        }
//
//        @Override
//        protected void onCancelled() {
////            if (mCallbacks != null) {
////                mCallbacks.onCancelled();
////            }
//        }
//
//        @Override
//        protected void onPostExecute(Void ignore) {
////            if (mCallbacks != null) {
////                mCallbacks.onPostExecute();
////            }
////            sf_pb.setProgress(0);
////            sf_tv.setText("0 %");
//            sf_btn_init.setEnabled(true);
//            running = false;
//        }
//    }


    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isFirstSearch() {
        return firstSearch;
    }

    public void setFirstSearch(boolean firstSearch) {
        this.firstSearch = firstSearch;
    }

    public int getActualPageResults() {
        return actualPageResults;
    }

    public void setActualPageResults(int actualPageResults) {
        this.actualPageResults = actualPageResults;
    }

    public int getTotalPagesResults() {
        return totalPagesResults;
    }

    public void setTotalPagesResults(int totalPagesResults) {
        this.totalPagesResults = totalPagesResults;
    }

    public String getLastQuery() {
        return lastQuery;
    }

    public void setLastQuery(String lastQuery) {
        this.lastQuery = lastQuery;
    }

    public MultiAdapterTMDBAPI getMultiAdapter() {
        return multiAdapter;
    }

    public void setMultiAdapter(MultiAdapterTMDBAPI multiAdapter) {
        this.multiAdapter = multiAdapter;
    }

    public RecyclerView getSearch_recyclerview() {
        return search_recyclerview;
    }

    private class SearchTaskTMDBAPI extends AsyncTask<String, Integer, List<Multi>> {

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        protected List<Multi> doInBackground(String... query) {
            try {
                TmdbSearch r = new TmdbApi(Common.TMDB_APIKEY).getSearch();

                TmdbSearch.MultiListResultsPage todo = r.searchMulti(query[0], Common.DEVICE_LANG, getActualPageResults());
                setTotalPagesResults(todo.getTotalPages());

                // ELIMINA DE LA LISTA LOS QUE CUMPLAN LAS CONDICIONES
                Iterator<Multi> i = todo.iterator();
                while (i.hasNext()) {
                    Multi item = i.next();

                    switch (item.getMediaType().ordinal()) {
                        case Common.TMDB_CODE_MOVIES:
                            MovieDb movie = (MovieDb) item;
                            if ((movie.getReleaseDate() == null || movie.getReleaseDate().equals(""))
                                    || (movie.getPosterPath() == null || movie.getPosterPath().equals(""))
                                    || movie.getVoteAverage() == 0) {
                                i.remove();
                            }

                            break;
                        case Common.TMDB_CODE_PEOPLE:
                            PersonPeople person = (PersonPeople) item;
                            if ((person.getName() == null || person.getName().equals(""))) {
                                i.remove();
                            }

                            break;
                        case Common.TMDB_CODE_SERIES:
                            TvSeries serie = (TvSeries) item;
                            if ((serie.getFirstAirDate() == null || serie.getFirstAirDate().equals(""))
                                    || (serie.getPosterPath() == null || serie.getPosterPath().equals(""))
                                    || serie.getVoteAverage() == 0) {
                                i.remove();
                            }

                            break;
                    }
                }

                Log.d("BUCLE", "items: " + todo.getResults().size() + ", pagina: " + getActualPageResults());

                setActualPageResults(getActualPageResults() + 1);

                return todo.getResults();
            } catch (Exception e) {
                return null;
            }
        }


        protected void onPostExecute(List<Multi> results) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(results);
            }
//            ((SearchActivity) getActivity()).setVoiceQueryOFF();
//            RelativeLayout loading_layout = (RelativeLayout) getActivity().findViewById(R.id.search_loading_layout);
//            loading_layout.setVisibility(View.GONE);
//            if (results != null) {
////                Collections.sort(results, new Comparator<Multi>() {
////                    @Override
////                    public int compare(Multi i1, Multi i2) {
////                        return i1.getMediaType().compareTo(i2.getMediaType());
////                    }
////                });
//
//                if (results.size() > 0) {
//                    if (isFirstSearch()) {
//                        setFirstSearch(false);
//                    } else {
//                        getMultiAdapter().removeLoader();
//                    }
//
//                    for (Multi m : results) {
//                        multiAdapter.insert(m);
//                    }
//
////                    if (!isNoMoreResults()) {
////                        getMultiAdapter().createLoader();
////                    }
//                } else {
//                    if (isFirstSearch()) {
//                    }
//                }
//            } else {
//            }
        }
    }


    interface TaskCallbacks {
        void onPreExecute();

//        void onProgressUpdate(int percent);
//
//        void onCancelled();

        void onPostExecute(List<Multi> results);
    }
}


