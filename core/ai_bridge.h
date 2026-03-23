// AI Bridge: Logical interface for NPU inference
#ifndef AI_BRIDGE_H
#define AI_BRIDGE_H

class AIKeyboardEngine {
public:
    // Added 'virtual' and '= 0' to make it a Pure Virtual Function
    virtual const char* predict_next_word(const char* input_buffer) = 0;
    
    virtual void set_language_context(int language_id) {}
    
    // Virtual destructor is mandatory for clean memory
    virtual ~AIKeyboardEngine() {} 
};

#endif
