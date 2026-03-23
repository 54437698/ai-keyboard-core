// AI Bridge: Joint Venture Logical Interface
#ifndef AI_BRIDGE_H
#define AI_BRIDGE_H

class AIKeyboardEngine {
public:
    // 'virtual' allows the NPU to override this logic.
    // '= 0' makes it a pure interface (Mandatory implementation).
    virtual const char* predict_next_word(const char* input_buffer) = 0;
    
    virtual void set_language_context(int language_id) {}

    // Destructor must be virtual for clean memory management
    virtual ~AIKeyboardEngine() {}
};

#endif
