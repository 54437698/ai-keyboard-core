cmake_minimum_required(VERSION 3.22.1)
project("jv_npu_engine")

# 1. Tell CMake where the ExecuTorch headers are (from the Gradle build)
# Usually found in the exploded AAR directory
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include) 

# 2. Define your engine
add_library(jv_npu_engine SHARED core/npu_pipeline.cpp)

# 3. Find the Android logging library
find_library(log-lib log)

# 4. Link everything
target_link_libraries(
    jv_npu_engine
    ${log-lib}
    # These names must match the .so files provided by the ExecuTorch AAR
    executorch
    executorch_nnapi_backend 
)
