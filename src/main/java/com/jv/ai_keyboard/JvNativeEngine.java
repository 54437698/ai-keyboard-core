package com.jv.ai_keyboard;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import com.google.ai.edge.litert.CompiledModel;
import java.io.IOException;

public class JvNativeEngine {
    private CompiledModel llamaModel;

    /**
     * Wakes up the Llama 3.2 1B brain on the Snapdragon 8 Elite NPU.
     */
    public void initialize(Context context) {
        try {
            // 1. Locate the model file we downloaded in the Workflow
            AssetFileDescriptor afd = context.getAssets().openFd("models/model.tflite");

            // 2. Set the 'Sovereign' Options: 
            // We tell LiteRT to prioritize the NPU (Hexagon), but fallback to GPU if needed.
            CompiledModel.Options options = CompiledModel.Options.builder()
                .setAccelerator(CompiledModel.Options.Accelerator.NPU)
                .setAccelerator(CompiledModel.Options.Accelerator.GPU)
                .build();

            // 3. Compile and Load. On the S25 Ultra, this happens in milliseconds.
            llamaModel = CompiledModel.create(afd, options);
            
            System.out.println("Sovereign: Llama 3.2 is online on the NPU.");

        } catch (IOException e) {
            System.err.println("Sovereign Error: Could not find the Llama brain file!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Sovereign Error: NPU initialization failed.");
            e.printStackTrace();
        }
    }

    public void close() {
        if (llamaModel != null) {
            llamaModel.close();
        }
    }
}
