package com.jv.ai_keyboard; // KEEP THIS - Matches your Manifest/Gradle

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

public class JointVentureInputService extends InputMethodService {

    private KeyboardLayoutParser parser;
    private String currentLang = "english";

    // --- 1. Lifecycle ---
    @Override
    public void onCreate() {
        super.onCreate();
        parser = new KeyboardLayoutParser(this, this);
    }

    // --- 2. The Keyboard Keys (Eyes & Hands) ---
    @Override
    public View onCreateInputView() {
        // This uses the JSON Parser we built to show the keys
        return parser.parseAndCreateView(currentLang);
    }

    // --- 3. The Prediction Toolbar (The Face) ---
    @Override
    public View onCreateCandidatesView() {
        // Ensure you have prediction_toolbar.xml in res/layout/
        View mCandidateView = getLayoutInflater().inflate(R.layout.prediction_toolbar, null);
        
        mCandidateView.findViewById(R.id.btn_mic).setOnClickListener(v -> {
            Log.d("JV_UI", "NPU Mic Pressed - Hexagon V79 Listening");
        });

        return mCandidateView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        setCandidatesViewShown(true); 
    }

    // --- 4. Language & Typing Logic ---
    public void toggleLanguage() {
        currentLang = currentLang.equals("english") ? "spanish" : "english";
        setInputView(onCreateInputView());
    }

    public void handleCharacter(char code) {
        if (getCurrentInputConnection() != null) {
            getCurrentInputConnection().commitText(String.valueOf(code), 1);
        }
    }

    // --- 5. NATIVE BRIDGE ---
    static {
        System.loadLibrary("npu_pipeline"); // KEEP THIS - Matches your CMake
    }

    public native void initNPU(byte[] modelData);
    public native String getNPUPrediction(String context, String lang);
}
