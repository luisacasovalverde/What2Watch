package com.disainin.what2watch;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.disainin.what2watch.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.Multi;

public class SearchActivity extends AppCompatActivity implements RecognitionListener, SearchFragment.TaskCallbacks {


    private ValueAnimator colorTransitionVoiceQuery;
    private RelativeLayout search_activity_layout, search_loading_layout;
    private EditText text_query;
    private TextView txt_queryvoice;
    private ImageButton btn_mic, btn_back, btn_clear;
    private SpeechRecognizer sr;
    private final int[] infoText = new int[]{
            R.string.search_voice_empty, //0
            R.string.search_voice_listening, //1
            R.string.search_voice_recognizing, //2
    };
    private RelativeLayout search_recyclerview;
    //    private MultiAdapterTMDBAPI multiAdapter;
//    private int totalPagesResults = 1, actualPageResults;
    private boolean clear = true, firstSearch = true, voiceQueryRunning = false, loadingRunning = false;
    private String lastQuery = "";


    private static final String TAG_SEARCH_FRAGMENT = "fragment_search_task";
    private SearchFragment fragmentSearchTask;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState != null) {
            setVoiceQueryRunning(savedInstanceState.getBoolean("VOICE_QUERY_RUNNING"));
            setLoadingRunning(savedInstanceState.getBoolean("LOADING_RUNNING"));
            if (!isLoadingRunning()) {
//                setActualPageResults(savedInstanceState.getInt("LAST_PAGE_LOADED"));
//                fragmentSearchTask.setActualPageResults(getActualPageResults());
//                setTotalPagesResults(savedInstanceState.getInt("TOTAL_PAGES"));
//                fragmentSearchTask.setTotalPagesResults(getTotalPagesResults());
                setFirstSearch(savedInstanceState.getBoolean("FIRST_SEARCH"));
                setLastQuery(savedInstanceState.getString("LAST_QUERY"));
            }
        }


        fm = getSupportFragmentManager();
        fragmentSearchTask = (SearchFragment) fm.findFragmentByTag(TAG_SEARCH_FRAGMENT);

        if (fragmentSearchTask == null) {
            fragmentSearchTask = new SearchFragment();
            fm.beginTransaction().add(R.id.include_search_recyclerview, fragmentSearchTask, TAG_SEARCH_FRAGMENT).commit();
//            fm.beginTransaction().add(fragmentSearchTask, TAG_SEARCH_FRAGMENT).commit();
        }

        loadViews();
        loadActions();

        if (isVoiceQueryRunning()) {
            setVoiceQueryON();
        }

        if (isLoadingRunning() && isFirstSearch()) {
            search_loading_layout.setVisibility(View.VISIBLE);
        }

