#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "JV_NPU_NATIVE"

extern "C" JNIEXPORT jboolean JNICALL
Java_com_jv_ai_keyboard_JvNativeEngine_initialize(JNIEnv* env, jobject thiz, jobject context, jstring model_path, jstring tokenizer_path) {
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "NPU Engine Initializing...");
    return JNI_TRUE; 
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jv_ai_keyboard_JvNativeEngine_getPrediction(JNIEnv* env, jobject thiz, jstring context) {
    return env->NewStringUTF("Llama is thinking...");
}
