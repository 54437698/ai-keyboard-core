/**
 * Joint Venture: JNI Nervous System
 * Connects the Android OS to our high-speed NPU Core.
 */

#include <jni.h>
#include <string>
#include "../core/ai_bridge.h"
#include "../core/inference_bridge.cpp"

extern "C" JNIEXPORT jstring JNICALL
Java_com_jointventure_keyboard_NativeInterface_getPrediction(
    JNIEnv* env, 
    jobject /* this */, 
    jstring input) {
    
    // 1. Convert the OS-level string to C++
    const char* nativeString = env->GetStringUTFChars(input, 0);
    
    // 2. Initialize our validated Inference Engine
    InferenceBridge engine;
    const char* prediction = engine.predict_next_word(nativeString);
    
    // 3. Hand the prediction back to the OS
    jstring result = env->NewStringUTF(prediction);
    
    // 4. Clean up memory to protect the battery
    env->ReleaseStringUTFChars(input, nativeString);
    
    return result;
}
