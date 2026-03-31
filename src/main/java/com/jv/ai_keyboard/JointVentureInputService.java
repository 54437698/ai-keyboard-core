package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    
    // --- State Variables ---
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
        npuEngine.initialize(this, "models/model.pte", "models/tokenizer.bin");
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

            case -5: 
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                break;

            case 10: 
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;

            case -2: 
                toggleKeyboardLayout();
                break;

            case 999: 
                ic.commitText("😂", 1);
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

    // --- Required Interface Overrides ---
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {} 
    @Override public void swipeUp() {}   
}
