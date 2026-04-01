cmake_minimum_required(VERSION 3.22.1)
project("jv_ai_keyboard")

add_library(jv_npu_engine SHARED src/main/cpp/jv_npu_engine.cpp)

find_library(log-lib log)
target_link_libraries(jv_npu_engine ${log-lib})
