package com.jv.ai_keyboard;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // This is just a placeholder screen to stop the crash
        TextView tv = new TextView(this);
        tv.setText("JV NPU Settings\nStatus: Online\nDebug: Enabled");
        tv.setPadding(50, 50, 50, 50);
        setContentView(tv);
    }
}
