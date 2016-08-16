package com.disainin.what2watch;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BuscarActivity extends AppCompatActivity implements RecognitionListener {

    private ValueAnimator ColorTransitionVoicequery;
    private RelativeLayout busqueda_layout, layout_input;
    private EditText input_query;
    private TextView txt_resultado, txt_queryvoice;
    private ImageButton input_btn_voice, btn_back;
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

//        initToolbar();

        cargarViews();
        loadActions();

    }

/*    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_busqueda);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }*/


    private void initSpeechActions() {
        sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        sr.setRecognitionListener(this);
        sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.pull_left, R.animator.push_right);
        finish();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void cargarViews() {
//        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface font_roboto_thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        busqueda_layout = (RelativeLayout) findViewById(R.id.busqueda_layout);
        layout_input = (RelativeLayout) findViewById(R.id.layout_input);
        txt_resultado = (TextView) findViewById(R.id.txt_resultado);

        txt_queryvoice = (TextView) findViewById(R.id.txt_queryvoice);
        txt_queryvoice.setTypeface(font_roboto_thin);

        input_query = (EditText) findViewById(R.id.busqueda_input_query);
        input_btn_voice = (ImageButton) findViewById(R.id.busqueda_input_btn_voice);

        btn_back = (ImageButton) findViewById(R.id.busqueda_btn_back);


//        Animation hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);
//        input_btn_voice.startAnimation(hyperspaceJump);


//
//        input_pelicula = (EditText) findViewById(R.id.input_pelicula);
//
//        btn_mic = (Button) findViewById(R.id.btnSpeak);
//        btn_mic.setTypeface(fontawesome);
//
//        vistas = new TextView[]{
//                txt_resultado,
//                input_pelicula
//        };
//
//
//        layout_base = (RelativeLayout) findViewById(R.id.layout_base);





/*        input_query.setOnTouchListener(new View.OnTouchListener() {

            private float touchX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int drawableLeft = input_query.getRight() - input_query.getCompoundDrawables()[2].getBounds().width();
                // This detects the location of touch on ACTION_DOWN, but because it is
                // using getRawX() and getRight() and the EditText's parent is not at the
                // left of the screen, it will respond when clicked in the middle of the
                // EditText. Instead, use getX() and EditText.getWidth()
                if (event.getAction() == MotionEvent.ACTION_DOWN && event.getRawX() >= drawableLeft) {
                    touchX = event.getRawX();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP && touchX >= drawableLeft) {


//                    Toast.makeText(getApplicationContext(), "Clicked Button", Toast.LENGTH_SHORT).show();

                    new BusquedaVoz(getApplicationContext(), txt_queryvoice, input_query);


                    touchX = 0;
                    return true;
                } else {
                    return input_query.onTouchEvent(event);
                }
            }
        });*/


    }


/*    public void voiceActionAnimation(final View v) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, layout_input.getLayoutParams().height);
        anim.setDuration(750);

        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                params.bottomMargin += -100;
//
//                view.setLayoutParams(params);
//                busqueda_layout.setBackgroundColor(Color.parseColor("#002775"));
                animation.setFillAfter(true);
//                animation.reset();
            }
        });

        v.startAnimation(anim);
    }*/

    public class ResizeAnimation extends Animation {
        private View mView;
        private float mToHeight;
        private float mFromHeight;

        private float mToWidth;
        private float mFromWidth;

        public ResizeAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
            mToHeight = busqueda_layout.getHeight();
            mToWidth = busqueda_layout.getWidth();
            mFromHeight = fromHeight;
            mFromWidth = busqueda_layout.getWidth();
            mView = v;
            setDuration(700);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);

            layoutParams.setMargins(0, 0, 0, 0);
            mView.setLayoutParams(layoutParams);

            float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
            float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
            ViewGroup.LayoutParams p = mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            mView.requestLayout();
        }
    }


    public void initTransitionVoicequery() {
        int colorFrom = Color.parseColor("#ffffff");
        int colorTo = Color.parseColor("#1e88e5");

        ColorTransitionVoicequery = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        ColorTransitionVoicequery.setDuration(750);

        ColorTransitionVoicequery.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                busqueda_layout.setBackgroundColor((int) animator.getAnimatedValue());

            }

        });
    }


    public void loadActions() {
        initTransitionVoicequery();

        input_btn_voice.setTag(new Boolean(true));
        input_btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Boolean) input_btn_voice.getTag()) {
                    initSpeechActions();
//                    moveButtonDown(input_btn_voice);
//                    voiceActionAnimation(layout_input);


/*                    Animation a = new Animation() {

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout_input.getLayoutParams();
                            params.leftMargin = (int) (0 * interpolatedTime);
                            params.topMargin = (int) (0 * interpolatedTime);
                            params.rightMargin = (int) (0 * interpolatedTime);
                            params.bottomMargin = (int) (0 * interpolatedTime);
                            layout_input.setLayoutParams(params);
                        }
                    };
                    a.setDuration(500); // in ms
                    layout_input.startAnimation(a);


                    ResizeAnimation ra = new ResizeAnimation(layout_input, 0, 0, 500, 500);
                    layout_input.startAnimation(ra);*/


                    ColorTransitionVoicequery.start();


                    txt_queryvoice.setVisibility(View.VISIBLE);
                } else {
                    txt_queryvoice.setVisibility(View.INVISIBLE);
                    sr.destroy();
                    setMicOn();
                }

            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


//        input_query.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new AdapterAPI(getApplicationContext(), 0, input_query.getText().toString(), txt_resultado);
//            }
//        });
//
//        btn_mic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new BusquedaVoz(getApplicationContext(), txt_queryvoice, input_pelicula);
//            }
//        });
//
//        layout_base.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (input_pelicula.isFocused()) {
//                        input_pelicula.clearFocus();
//                        hideSoftKeyboard();
//                    }
//                }
//                return false;
//            }
//        });
    }


    @Override
    public void onReadyForSpeech(Bundle bundle) {
        setMicOff();
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
        setMicOn();
    }

    @Override
    public void onResults(Bundle bundle) {
        input_query.setText(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
        input_query.setSelection(input_query.getText().length());
        setMicOn();
        new AdapterAPI(getApplicationContext(), 0, bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0), txt_resultado);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    public void setMicOn() {
        input_btn_voice.setTag(new Boolean(true));
        txt_queryvoice.setText(getString(infoText[0]));
        txt_queryvoice.setVisibility(View.INVISIBLE);
        input_btn_voice.setImageResource(R.drawable.ic_mic_white_24dp);
        sr.destroy();
        ColorTransitionVoicequery.reverse();
    }

    public void setMicOff() {
        txt_queryvoice.setVisibility(View.VISIBLE);
        input_btn_voice.setTag(new Boolean(false));
        hideSoftKeyboard();
        input_btn_voice.setImageResource(R.drawable.ic_mic_off_white_24dp);
    }
}
