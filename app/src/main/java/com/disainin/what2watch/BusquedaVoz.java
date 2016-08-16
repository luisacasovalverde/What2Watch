package com.disainin.what2watch;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;

public class BusquedaVoz implements RecognitionListener {

    private Context context;
    private SpeechRecognizer sr;
    private TextView vista_informacion, vista_resultado;
    private final int[] infoText = new int[]{
            R.string.bv_vacio, //0
            R.string.bv_escuchando, //1
            R.string.bv_reconociendo, //2
            R.string.bv_error_desconexion, //3
    };


    public BusquedaVoz(Context context, TextView... tv) {
        this.vista_informacion = tv[0];
        this.vista_resultado = tv[1];
        this.context = context;

        setSpeechActions();
    }

    private void setSpeechActions() {
        sr = SpeechRecognizer.createSpeechRecognizer(context);
        sr.setRecognitionListener(this);
        sr.startListening(RecognizerIntent.getVoiceDetailsIntent(context));
    }

    private void setInfo(int stringID) {
        vista_informacion.setText(stringID);
    }

    private void setString(byte n) {
        setInfo(infoText[n]);
    }


    @Override
    public void onReadyForSpeech(Bundle bundle) {

        setString((byte) 1);
    }

    @Override
    public void onBeginningOfSpeech() {
        setString((byte) 2);

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        setString((byte) 0);
    }

    @Override
    public void onError(int i) {
//        setString((byte) 3);
    }

    @Override
    public void onResults(Bundle bundle) {
        if (vista_resultado != null) {
            vista_resultado.setText(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
        }

//        new AdapterAPI(context, 0, bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0), MainActivity.vistas);
        sr.destroy();
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
