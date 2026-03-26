package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;

public class JointVentureInputService extends InputMethodService {
    private JvNativeEngine npuEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        npuEngine = new JvNativeEngine();
        // We will call the setup here - matching the JvNativeEngine class
        npuEngine.setup(this); 
    }

    @Override
    public View onCreateInputView() {
        // Return a dummy view for now to get the green tick
        return new View(this);
    }
}
