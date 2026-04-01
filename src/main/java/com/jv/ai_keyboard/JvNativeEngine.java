package com.jv.ai_keyboard;

import android.content.Context;

public class JvNativeEngine {
    static {
        // This MUST match the name in CMakeLists.txt
        System.loadLibrary("jv_npu_engine");
    }

    // These link directly to the functions in jv_npu_engine.cpp
    public native boolean initialize(Context context, String modelPath, String tokenizerPath);
    public native String getPrediction(String context);
}
