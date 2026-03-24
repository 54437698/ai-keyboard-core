#include <jni.h>
#include <string>

extern "C" {

// This name MUST match your Java package: com.jv.ai_keyboard
JNIEXPORT jstring JNICALL
Java_com_jv_ai_1keyboard_JointVentureInputService_getNPUPrediction(
        JNIEnv* env,
        jobject thiz,
        jstring context,
        jstring lang) {

    // 1. High-Performance String Extraction
    const char *nativeLang = env->GetStringUTFChars(lang, nullptr);
    const char *nativeContext = env->GetStringUTFChars(context, nullptr);
    
    std::string language = nativeLang;
    std::string input = nativeContext;
    std::string prediction;

    // 2. The Silicon Brain Logic
    // For now, simple bilingual toggling. 
    // Later, we plug the 'input' into a real NPU model.
    if (language == "spanish") {
        prediction = "mañana"; 
    } else {
        prediction = "tomorrow";
    }

    // 3. Essential Memory Release (Prevents the 'Fatal' Leak)
    env->ReleaseStringUTFChars(lang, nativeLang);
    env->ReleaseStringUTFChars(context, nativeContext);

    return env->NewStringUTF(prediction.c_str());
}

}
