package com.jv.ai_keyboard;

import android.content.Context;
import org.pytorch.executorch.LlamaModule;
import com.facebook.soloader.SoLoader;
import java.io.File;

public class JvNativeEngine {
    private LlamaModule llamaModule;

    public void initialize(Context context, String modelPath, String tokenizerPath) {
        try {
            // 1. Initialize SoLoader (ExecuTorch depends on native C++ libs)
            SoLoader.init(context, false);

            // 2. Load the Llama 3.2 1B Module
            // ExecuTorch uses .pte files and requires the tokenizer path separately
            llamaModule = new LlamaModule(
                modelPath,      // Path to your .pte file
                tokenizerPath,  // Path to your tokenizer.bin
                0.7f            // Temperature (randomness)
            );

            System.out.println("Sovereign: Llama 3.2 1B is active via ExecuTorch.");

        } catch (Exception e) {
            System.err.println("Sovereign Error: ExecuTorch failed to initialize.");
            e.printStackTrace();
        }
    }

    /**
     * Generates a response from the Llama brain.
     */
    public String generateResponse(String prompt) {
        if (llamaModule == null) return "Model not loaded.";
        
        // ExecuTorch handles the tokenization and loop internally
        return llamaModule.generate(prompt);
    }

    public void close() {
        if (llamaModule != null) {
            llamaModule.stop(); // Stops any active generation
        }
    }
}
