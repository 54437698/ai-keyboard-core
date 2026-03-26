package com.jv.ai_keyboard;

public class JvNativeEngine {
    static {
        // This loads your "jv_npu_engine.so" file from the JNI folder
        System.loadLibrary("jv_npu_engine");
    }

    // These are the "Direct Lines" to your C++ code
    public native String getPrediction(String currentText);
    public native void initEngine(byte[] modelData);
}
