cmake_minimum_required(VERSION 3.22.1)
project("jv_npu_engine")

add_library(
    jv_npu_engine
    SHARED
    core/npu_pipeline.cpp 
)

find_library(log-lib log)

target_link_libraries(
    jv_npu_engine
    ${log-lib}
)
