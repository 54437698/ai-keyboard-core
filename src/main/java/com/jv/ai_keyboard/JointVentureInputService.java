package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.WindowManager;
import android.util.Log;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    // 3. THE STARTUP & WINDOW FORCE
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("JV_DEBUG", "### THE SOVEREIGN BRAIN IS STARTING ###");
        
        // Android 16 Alpha Fix: Ensure the window isn't dimmed or invisible
        if (getWindow() != null && getWindow().getWindow() != null) {
            getWindow().getWindow().setDimAmount(0);
        }
    }

    @Override
    public void onWindowShown() {
        super.onWindowShown();
        // The "God Move": Force opacity to 1.0 to stop the -0.015027 clamp error
        if (getWindow() != null && getWindow().getWindow() != null) {
            WindowManager.LayoutParams lp = getWindow().getWindow().getAttributes();
            lp.alpha = 1.0f; 
            getWindow().getWindow().setAttributes(lp);
            Log.d("JV_DEBUG", "Window Alpha Forced to 1.0");
        }
    }

    // 4. THE INTERFACE: Loads keys OR Magenta Fallback
    @Override
    public View onCreateInputView() {
        Log.d("JV_DEBUG", "Attempting to inflate Input View...");
        try {
            // Attempt standard XML inflation
            View v = getLayoutInflater().inflate(R.layout.input, null);
            
            if (v instanceof KeyboardView) {
                KeyboardView kv = (KeyboardView) v;
                Keyboard k = new Keyboard(this, R.xml.qwerty);
                kv.setKeyboard(k);
                kv.setOnKeyboardActionListener(this);
                return kv;
            }
            return v;
        } catch (Exception e) {
            Log.e("JV_DEBUG", "Inflation Failed: " + e.getMessage());
            
            // EMERGENCY FALLBACK: The Pink Test
            LinearLayout fallback = new LinearLayout(this);
            fallback.setBackgroundColor(Color.MAGENTA);
            TextView tv = new TextView(this);
            tv.setText("JV NPU CORE: XML INFLATION ERROR");
            tv.setTextColor(Color.WHITE);
            tv.setPadding(20, 20, 20, 20);
            fallback.addView(tv);
            return fallback;
        }
    }

    // 5. THE NERVOUS SYSTEM: Sending characters to the screen
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (primaryCode == Keyboard.KEYCODE_
