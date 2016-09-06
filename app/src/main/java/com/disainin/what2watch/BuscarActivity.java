package com.disainin.what2watch;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
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
import android.text.Layout;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.PersonPeople;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class BuscarActivity extends AppCompatActivity implements RecognitionListener {

    private boolean clear = true;
    private List LIST_RESULTS = null;
    private String lastQuery = "";
    private ValueAnimator ColorTransitionVoicequery;
    private RelativeLayout layout_busqueda, layout_input, recyclerview_buscar_item, layout_loading_buscar;
    private EditText input_query;
    private TextView txt_queryvoice;
    private ImageButton input_btn_voice, btn_back, input_btn_clear;
    private SpeechRecognizer sr;
    private final int[] infoText = new int[]{
            R.string.bv_vacio, //0
            R.string.bv_escuchando, //1
            R.string.bv_reconociendo, //2
            R.string.bv_error_desconexion, //3
    };
    private RecyclerView recyclerview_buscar;
    private MultiAdapterTMDBAPI mAdapter;
    private int totalPagesResults = 0, actualPageResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        loadViews();
        loadActions();

        if (getLastCustomNonConfigurationInstance() != null) {
            mAdapter = new MultiAdapterTMDBAPI((List) getLastCustomNonConfigurationInstance(), BuscarActivity.this);
            recyclerview_buscar.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            LIST_RESULTS = (List) getLastCustomNonConfigurationInstance();
        }
    }


    private void verifyPermission(String permission_type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int writePermission = checkSelfPermission(permission_type);

            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermission(permission_type);
            }

//              else -> accion en caso de que ya tenga los permisos

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
//                saveComments();
                Toast.makeText(BuscarActivity.this, "PERMITIDO RECORD AUDIO", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BuscarActivity.this, "PROHIBIDO RECORD AUDIO", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return LIST_RESULTS;
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
            layout_loading_buscar.setVisibility(View.VISIBLE);
/*            layout_loading_buscar.animate()
                    .alpha(1.0f)
                    .setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            layout_loading_buscar.setVisibility(View.VISIBLE);
                        }
                    });*/
        }

        protected List<Multi> doInBackground(String... query) {
            try {
                TmdbSearch r = new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getSearch();
                TmdbSearch.MultiListResultsPage todo = r.searchMulti(query[0], Common.DEVICE_LANG, getActualPageResults());
                todo.getTotalPages();


                Iterator<Multi> i = todo.iterator();
                while (i.hasNext()) {
                    Multi item = i.next();

                    switch (item.getMediaType().ordinal()) {
                        case 0:
                            MovieDb movie = (MovieDb) item;
                            if ((movie.getReleaseDate() == null || movie.getReleaseDate().equals(""))
                                    || (movie.getPosterPath() == null || movie.getPosterPath().equals(""))
                                    || movie.getVoteAverage() == 0) {
                                i.remove();
                            }

                            break;
                        case 1:
                            PersonPeople person = (PersonPeople) item;
//                            if ((person.getName() == null || person.getName().equals(""))
//                                    || (person.getProfilePath() == null || person.getProfilePath().equals(""))) {
//                                i.remove();
//                            }

                            if ((person.getName() == null || person.getName().equals(""))) {
                                i.remove();
                            }

                            break;
                        case 2:
                            TvSeries serie = (TvSeries) item;
                            if ((serie.getFirstAirDate() == null || serie.getFirstAirDate().equals(""))
                                    || (serie.getPosterPath() == null || serie.getPosterPath().equals(""))
                                    || serie.getVoteAverage() == 0) {
                                i.remove();
                            }
                            break;
                    }


                }


                return todo.getResults();
            } catch (RuntimeException e) {
                if (!isNetworkConnected()) {
                    Snackbar snackbar = Snackbar
                            .make(layout_busqueda, getString(R.string.warning_no_connection), Snackbar.LENGTH_LONG)
                            /*.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })*/;
//                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                }

                return null;
            }
        }

        protected void onPostExecute(List<Multi> results) {
            if (results != null) {
                Collections.sort(results, new Comparator<Multi>() {
                    @Override
                    public int compare(Multi i1, Multi i2) {
                        return i1.getMediaType().compareTo(i2.getMediaType());
                    }
                });


                if (results.size() > 0) {
                    LIST_RESULTS = results;
                    mAdapter = new MultiAdapterTMDBAPI(results, BuscarActivity.this);
                    recyclerview_buscar.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(layout_busqueda, getString(R.string.search_no_results), Snackbar.LENGTH_LONG)
                            /*.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })*/;
//                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                }
            }

/*            layout_loading_buscar.animate()
                    .alpha(0.0f)
                    .setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            layout_loading_buscar.setVisibility(View.GONE);
                        }
                    });*/
            layout_loading_buscar.setVisibility(View.GONE);
        }
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

/*    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Toast.makeText(BuscarActivity.this, "" + requestCode, Toast.LENGTH_SHORT).show();
*//*
        switch (requestCode) {
            case RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        // other 'case' lines to check for other
        // permissions this app might request
    }*//*

    }*/

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(input_query, InputMethodManager.SHOW_FORCED);
    }

    public void loadViews() {
//        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface font_roboto_thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        layout_busqueda = (RelativeLayout) findViewById(R.id.layout_busqueda);
        layout_input = (RelativeLayout) findViewById(R.id.layout_input);

        txt_queryvoice = (TextView) findViewById(R.id.txt_queryvoice);
        txt_queryvoice.setTypeface(font_roboto_thin);

        input_query = (EditText) findViewById(R.id.busqueda_input_query);

        input_btn_voice = (ImageButton) findViewById(R.id.busqueda_input_btn_voice);

        if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            input_btn_voice.setVisibility(View.GONE);
        }

        input_btn_clear = (ImageButton) findViewById(R.id.busqueda_input_btn_clear);

        btn_back = (ImageButton) findViewById(R.id.busqueda_btn_back);

        recyclerview_buscar = (RecyclerView) findViewById(R.id.recycler_view);
        //ESTE FUNCIONA
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerview_buscar.setLayoutManager(mLayoutManager);

        recyclerview_buscar.setHasFixedSize(true);
//        recyclerview_buscar.setLayoutManager(new GridLayoutManager(this, Utility.calculateNoOfColumns(getApplicationContext(), 255)));


        recyclerview_buscar.setLayoutManager(new GridLayoutManager(this, Utility.getColumnsFromWidth(getApplicationContext())));
        recyclerview_buscar.setItemAnimator(new DefaultItemAnimator());

        recyclerview_buscar.setAdapter(mAdapter);

        layout_loading_buscar = (RelativeLayout) findViewById(R.id.layout_loading_buscar);
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
//        params.addRule(RelativeLayout.START_OF, rightElement);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.busqueda_btn_back);
//        params.addRule(RelativeLayout.END_OF, R.id.busqueda_btn_back);
        input_query.setLayoutParams(params);
    }

    public void loadActions() {
        initTransitionVoicequery();

        input_btn_voice.setTag(true);
        input_btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Boolean) input_btn_voice.getTag()) {
                    initSpeechActions();
                    setMicOff();
                    input_query.setText("");
                } else {
                    setMicOn();
                    input_query.setText(lastQuery);
                }
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        input_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setFocusableInTouchMode(true);
                view.requestFocus();
