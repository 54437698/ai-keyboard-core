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

        // Boot the Brain in the background
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
        
        // 1. ADD VIBRATION
        if (kv != null) {
            kv.performHapticFeedback(
                android.view.HapticFeedbackConstants.KEYBOARD_TAP,
                android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            );
        }

        if (ic == null) return;

        switch (primaryCode) {
            case -100: // THE G-BUTTON: Launch Settings
                android.content.Intent intent = new android.content.Intent(this, SettingsActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
                
            case -5: // Delete
                ic.deleteSurroundingText(1, 0);
                break;
                
            case -1: // Shift
                handleShiftLogic();
                break;
            
            case -2: // Symbols
                toggleKeyboardLayout();
                break;
                
            case -4: // Enter
                ic.sendKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER));
                break;

            case 32: // Space
                ic.commitText(" ", 1);
                break;

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
                
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
