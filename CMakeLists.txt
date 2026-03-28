cmake_minimum_required(VERSION 3.22.1)
project("jv_npu_engine")

find_package(litert REQUIRED CONFIG)

add_library(jv_npu_engine SHARED 
    core/npu_pipeline.cpp 
)

find_library(log-lib log)

target_link_libraries(jv_npu_engine
    litert::litert
    litert::litert_gpu
    ${log-lib}
)
