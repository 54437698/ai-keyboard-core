package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

   @Override
    public View onCreateInputView() {
        Log.d("JV_DEBUG", "onCreateInputView: Forcing Focusable Container");

        // 1. Create the container
        LinearLayout container = new LinearLayout(this);
        container.setBackgroundColor(Color.MAGENTA); // KEEP THE PINK SO WE CAN SEE IT
        
        // 2. THE FORCE: Set a hard minimum height in pixels (approx 350dp)
        container.setMinimumHeight(900); 

        // 3. THE FIX: Android 16 requires the view to be "Interactive"
        container.setClickable(true);
        container.setFocusable(true);
        container.setFocusableInTouchMode(true); // Tells the OS we can receive touches

        // 4. Content
        TextView tv = new TextView(this);
        tv.setText("JV NPU: SYSTEM ESTABLISHED");
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(18);
        tv.setPadding(40, 40, 40, 40);
        container.addView(tv);

        return container;
    }

    // Empty Mandatory Methods to make the compiler happy
    @Override public void onKey(int primaryCode, int[] keyCodes) {}
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
