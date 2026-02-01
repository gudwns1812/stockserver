#!/bin/bash

# jemalloc 라이브러리 경로 설정
export LD_PRELOAD="/usr/local/lib/libjemalloc.so.2"

# jemalloc 구성 옵션 설정 (쉼표로 구분)
export MALLOC_CONF="prof:true,lg_chunk:20,percpu_arena:disabled,metadata_thp:never,lg_prof_sample:17,prof_active:true,prof_prefix:/jemalloc_profiles/jemalloc_profile"

# Java 애플리케이션 실행
java \
  "-XX:+UseContainerSupport" \
  "-Xms256m" \
  "-Xmx256m" \
  "-Xss256k" \
  "-XX:CompressedClassSpaceSize=32m" \
  "-XX:ReservedCodeCacheSize=64m" \
  "-XX:+UnlockDiagnosticVMOptions" \
  "-XX:NativeMemoryTracking=detail" \
  "-Dlogging.file.name=/logs/app.log" \
  "-jar" "app.jar"
