#include <jni.h>
#include <string>
#include <android/log.h>

// Debugging macro so we can see what's happening in Logcat
#define LOG_TAG "JV_NPU_CORE"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

/**
 * IMPORTANT: The name below is mapped to:
 * Package: com.jv.ai_keyboard
 * Class: JointVentureInputService
 * Function: getNPUPrediction
 * Note: _1keyboard is how JNI handles the underscore in "ai_keyboard"
 */
JNIEXPORT jstring JNICALL
Java_com_jv_ai_1keyboard_JointVentureInputService_getNPUPrediction(JNIEnv *env, jobject thiz, jstring input) {
    const char *nativeInput = env->GetStringUTFChars(input, 0);
    
    // Placeholder for actual NPU logic
    std::string prediction = "Sovereign Ready"; 
    
    env->ReleaseStringUTFChars(input, nativeInput);
    return env->NewStringUTF(prediction.c_str());
}

/**
 * Handles the NPU initialization call from Java
 */
JNIEXPORT void JNICALL
Java_com_jv_ai_1keyboard_JointVentureInputService_initNPU(JNIEnv *env, jobject thiz, jbyteArray model_data) {
    LOGE("NPU Pipeline Initializing...");
    // Logic for loading .tflite or .pte models goes here
}

}