//        if (getLastCustomNonConfigurationInstance() != null) {
//            if (!isLoadingRunning()) {
//                multiAdapter.setItems((List) getLastCustomNonConfigurationInstance());
//            }
//        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt("LAST_PAGE_LOADED", getActualPageResults());
//        outState.putInt("TOTAL_PAGES", getTotalPagesResults());
        outState.putBoolean("FIRST_SEARCH", isFirstSearch());
        outState.putBoolean("VOICE_QUERY_RUNNING", isVoiceQueryRunning());
        outState.putBoolean("LOADING_RUNNING", isLoadingRunning());
        outState.putString("LAST_QUERY", getLastQuery());
    }

    private void verifyPermission(String permission_type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int writePermission = checkSelfPermission(permission_type);

            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermission(permission_type);
            }   // else -> accion en caso de que ya tenga los permisos
        }
    }

    private void requestPermission(String permission_type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission_type)) {
                Utility.showRequestPermissionAction(getApplicationContext(), search_activity_layout, R.string.permission_record_audio);
            } else {
                requestPermissions(new String[]{permission_type}, Common.PERMISSION_RECORD_AUDIO);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Common.PERMISSION_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //ESTO CAMBIARLO POR UNA FUNCION SI FUERA NECESARIO
                Toast.makeText(SearchActivity.this, "PERMITIDO RECORD AUDIO", Toast.LENGTH_SHORT).show();
            } else {
                //ESTO CAMBIARLO POR UNA FUNCION SI FUERA NECESARIO
                Toast.makeText(SearchActivity.this, "PROHIBIDO RECORD AUDIO", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public List onRetainCustomNonConfigurationInstance() {
//        return multiAdapter.getItems();
//    }

//    @Override
//    public SpeechRecognizer onRetainCustomNonConfigurationInstance() {
//        return sr;
//    }

//    protected boolean isNetworkConnected() {
//        try {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//            return mNetworkInfo != null;
//        } catch (NullPointerException e) {
//            return false;
//        }
//    }


//    private class SearchTaskTMDBAPI extends AsyncTask<String, Integer, List<Multi>> {
//
//        @Override
//        protected void onPreExecute() {
////            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_NOSENSOR) {
////                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
////            }
//            setLoadingRunning(true);
//            if (isFirstSearch()) {
//                multiAdapter.getItems().clear();
//                multiAdapter.notifyDataSetChanged();
//                search_loading_layout.setVisibility(View.VISIBLE);
//            }
//        }
//
//        protected List<Multi> doInBackground(String... query) {
//            try {
//                TmdbSearch r = new TmdbApi(Common.TMDB_APIKEY).getSearch();
//
//                TmdbSearch.MultiListResultsPage todo_ = r.searchMulti(query[0], Common.DEVICE_LANG, getActualPageResults());
//                setTotalPagesResults(todo_.getTotalPages());
//
//                // ELIMINA DE LA LISTA LOS QUE CUMPLAN LAS CONDICIONES
//                Iterator<Multi> i = todo_.iterator();
//                while (i.hasNext()) {
//                    Multi item = i.next();
//
//                    switch (item.getMediaType().ordinal()) {
//                        case Common.TMDB_CODE_MOVIES:
//                            MovieDb movie = (MovieDb) item;
//                            if ((movie.getReleaseDate() == null || movie.getReleaseDate().equals(""))
//                                    || (movie.getPosterPath() == null || movie.getPosterPath().equals(""))
//                                    || movie.getVoteAverage() == 0) {
//                                i.remove();
//                            }
//
//                            break;
//                        case Common.TMDB_CODE_PEOPLE:
//                            PersonPeople person = (PersonPeople) item;
//                            if ((person.getName() == null || person.getName().equals(""))) {
//                                i.remove();
//                            }
//
//                            break;
//                        case Common.TMDB_CODE_SERIES:
//                            TvSeries serie = (TvSeries) item;
//                            if ((serie.getFirstAirDate() == null || serie.getFirstAirDate().equals(""))
//                                    || (serie.getPosterPath() == null || serie.getPosterPath().equals(""))
//                                    || serie.getVoteAverage() == 0) {
//                                i.remove();
//                            }
//
//                            break;
//                    }
//                }
//
//                Log.d("BUCLE", "items: " + todo_.getResults().size() + ", pagina: " + getActualPageResults());
//
//                setActualPageResults(getActualPageResults() + 1);
//                Snackbar.make(search_activity_layout, "DOING", Snackbar.LENGTH_SHORT);
//
//                return todo_.getResults();
//            } catch (Exception e) {
//                Utility.setNoConnectedWarning(getApplicationContext(), search_activity_layout);
//
//                return null;
//            }
//        }
//
//
//        protected void onPostExecute(List<Multi> results) {
//            if (results != null) {
////                Collections.sort(results, new Comparator<Multi>() {
////                    @Override
////                    public int compare(Multi i1, Multi i2) {
////                        return i1.getMediaType().compareTo(i2.getMediaType());
////                    }
////                });
//
//
//                if (results.size() > 0) {
//                    if (isFirstSearch()) {
//                        search_loading_layout.setVisibility(View.GONE);
//                        setFirstSearch(false);
//                    } else {
//                        multiAdapter.removeLoader();
//                    }
//
//                    for (Multi m : results) {
//                        multiAdapter.insert(m);
//                    }
//
//                    if (!isNoMoreResults()) {
//                        multiAdapter.createLoader();
//                    }
//                } else {
//                    if (isFirstSearch()) {
//                        Snackbar.make(search_activity_layout, getString(R.string.warning_no_results), Snackbar.LENGTH_LONG).show();
//                        search_loading_layout.setVisibility(View.GONE);
//                    }
//                }
//            }
//
////            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//            setLoadingRunning(false);
//        }
//    }

    private boolean isNoMoreResults() {
        return fragmentSearchTask.getActualPageResults() > fragmentSearchTask.getTotalPagesResults();
    }

    private void initSpeechActions() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            verifyPermission(Manifest.permission.RECORD_AUDIO);
            sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            sr.setRecognitionListener(this);
//            sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        text_query.clearFocus();
//        hideSoftKeyboard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        search_recyclerview.requestFocus();
        hideSoftKeyboard();
        overridePendingTransition(R.animator.pull_left, R.animator.push_right);
        safeDestroySR();
        finish();
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(text_query, InputMethodManager.SHOW_FORCED);
    }

    public void loadViews() {
//        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface font_roboto_thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        search_activity_layout = (RelativeLayout) findViewById(R.id.activity_search_layout);

        txt_queryvoice = (TextView) findViewById(R.id.activity_search_sr_process);
        txt_queryvoice.setTypeface(font_roboto_thin);

        text_query = (EditText) findViewById(R.id.activity_search_inputbar_text_query);
        btn_mic = (ImageButton) findViewById(R.id.activity_search_inputbar_btn_mic);
        btn_clear = (ImageButton) findViewById(R.id.activity_search_inputbar_btn_clear);
        btn_back = (ImageButton) findViewById(R.id.activity_search_inputbar_btn_back);

        if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            btn_mic.setVisibility(View.GONE);
        }

//        initRecyclerView();

        search_recyclerview = (RelativeLayout) findViewById(R.id.include_search_recyclerview);


        search_loading_layout = (RelativeLayout) findViewById(R.id.search_loading_layout);
    }

//    public void initRecyclerView() {
//        search_recyclerview = (RecyclerView) findViewById(R.id.activity_search_recyclerview);
//        search_recyclerview.setHasFixedSize(true);
//        search_recyclerview.setItemAnimator(new DefaultItemAnimator());
//        GridLayoutManager search_recyclerview_lm = new GridLayoutManager(this, Utility.getColumnsFromWidth(getApplicationContext()));
//        search_recyclerview_lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                switch (fragmentSearchTask.getMultiAdapter().getItemViewType(position)) {
//                    case MultiAdapterTMDBAPI.TYPE_FOOTER_LOADER:
//                        if ((fragmentSearchTask.getMultiAdapter().getItemCount() - 1) % 2 == 0) {
//                            return Utility.getColumnsFromWidth(getApplicationContext());
//                        }
//                    default:
//                        return 1;
//                }
//            }
//        });
//        search_recyclerview.setLayoutManager(search_recyclerview_lm);
//        fragmentSearchTask.setMultiAdapter(new MultiAdapterTMDBAPI(new ArrayList<>(), SearchActivity.this));
//        search_recyclerview.setAdapter(fragmentSearchTask.getMultiAdapter());
//    }


    public void initTransitionVoiceQuery() {
        int colorFrom = ContextCompat.getColor(getApplicationContext(), R.color.bg_white_content);
        int colorTo = ContextCompat.getColor(getApplicationContext(), R.color.bg_orange_content);

        colorTransitionVoiceQuery = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorTransitionVoiceQuery.setDuration(350);

        colorTransitionVoiceQuery.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                search_activity_layout.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
    }

    private void setInputQueryLayout(int rightElement) {
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.LEFT_OF, rightElement);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.activity_search_inputbar_btn_back);
        text_query.setLayoutParams(params);
    }

    public void loadActions() {
        initTransitionVoiceQuery();
        initSpeechActions();

        btn_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVoiceQueryRunning()) {
                    setVoiceQueryON();
                    sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
                    text_query.setText("");
                } else {
                    setVoiceQueryOFF();
                    text_query.setText(getLastQuery());
                }
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        text_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                showSoftKeyboard();

                if (text_query.getText().length() > 0) {
                    setClearOn();
                }

                if (isVoiceQueryRunning()) {
                    txt_queryvoice.setVisibility(View.GONE);
//                    sr.destroy();
                    setVoiceQueryOFF();
                }
            }
        });

        text_query.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.getText().toString().equals("")) {
                    doFirstSearch(v.getText().toString());
                    setClearOffAndKeyboard();
                    return true;
                }

                return false;
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_query.setText("");
            }
        });

        text_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 1) {
                    setClearOn();
                }

                if (count == 0) {
                    setClearOff();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        fragmentSearchTask.getSearch_recyclerview().setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (!view.isFocused()) {
//                    view.requestFocus();
//                    setClearOffAndKeyboard();
//                }
//                return false;
//            }
//        });


//        fragmentSearchTask.getMultiAdapter().setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                if (fragmentSearchTask.getActualPageResults() <= fragmentSearchTask.getTotalPagesResults() && !isFirstSearch()) {
////                    new SearchTaskTMDBAPI().execute(getLastQuery());
//                    fragmentSearchTask.search(getLastQuery());
//                } else {
//                    Snackbar.make(search_activity_layout, getString(R.string.warning_no_more_results), Snackbar.LENGTH_LONG).show();
//                }
//            }
//        });

    }


    private void doFirstSearch(String query) {
        fragmentSearchTask.setActualPageResults(1);
        setFirstSearch(true);
        fragmentSearchTask.setFirstSearch(true);
//        new SearchTaskTMDBAPI().execute(query);
        fragmentSearchTask.search(query);

        setLastQuery(query);
    }

    private void setClearOffAndKeyboard() {
        hideSoftKeyboard();
        text_query.setFocusable(false);
        setClearOff();
    }

    private void setClearOff() {
        setClear(true);
        btn_clear.setVisibility(View.GONE);
        setInputQueryLayout(R.id.activity_search_inputbar_btn_mic);
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            btn_mic.setVisibility(View.VISIBLE);
        }
    }

    private void setClearOn() {
        setClear(false);
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            btn_mic.setVisibility(View.GONE);
        }
        setInputQueryLayout(R.id.activity_search_inputbar_btn_clear);
        btn_clear.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        txt_queryvoice.setText(getString(infoText[1]));
    }

    @Override
    public void onBeginningOfSpeech() {
        txt_queryvoice.setText(getString(infoText[2]));
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int i) {
//        Toast.makeText(SearchActivity.this, "ERROR " + i, Toast.LENGTH_SHORT).show();

        switch (i) {
            case SpeechRecognizer.ERROR_SERVER:
                if (!Utility.isNetworkConnected(getApplicationContext())) {
                    Snackbar snackbar = Snackbar
                            .make(search_activity_layout, getString(R.string.warning_no_connection), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                text_query.setText(getLastQuery());
                break;
            default:
                Log.d("ERROR_SR", "Error numero: " + i);
        }

        setClearOff();
        setVoiceQueryOFF();
    }

    @Override
    public void onResults(final Bundle bundle) {
        String query = "";

        try {
            query = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        } catch (Exception ignored) {

        }

        text_query.setText(query);
        text_query.setSelection(text_query.getText().length());
        setLastQuery(query);

        setClearOff();
        setVoiceQueryOFF();

        doFirstSearch(query);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
//        String query = "";
//
//        try {
//            query = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
//        } catch (Exception ignored) {
//
//        }
//
//        text_query.setText(query);
//        text_query.setSelection(text_query.getText().length());
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }

//    private void setVoiceQueryOFF() {
//        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
//            setVoiceQueryRunning(false);
//            Toast.makeText(SearchActivity.this, "OFF, running: " + isVoiceQueryRunning(), Toast.LENGTH_SHORT).show();
//            txt_queryvoice.setText(getString(infoText[0]));
//            txt_queryvoice.setVisibility(View.GONE);
//            btn_mic.setImageResource(R.drawable.ic_svg_microphone);
//            sr.destroy();
//            colorTransitionVoiceQuery.reverse();
//            search_recyclerview.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void setVoiceQueryON() {
//        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
//            setVoiceQueryRunning(true);
//            search_recyclerview.setVisibility(View.GONE);
//            colorTransitionVoiceQuery.start();
//            setClearOffAndKeyboard();
//            txt_queryvoice.setVisibility(View.VISIBLE);
//            btn_mic.setImageResource(R.drawable.ic_svg_microphone_off);
//        }
//    }


    public void setVoiceQueryOFF() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            setVoiceQueryRunning(false);
            sr.cancel();
            btn_mic.setImageResource(R.drawable.ic_svg_microphone);
            colorTransitionVoiceQuery.reverse();
            txt_queryvoice.setVisibility(View.GONE);
            txt_queryvoice.setText(getString(infoText[0]));
            search_recyclerview.setVisibility(View.VISIBLE);
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void setVoiceQueryON() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            setVoiceQueryRunning(true);
            search_recyclerview.setVisibility(View.GONE);
            colorTransitionVoiceQuery.start();
            setClearOffAndKeyboard();
            btn_mic.setImageResource(R.drawable.ic_svg_microphone_off);
            txt_queryvoice.setVisibility(View.VISIBLE);
        }
    }


    private void safeDestroySR() {
        if (isVoiceQueryRunning()) {
            sr.destroy();
        }
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

//    public int getTotalPagesResults() {
//        return totalPagesResults;
//    }
//
//    public void setTotalPagesResults(int totalPagesResults) {
//        this.totalPagesResults = totalPagesResults;
//    }
//
//    public int getActualPageResults() {
//        return actualPageResults;
//    }
//
//    public void setActualPageResults(int actualPageResults) {
//        this.actualPageResults = actualPageResults;
//    }

    public boolean isFirstSearch() {
        return firstSearch;
    }

    public void setFirstSearch(boolean firstSearch) {
        this.firstSearch = firstSearch;
    }

    public String getLastQuery() {
        return lastQuery;
    }

    public void setLastQuery(String lastQuery) {
        this.lastQuery = lastQuery;
    }

    public boolean isVoiceQueryRunning() {
        return voiceQueryRunning;
    }

    public void setVoiceQueryRunning(boolean voiceQueryRunning) {
        this.voiceQueryRunning = voiceQueryRunning;
    }

    public boolean isLoadingRunning() {
        return loadingRunning;
    }

    public void setLoadingRunning(boolean loadingRunning) {
        this.loadingRunning = loadingRunning;
    }

    @Override
    public void onPreExecute() {
        setLoadingRunning(true);
        if (isFirstSearch()) {
            fragmentSearchTask.getMultiAdapter().getItems().clear();
            fragmentSearchTask.getMultiAdapter().notifyDataSetChanged();
            search_loading_layout.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public void onProgressUpdate(int percent) {
//
//    }
//
//    @Override
//    public void onCancelled() {
//
//    }

    @Override
    public void onPostExecute(List<Multi> results) {

        if (results != null) {
//                Collections.sort(results, new Comparator<Multi>() {
//                    @Override
//                    public int compare(Multi i1, Multi i2) {
//                        return i1.getMediaType().compareTo(i2.getMediaType());
//                    }
//                });

            if (results.size() > 0) {
                if (isFirstSearch()) {
                    search_loading_layout.setVisibility(View.GONE);
                    setFirstSearch(false);
                } else {
                    fragmentSearchTask.getMultiAdapter().removeLoader();
                }

                for (Multi m : results) {
                    fragmentSearchTask.getMultiAdapter().insert(m);
                }

                if (!isNoMoreResults()) {
                    fragmentSearchTask.getMultiAdapter().createLoader();
                }
            } else {
                if (isFirstSearch()) {
                    Snackbar.make(search_activity_layout, getString(R.string.warning_no_results), Snackbar.LENGTH_LONG).show();
                    search_loading_layout.setVisibility(View.GONE);
                }
            }
        } else {
            Utility.setNoConnectedWarning(getApplicationContext(), search_activity_layout);
        }

        setLoadingRunning(false);
    }
}
