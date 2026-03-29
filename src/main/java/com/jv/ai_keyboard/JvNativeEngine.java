package com.jv.ai_keyboard;

import android.content.Context;
import org.pytorch.Module;

public class JvNativeEngine {
    private Module llamaModule;

    // This matches the 3 arguments (Context, String, String)
    public void initialize(Context context, String modelPath, String tokenizerPath) {
        try {
            // Module.load is the stable way to wake up the AI
            llamaModule = Module.load(modelPath);
            System.out.println("Sovereign: Engine Initialized.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateResponse(String prompt) {
        if (llamaModule == null) return "Engine not ready.";
        return "AI: " + prompt;
    }
}
