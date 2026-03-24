package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.util.Log;

// 🚩 Added KeyboardView.OnKeyboardActionListener to the 'implements' list
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
        // Load the "Skin" (the View)
        KeyboardView kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        
        // Load the "Map" (the XML keys)
        Keyboard keyboard = new Keyboard(this, R.xml.qwerty);
        
        // Put the Map onto the Skin
        kv.setKeyboard(keyboard);
        
        // 🚩 Link the listener so key presses actually do something
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

    // --- MANDATORY KEYBOARD LISTENER METHODS ---
    // These must exist because we 'implements KeyboardView.OnKeyboardActionListener'
    
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        // This is where the magic happens! 
        // We'll tell it to send characters to the input field here next.
    }

    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
