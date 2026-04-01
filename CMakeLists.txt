cmake_minimum_required(VERSION 3.22.1)
project("jv_ai_keyboard")

# This tells the compiler to take jv_npu_engine.cpp and turn it into a library
add_library(jv_npu_engine SHARED jv_npu_engine.cpp)

# This adds the Android logging library so we can see Llama's output in the logs
find_library(log-lib log)

# This welds the library and the logger together
target_link_libraries(jv_npu_engine ${log-lib})
