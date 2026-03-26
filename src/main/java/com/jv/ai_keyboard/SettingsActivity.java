package com.jv.ai_keyboard;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override
    protected void Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Basic UI for now
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView title = new TextView(this);
        title.setText("JV NPU Debug Settings");
        title.setTextSize(24);
        layout.addView(title);

        CheckBox debugToggle = new CheckBox(this);
        debugToggle.setText("Enable Detailed NPU Logging");
        debugToggle.setChecked(true); // Default to ON for debugging
        layout.addView(debugToggle);

        setContentView(layout);
    }
}
