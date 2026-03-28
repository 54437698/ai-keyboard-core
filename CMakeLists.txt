cmake_minimum_required(VERSION 3.22.1)
project("jv_npu_engine")

# 1. This finds the 8-bit Google LiteRT engine we added in Gradle
find_package(litert REQUIRED CONFIG)

# 2. Create your keyboard's NPU library
add_library(
    jv_npu_engine
    SHARED
    core/npu_pipeline.cpp 
)

# 3. Find the Android logging library
find_library(log-lib log)

# 4. STITCH THEM TOGETHER (The Sovereign Link)
target_link_libraries(
    jv_npu_engine
    litert::litert    # This adds the 19MB of Google's AI "Muscle"
    ${log-lib}
)
