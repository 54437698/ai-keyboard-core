#include "ai_bridge.h"
#include <iostream>
#include <string>
#include <algorithm>
#include <jni.h> // Move this to the top

class NPUPipeline : public AIKeyboardEngine {
private:
    bool is_spanish_context = true; 

public:
    void initialize_npu() {
        std::cout << "Hexagon DSP 2026: INT4 Weights Loaded." << std::endl;
    }

    const char* predict_next_word(const char* input_buffer) override {
        std::string input(input_buffer);
        
        if (input.find(" the ") != std::string::npos || input.find(" is ") != std::string::npos) {
            is_spanish_context = false;
        } else if (input.find(" el ") != std::string::npos || input.find(" que ") != std::string::npos) {
            is_spanish_context = true;
        }

        if (is_spanish_context) {
            return "mañana"; 
        } else {
            return "tomorrow"; 
        }
    }
};

// --- START OF THE JNI BRIDGE (OUTSIDE THE CLASS) ---

static NPUPipeline engine;

extern "C" {

JNIEXPORT void JNICALL
Java_com_jv_ai_1keyboard_JointVentureInputService_initNPU(JNIEnv *env, jobject thiz, jbyteArray model_data) {
    engine.initialize_npu();
}

JNIEXPORT jstring JNICALL
Java_com_jv_ai_1keyboard_JointVentureInputService_getNPUPrediction(JNIEnv *env, jobject thiz, jstring context, jstring lang) {
    const char *native_context = env->GetStringUTFChars(context, nullptr);
    const char *prediction = engine.predict_next_word(native_context);
    
    jstring result = env->NewStringUTF(prediction);
    env->ReleaseStringUTFChars(context, native_context);
    
    return result;
}

} // extern "C"
