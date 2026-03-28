#include <jni.h>
#include <string>
#include <android/log.h>
#include "tensorflow/lite/c/c_api.h" // The LiteRT Engine Header

// Debugging macro
#define LOG_TAG "JV_NPU_CORE"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

/**
 * Mapped to: com.jv.ai_keyboard.JointVentureInputService.getNPUPrediction
 */
JNIEXPORT jstring JNICALL
Java_com_jv_ai_1keyboard_JointVentureInputService_getNPUPrediction(JNIEnv *env, jobject thiz, jstring input) {
    const char *nativeInput = env->GetStringUTFChars(input, 0);
    
    // Get the version from the actual 19MB engine to prove it's linked
    const char* engineVersion = TfLiteVersion();
    
    // Combine your "Sovereign" message with the engine status
    std::string prediction = "Sovereign Ready (LiteRT " + std::string(engineVersion) + ")"; 
    
    LOGI("Prediction requested for: %s", nativeInput);
    
    env->ReleaseStringUTFChars(input, nativeInput);
    return env->NewStringUTF(prediction.c_str());
}

/**
 * Mapped to: com.jv.ai_keyboard.JointVentureInputService.initNPU
 */
JNIEXPORT void JNICALL
Java_com_jv_ai_1keyboard_JointVentureInputService_initNPU(JNIEnv *env, jobject thiz, jbyteArray model_data) {
    LOGI("NPU Pipeline Initializing with LiteRT Engine...");
    
    // This is where the 19MB engine starts breathing.
    // For now, we just log that the "Sovereign" bridge is open.
    const char* version = TfLiteVersion();
    LOGI("Link established: LiteRT v%s is under the hood.", version);
}

}
