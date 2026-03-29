package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.view.View;

public class JointVentureInputService extends InputMethodService {
    private JvNativeEngine npuEngine = new JvNativeEngine();

    @Override
    public void onCreate() {
        super.onCreate();
        // FIXED: Now we are sending exactly 3 things: (this, string, string)
        npuEngine.initialize(this, "model.pte", "tokenizer.bin");
    }

    @Override
    public View onCreateInputView() {
        // Your keyboard UI code goes here
        return null; 
    }
}
