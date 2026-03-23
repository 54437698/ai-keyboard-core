// AI Bridge: Logical interface for NPU inference
// Targeted for Qualcomm Hexagon DSP 2026

#ifndef AI_BRIDGE_H
#define AI_BRIDGE_H

class AIKeyboardEngine {
public:
    // Takes the last few characters and predicts the next Spanish word
    const char* predict_next_word(const char* input_buffer);
    
    // Switch between English and Spanish context
    void set_language_context(int language_id);
};

#endif
