package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.util.Log;

public class JointVentureInputService extends InputMethodService 
        implements KeyboardView.OnKeyboardActionListener {

    // 1. THE BRIDGE: Connects to the C++ Engine
    static {
        try {
            System.loadLibrary("jv_npu_engine");
            Log.d("JV_DEBUG", "C++ Engine Loaded Successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e("JV_DEBUG", "CRITICAL: Could not load jv_npu_engine: " + e.getMessage());
        }
    }

    // 2. THE NATIVE METHODS
    public native String getNPUPrediction(String input);
    public native void initNPU(byte[] modelData);

    // 3. THE INTERFACE: Loads the QWERTY keys
    @Override
    public View onCreateInputView() {
        KeyboardView kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        Keyboard keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this); 
        return kv;
    }

    // 4. THE STARTUP
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("JV_DEBUG", "########################################");
        Log.e("JV_DEBUG", "### THE SOVEREIGN BRAIN IS STARTING  ###");
        Log.e("JV_DEBUG", "########################################");
    }

    // 5. THE NERVOUS SYSTEM: Sending characters to the screen
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        
        if (ic != null) {
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                ic.deleteSurroundingText(1, 0);
            } else {
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
            }
        }
    }

    // MANDATORY METHODS: Required to stay here to keep Android happy
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