//                showSoftKeyboard();

                if (input_query.getText().length() > 0) {
                    setClearOn();
                }

                if (!(Boolean) input_btn_voice.getTag()) {
                    txt_queryvoice.setVisibility(View.GONE);
                    sr.destroy();
                    setMicOn();
                }
            }
        });

        input_query.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.getText().toString().equals("")) {

                    setActualPageResults(1);
                    new SearchTaskTMDBAPI().execute(v.getText().toString());

                    lastQuery = v.getText().toString();
                    setClearOffAndKeyboard();
                    return true;
                }


//                Toast.makeText(getApplicationContext(), "BIEN!", Toast.LENGTH_SHORT).show();

//                    Snackbar.make(v, "Esto es una prueba", Snackbar.LENGTH_LONG).show();


                return false;
            }
        });

        input_btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_query.setText("");
            }
        });

        input_query.addTextChangedListener(new TextWatcher() {
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


        recyclerview_buscar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (input_query.isFocused()) {
                    setClearOffAndKeyboard();
//                    }
                }
                return false;
            }
        });


    }


    private void setClearOffAndKeyboard() {
        hideSoftKeyboard();
        input_query.setFocusable(false);
        setClearOff();
    }


    private void setClearOff() {
        setClear(true);
        input_btn_clear.setVisibility(View.GONE);
        setInputQueryLayout(R.id.busqueda_input_btn_voice);
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            input_btn_voice.setVisibility(View.VISIBLE);
        }
    }

    private void setClearOn() {
        setClear(false);
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            input_btn_voice.setVisibility(View.GONE);
        }
        setInputQueryLayout(R.id.busqueda_input_btn_clear);
        input_btn_clear.setVisibility(View.VISIBLE);
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
                input_query.setText(lastQuery);
                break;
            default:
//                Toast.makeText(BuscarActivity.this, "Error: " + i, Toast.LENGTH_LONG).show();
        }
        setClearOff();
        setMicOn();
    }

    @Override
    public void onResults(Bundle bundle) {
        input_query.setText(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
        input_query.setSelection(input_query.getText().length());
        setClearOff();
        setMicOn();

        setActualPageResults(1);
        new SearchTaskTMDBAPI().execute(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));

        lastQuery = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    private void setMicOn() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            input_btn_voice.setTag(true);
            txt_queryvoice.setText(getString(infoText[0]));
            txt_queryvoice.setVisibility(View.GONE);
            input_btn_voice.setImageResource(R.drawable.ic_svg_microphone);
            sr.destroy();
            ColorTransitionVoicequery.reverse();
            recyclerview_buscar.setVisibility(View.VISIBLE);
        }
    }

    private void setMicOff() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            recyclerview_buscar.setVisibility(View.GONE);
            ColorTransitionVoicequery.start();
            setClearOffAndKeyboard();
            txt_queryvoice.setVisibility(View.VISIBLE);
            input_btn_voice.setTag(false);
            input_btn_voice.setImageResource(R.drawable.ic_svg_microphone_off);
        }
    }

    private void safeDestroySR() {
        if (!(Boolean) input_btn_voice.getTag()) {
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
}
