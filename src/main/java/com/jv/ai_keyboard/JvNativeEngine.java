package com.jv.ai_keyboard;

import android.content.Context;

public class JvNativeEngine {

    // 1. This matches your "npuEngine.initialize(this)" call
    public void initialize(Context context) {
        // We will add the LiteRT-LM loading logic here later
    }

    // 2. This matches your "npuEngine.getPrediction(inputContext)" call
    public String getPrediction(String context) {
        // For now, return a placeholder so it compiles
        return "AI Ready"; 
    }
}
