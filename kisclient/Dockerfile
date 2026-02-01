# 1단계: Build Stage
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 2단계: Runtime Stage
FROM eclipse-temurin:17-jdk-jammy

# jemalloc 설치
RUN apt-get update && \
    apt-get install -y autoconf make gcc g++ git libtool && \
    git clone https://github.com/jemalloc/jemalloc.git /tmp/jemalloc && \
    cd /tmp/jemalloc && \
    git checkout 5.3.0 && \
    ./autogen.sh && \
    ./configure --enable-prof && \
    make -j$(nproc) && \
    make install && \
    ldconfig && \
    rm -rf /tmp/jemalloc && \
    apt-get remove -y autoconf make gcc g++ git libtool && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 디렉토리 생성 (권한 확실히 설정)
RUN mkdir -p /logs /jemalloc_profiles && \
    chmod 755 /logs /jemalloc_profiles

# JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경변수 설정 (스크립트 없이)
ENV LD_PRELOAD="/usr/local/lib/libjemalloc.so.2"
ENV MALLOC_CONF="prof:true,lg_prof_sample:17,prof_active:true,prof_prefix:/jemalloc_profiles/heap,prof_final:true,lg_chunk:20,percpu_arena:disabled,metadata_thp:never"

# Java 실행 (ENTRYPOINT 직접 사용)
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-Xms256m", \
    "-Xmx256m", \
    "-Xss256k", \
    "-XX:CompressedClassSpaceSize=32m", \
    "-XX:ReservedCodeCacheSize=64m", \
    "-XX:+UnlockDiagnosticVMOptions", \
    "-XX:NativeMemoryTracking=detail", \
    "-Dlogging.file.name=/logs/app.log", \
    "-jar", "app.jar"]
