# 1단계: Build Stage
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 2단계: Runtime Stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 로그 저장용 디렉토리 만들기
RUN mkdir -p /logs

# JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행 시 로그를 /stock-logs/app.log에 저장

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-Xms256m", \
  "-Xmx600m", \
  "-XX:+UnlockDiagnosticVMOptions", \
  "-XX:NativeMemoryTracking=detail", \
  "-Dlogging.file.name=/logs/app.log", \
  "-jar", "app.jar"]