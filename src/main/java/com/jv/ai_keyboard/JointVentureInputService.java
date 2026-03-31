package com.jv.ai_keyboard;

private boolean mCapsLock = false;
private long mLastShiftTime = 0;
private static final long DOUBLE_TAP_TIMEOUT = 300; // milliseconds
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent; // ADD THIS
import android.view.inputmethod.EditorInfo; // ADD THIS
import android.view.inputmethod.InputConnection;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private JvNativeEngine npuEngine = new JvNativeEngine();
    private KeyboardView kv;
    private Keyboard mKeyboard;
    private Keyboard mSymbols;

    @Override
    public void onCreate() {
        super.onCreate();
        npuEngine.initialize(this, "models/model.pte", "models/tokenizer.bin");
        // Pre-load both layouts
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

    private boolean isCaps = false; // Add this variable at the very top of your class (above onCreate)

   @Override
public void onKey(int primaryCode, int[] keyCodes) {
    InputConnection ic = getCurrentInputConnection();
    if (ic == null) return;

    switch(primaryCode) {
        case -1: // The SHIFT Key logic
            long now = System.currentTimeMillis();
            
            if (mCapsLock) {
                // If already locked, turn everything off
                mCapsLock = false;
                isCaps = false;
            } else if (isCaps) {
                // If shifted (single tap), check if this is a double tap
                if (now - mLastShiftTime < DOUBLE_TAP_TIMEOUT) {
                    mCapsLock = true;
                    isCaps = true;
                } else {
                    // Too slow for double tap? Turn it off
                    isCaps = false;
                }
            } else {
                // First tap: turn on single-tap shift
                isCaps = true;
            }

            mLastShiftTime = now;
            mKeyboard.setShifted(isCaps);
            kv.invalidateAllKeys();
            break;

        case -5: // Backspace
            ic.deleteSurroundingText(1, 0);
            break;

        case 10: // Enter
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
            break;

        case -2: // Symbols
            if (kv.getKeyboard() == mKeyboard) {
                kv.setKeyboard(mSymbols);
            } else {
                kv.setKeyboard(mKeyboard);
            }
            break;

        case 999: // Smiley
            ic.commitText("😂", 1);
            break;

        default:
            char code = (char) primaryCode;
            if (Character.isLetter(code) && isCaps) {
                code = Character.toUpperCase(code);
            }
            ic.commitText(String.valueOf(code), 1);
            
            // AUTO-LOWERCASE LOGIC
            // If we are in single-tap shift (not Caps Lock), drop back down
            if (isCaps && !mCapsLock) {
                isCaps = false;
                mKeyboard.setShifted(false);
                kv.invalidateAllKeys();
            }
    }
}
    // Required overrides
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
