package com.jointventure.keyboard;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;

public class JointVentureInputService extends InputMethodService {
    
    // Loading our surgical C++ core
    static {
        System.loadLibrary("native_bridge");
    }

    // This is the gatekeeper to our NPU
    private native String getPrediction(String input);

    @Override
    public View onCreateInputView() {
        // This is where we will eventually inflate our JSON layout
        // For now, it's the anchor for our C++ logic
        return super.onCreateInputView();
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        // "Joint Venture" logic: Reset the NPU context when the user starts a new text field
    }
}
