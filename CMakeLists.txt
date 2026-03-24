cmake_minimum_required(VERSION 3.22.1)
project("jv_npu_engine")

# We are fusing the Bridge and the Pipeline into one engine
add_library( 
    jv_npu_engine
    SHARED
    core/inference_bridge.cpp
    core/npu_pipeline.cpp 
)

# Pull in the Android logging library for NPU debugging
find_library( 
    log-lib
    log 
)

# Link the engine to the system logs
target_link_libraries( 
    jv_npu_engine
    ${log-lib} 
)
