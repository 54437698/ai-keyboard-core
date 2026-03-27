package com.jv.ai_keyboard;

import android.view.inputmethod.EditorInfo; 
import android.view.inputmethod.InputConnection;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent; 
import android.widget.TextView;
import android.util.Log;
import java.util.ArrayList;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private boolean isSymbols = false;
    private JvNativeEngine npuEngine;
    private KeyboardView kv;
    private Keyboard k;
    private View mCandidateView;
    private TextView suggestionText;
    private boolean isCaps = false;

    @Override
    public void onCreate() {
        super.onCreate();
        npuEngine = new JvNativeEngine();
        npuEngine.initialize(this); 
        Log.d("JV_DEBUG", "Sovereign LiteRT-LM Engine Initialized");
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        k = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(k);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public View onCreateCandidatesView() {
        mCandidateView = getLayoutInflater().inflate(R.layout.candidate_preview, null);
        suggestionText = mCandidateView.findViewById(R.id.suggestion_1);
        setCandidatesViewShown(true); 
        return mCandidateView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        setCandidatesViewShown(true); 
        if (suggestionText != null) {
            suggestionText.setText("Jv-NPU Active..."); 
            suggestionText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                k.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;
            case 10:
                ic.sendKeyEvent
