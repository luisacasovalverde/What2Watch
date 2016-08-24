package com.disainin.what2watch;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BuscarActivity extends AppCompatActivity implements RecognitionListener {

    private boolean clear = true;
    private String lastQuery = "";
    private ValueAnimator ColorTransitionVoicequery;
    private ScrollView layout_container;
    private RelativeLayout layout_busqueda, layout_input;
    private EditText input_query;
    private TextView txt_resultado, txt_queryvoice;
    private ImageButton input_btn_voice, btn_back, input_btn_clear;
    private SpeechRecognizer sr;
    private final int[] infoText = new int[]{
            R.string.bv_vacio, //0
            R.string.bv_escuchando, //1
            R.string.bv_reconociendo, //2
            R.string.bv_error_desconexion, //3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        cargarViews();
        loadActions();
    }

    private void initSpeechActions() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            sr.setRecognitionListener(this);
            sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public void cargarViews() {
//        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface font_roboto_thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        layout_container = (ScrollView) findViewById(R.id.layout_container);
        layout_busqueda = (RelativeLayout) findViewById(R.id.layout_busqueda);
        layout_input = (RelativeLayout) findViewById(R.id.layout_input);
        txt_resultado = (TextView) findViewById(R.id.txt_resultado);

        txt_queryvoice = (TextView) findViewById(R.id.txt_queryvoice);
        txt_queryvoice.setTypeface(font_roboto_thin);

        input_query = (EditText) findViewById(R.id.busqueda_input_query);

        input_btn_voice = (ImageButton) findViewById(R.id.busqueda_input_btn_voice);

        if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            input_btn_voice.setVisibility(View.GONE);
        }

        input_btn_clear = (ImageButton) findViewById(R.id.busqueda_input_btn_clear);

        btn_back = (ImageButton) findViewById(R.id.busqueda_btn_back);
    }


    public void initTransitionVoicequery() {
        int colorFrom = getResources().getColor(R.color.bg_white_content);
        int colorTo = getResources().getColor(R.color.bg_input_busqueda);

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
        params.addRule(RelativeLayout.START_OF, rightElement);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.busqueda_btn_back);
        params.addRule(RelativeLayout.END_OF, R.id.busqueda_btn_back);
        input_query.setLayoutParams(params);
    }


    public void loadActions() {
        initTransitionVoicequery();

        input_btn_voice.setTag(new Boolean(true));
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
                showSoftKeyboard();

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
                    new AdapterAPI(getApplicationContext(), 0, v.getText().toString(), txt_resultado);
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
                } else if (count == 0) {
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


        layout_container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (input_query.isFocused()) {
                        setClearOffAndKeyboard();
                    }
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
        setClearOff();
        setMicOn();
    }

    @Override
    public void onResults(Bundle bundle) {
        input_query.setText(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
        input_query.setSelection(input_query.getText().length());
        setClearOff();
        setMicOn();
        new AdapterAPI(getApplicationContext(), 0, bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0), txt_resultado);
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
            input_btn_voice.setTag(new Boolean(true));
            txt_queryvoice.setText(getString(infoText[0]));
            txt_queryvoice.setVisibility(View.GONE);
            input_btn_voice.setImageResource(R.drawable.ic_mic_white_24dp);
            sr.destroy();
            ColorTransitionVoicequery.reverse();
        }
    }

    private void setMicOff() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            ColorTransitionVoicequery.start();
            setClearOffAndKeyboard();
            txt_queryvoice.setVisibility(View.VISIBLE);
            input_btn_voice.setTag(new Boolean(false));
            input_btn_voice.setImageResource(R.drawable.ic_mic_off_white_24dp);
        }
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }
}
