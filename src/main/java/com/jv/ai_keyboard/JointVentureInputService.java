package com.jv.ai_keyboard;

import java.io.File;
import android.util.Log;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    
    private JvNativeEngine npuEngine = new JvNativeEngine();
    private KeyboardView kv;
    private Keyboard mKeyboard;
    private Keyboard mSymbols;
    
    private boolean isCaps = false; 
    private boolean mCapsLock = false;
    private long mLastShiftTime = 0;
    private static final long DOUBLE_TAP_TIMEOUT = 300; 

    @Override
    public void onCreate() {
        super.onCreate();
        
        final JointVentureInputService serviceContext = this;

        // Boot the Brain in the background to avoid UI lag
        new Thread(() -> {
            try {
                File modelFile = new File(serviceContext.getExternalFilesDir(null), "model.pte");
                if (modelFile.exists()) {
                    npuEngine.initialize(serviceContext, modelFile.getAbsolutePath(), "tokenizer.bin");
                    Log.i("JV_AI", "NPU Engine Warm and Ready.");
                } else {
                    Log.w("JV_AI", "Model file not found. Running in standard mode.");
                }
            } catch (Exception e) {
                Log.e("JV_AI", "NPU Boot Failure", e);
            }
        }).start();

        mKeyboard = new Keyboard(this, R.xml.qwerty);
        mSymbols = new Keyboard(this, R.xml.symbols);
    }

    @Override
    public View onCreateInputView() {
        View mInputView = getLayoutInflater().inflate(R.layout.input, null);
        kv = mInputView.findViewById(R.id.keyboard);
        kv.setKeyboard(mKeyboard);
        kv.setOnKeyboardActionListener(this);
        return mInputView;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null || primaryCode == 0) return;

        switch(primaryCode) {
            case -1: 
                handleShiftLogic();
                break;
            case -5: // DELETE
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                break;
            case -4: // DONE / ENTER
            case 10: 
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
            case -2: // Layout Switch (?123 / ABC)
                toggleKeyboardLayout();
                break;
            case -10: // Emoji Button (Updated from 999)
                ic.commitText("😊", 1);
                break;
            case -100: // G-Button (AI Settings / Import)
                // For now, let's just confirm it's working
                ic.commitText("[AI Ready]", 1); 
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && (isCaps || mCapsLock)) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
                
                // --- AI TRIGGER WITH SAFETY ---
                final String currentContext = ic.getTextBeforeCursor(20, 0).toString();
                new Thread(() -> {
                    try {
                        // CRITICAL: Only call the NPU if the model file was actually found
                        // This prevents the "Crash on Tap" with the 1MB fake model!
                        if (npuEngine != null && new File(getExternalFilesDir(null), "model.pte").exists()) {
                            String suggestion = npuEngine.getPrediction(currentContext);
                            if (suggestion != null) Log.d("JV_AI", "Llama: " + suggestion);
                        }
                    } catch (Exception e) {
                        Log.e("JV_AI", "NPU Prediction failed - bypassing to prevent crash", e);
                    }
                }).start();

                // Reset shift if not in Caps Lock
                if (isCaps && !mCapsLock) {
                    isCaps = false;
                    mKeyboard.setShifted(false);
                    kv.invalidateAllKeys();
                }
        }
    }

    private void handleShiftLogic() {
        long now = System.currentTimeMillis();
        if (mCapsLock) {
            mCapsLock = false;
            isCaps = false;
        } else if (isCaps && (now - mLastShiftTime < DOUBLE_TAP_TIMEOUT)) {
            mCapsLock = true;
            isCaps = true;
        } else {
            isCaps = !isCaps;
        }
        mLastShiftTime = now;
        mKeyboard.setShifted(isCaps);
        kv.invalidateAllKeys();
    }

    private void toggleKeyboardLayout() {
        if (kv.getKeyboard() == mKeyboard) {
            kv.setKeyboard(mSymbols);
        } else {
            kv.setKeyboard(mKeyboard);
        }
    }

    // Required Overrides
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {} 
    @Override public void swipeUp() {}   
}
