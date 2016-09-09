package com.disainin.what2watch;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.disainin.what2watch.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.PersonPeople;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class SearchActivity extends AppCompatActivity implements RecognitionListener {

    private boolean clear = true, first_search = false;
    private String lastQuery = "";
    private ValueAnimator ColorTransitionVoicequery;
    private RelativeLayout layout_busqueda, search_loading_layout;
    private EditText text_query;
    private TextView txt_queryvoice;
    private ImageButton btn_mic, btn_back, btn_clear;
    private SpeechRecognizer sr;
    private final int[] infoText = new int[]{
            R.string.search_voice_empty, //0
            R.string.search_voice_listening, //1
            R.string.search_voice_recognizing, //2
    };
    private RecyclerView search_recyclerview;
    private MultiAdapterTMDBAPI multiAdapter;
    private int totalPagesResults = 1, actualPageResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState != null) {
            setActualPageResults(savedInstanceState.getInt("LAST_PAGE_LOADED"));
            setTotalPagesResults(savedInstanceState.getInt("TOTAL_PAGES"));
            setFirst_search(savedInstanceState.getBoolean("FIRST_SEARCH"));
            lastQuery = savedInstanceState.getString("LAST_QUERY");
        }

        loadViews();
        loadActions();

        if (getLastCustomNonConfigurationInstance() != null) {
            multiAdapter.setItems((List) getLastCustomNonConfigurationInstance());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("LAST_PAGE_LOADED", getActualPageResults());
        outState.putInt("TOTAL_PAGES", getTotalPagesResults());
        outState.putBoolean("FIRST_SEARCH", isFirst_search());
        outState.putString("LAST_QUERY", lastQuery);
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
                Utility.showRequestPermissionAction(getApplicationContext(), layout_busqueda, R.string.permission_record_audio);
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

    @Override
    public List onRetainCustomNonConfigurationInstance() {
        return multiAdapter.getItems();
    }

    protected boolean isNetworkConnected() {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            return mNetworkInfo != null;
        } catch (NullPointerException e) {
            return false;
        }
    }


    private class SearchTaskTMDBAPI extends AsyncTask<String, Integer, List<Multi>> {

        @Override
        protected void onPreExecute() {
            if (first_search) {
                multiAdapter.getItems().clear();
                multiAdapter.notifyDataSetChanged();
                search_loading_layout.setVisibility(View.VISIBLE);
            }
        }

        protected List<Multi> doInBackground(String... query) {
            try {
                TmdbSearch r = new TmdbApi(Common.TMDB_APIKEY).getSearch();

                TmdbSearch.MultiListResultsPage todo = r.searchMulti(query[0], Common.DEVICE_LANG, getActualPageResults());
                setTotalPagesResults(todo.getTotalPages());

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
            } catch (RuntimeException e) {
                if (!isNetworkConnected()) {
                    Snackbar.make(layout_busqueda, getString(R.string.warning_no_connection), Snackbar.LENGTH_LONG).show();
                }

                return null;
            }
        }


        protected void onPostExecute(List<Multi> results) {
            if (results != null) {
//                Collections.sort(results, new Comparator<Multi>() {
//                    @Override
//                    public int compare(Multi i1, Multi i2) {
//                        return i1.getMediaType().compareTo(i2.getMediaType());
//                    }
//                });

                if (results.size() > 0) {
                    if (first_search) {
                        first_search = false;
                        search_loading_layout.setVisibility(View.GONE);
                    } else {
                        multiAdapter.removeLoader();
                    }

                    for (Multi m : results) {
                        multiAdapter.insert(m);
                    }

                    if (!isNoMoreResults()) {
                        multiAdapter.createLoader();
                    }
                } else {
                    if (first_search) {
                        Snackbar.make(layout_busqueda, getString(R.string.warning_no_results), Snackbar.LENGTH_LONG).show();
                        search_loading_layout.setVisibility(View.GONE);
                    }
                }
            }
        }

    }

    private boolean isNoMoreResults() {
        return getActualPageResults() > getTotalPagesResults();
    }


    private void initSpeechActions() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            verifyPermission(Manifest.permission.RECORD_AUDIO);
            sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            sr.setRecognitionListener(this);
            sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftKeyboard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        hideSoftKeyboard();
        safeDestroySR();
        overridePendingTransition(R.animator.pull_left, R.animator.push_right);
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

        layout_busqueda = (RelativeLayout) findViewById(R.id.activity_search_layout);

        txt_queryvoice = (TextView) findViewById(R.id.activity_search_sr_process);
        txt_queryvoice.setTypeface(font_roboto_thin);

        text_query = (EditText) findViewById(R.id.activity_search_inputbar_text_query);

        btn_mic = (ImageButton) findViewById(R.id.activity_search_inputbar_btn_mic);

        if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            btn_mic.setVisibility(View.GONE);
        }

        btn_clear = (ImageButton) findViewById(R.id.activity_search_inputbar_btn_clear);

        btn_back = (ImageButton) findViewById(R.id.activity_search_inputbar_btn_back);

        search_recyclerview = (RecyclerView) findViewById(R.id.activity_search_recyclerview);
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setItemAnimator(new DefaultItemAnimator());

        GridLayoutManager search_recyclerview_lm = new GridLayoutManager(this, Utility.getColumnsFromWidth(getApplicationContext()));
        search_recyclerview_lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (multiAdapter.getItemViewType(position)) {
                    case MultiAdapterTMDBAPI.TYPE_FOOTER_LOADER:
                        if ((multiAdapter.getItemCount() - 1) % 2 == 0) {
                            return Utility.getColumnsFromWidth(getApplicationContext());
                        }
                    default:
                        return 1;
                }
            }
        });
        search_recyclerview.setLayoutManager(search_recyclerview_lm);
        multiAdapter = new MultiAdapterTMDBAPI(new ArrayList<>(), SearchActivity.this);
        search_recyclerview.setAdapter(multiAdapter);

        search_loading_layout = (RelativeLayout) findViewById(R.id.search_loading_layout);
    }


    public void initTransitionVoicequery() {
        int colorFrom = ContextCompat.getColor(getApplicationContext(), R.color.bg_white_content);
        int colorTo = ContextCompat.getColor(getApplicationContext(), R.color.bg_orange_content);

        ColorTransitionVoicequery = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        ColorTransitionVoicequery.setDuration(350);

        ColorTransitionVoicequery.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                layout_busqueda.setBackgroundColor((int) animator.getAnimatedValue());
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
        initTransitionVoicequery();

        btn_mic.setTag(true);
        btn_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Boolean) btn_mic.getTag()) {
                    initSpeechActions();
                    setMicOff();
                    text_query.setText("");
                } else {
                    setMicOn();
                    text_query.setText(lastQuery);
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
//                showSoftKeyboard();

                if (text_query.getText().length() > 0) {
                    setClearOn();
                }

                if (!(Boolean) btn_mic.getTag()) {
                    txt_queryvoice.setVisibility(View.GONE);
                    sr.destroy();
                    setMicOn();
                }
            }
        });

        text_query.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.getText().toString().equals("")) {

                    setActualPageResults(1);
                    first_search = true;
                    new SearchTaskTMDBAPI().execute(v.getText().toString());

                    lastQuery = v.getText().toString();
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


        search_recyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    setClearOffAndKeyboard();
                }
                return false;
            }
        });


        multiAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (getActualPageResults() <= getTotalPagesResults() && !isFirst_search()) {
                    new SearchTaskTMDBAPI().execute(lastQuery);
                } else {
//                    multiAdapter.removeLoader();
                    Snackbar.make(layout_busqueda, getString(R.string.warning_no_more_results), Snackbar.LENGTH_LONG).show();
                }
            }
        });

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
        switch (i) {
            case SpeechRecognizer.ERROR_SERVER:
                if (!isNetworkConnected()) {
                    Snackbar snackbar = Snackbar
                            .make(layout_busqueda, getString(R.string.warning_no_connection), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                text_query.setText(lastQuery);
                break;
            default:
//                Toast.makeText(BuscarActivity.this, "Error: " + i, Toast.LENGTH_LONG).show();
        }
        setClearOff();
        setMicOn();
    }

    @Override
    public void onResults(Bundle bundle) {
        String query = "";

        try {
            query = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        } catch (Exception ignored) {

        }

        text_query.setText(query);
        text_query.setSelection(text_query.getText().length());
        setClearOff();
        setMicOn();

        setActualPageResults(1);
        first_search = true;
        new SearchTaskTMDBAPI().execute(query);

        lastQuery = query;
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    private void setMicOn() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            btn_mic.setTag(true);
            txt_queryvoice.setText(getString(infoText[0]));
            txt_queryvoice.setVisibility(View.GONE);
            btn_mic.setImageResource(R.drawable.ic_svg_microphone);
            sr.destroy();
            ColorTransitionVoicequery.reverse();
            search_recyclerview.setVisibility(View.VISIBLE);
        }
    }

    private void setMicOff() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            search_recyclerview.setVisibility(View.GONE);
            ColorTransitionVoicequery.start();
            setClearOffAndKeyboard();
            txt_queryvoice.setVisibility(View.VISIBLE);
            btn_mic.setTag(false);
            btn_mic.setImageResource(R.drawable.ic_svg_microphone_off);
        }
    }

    private void safeDestroySR() {
        if (!(Boolean) btn_mic.getTag()) {
            setMicOn();
        }
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public int getTotalPagesResults() {
        return totalPagesResults;
    }

    public void setTotalPagesResults(int totalPagesResults) {
        this.totalPagesResults = totalPagesResults;
    }

    public int getActualPageResults() {
        return actualPageResults;
    }

    public void setActualPageResults(int actualPageResults) {
        this.actualPageResults = actualPageResults;
    }

    public boolean isFirst_search() {
        return first_search;
    }

    public void setFirst_search(boolean first_search) {
        this.first_search = first_search;
    }
}
