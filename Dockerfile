# syntax=docker/dockerfile:1

# stage 1: 빌드 단계
FROM eclipse-temurin:25.0.3_9-jdk-alpine AS builder

WORKDIR /app

COPY --chmod=0755 gradlew gradlew
COPY gradle/ gradle/
COPY settings.gradle build.gradle ./
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon dependencies
COPY src/ src/
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon bootJar

# 빌드된 jar를 고정된 이름으로 통일
RUN cp build/libs/*.jar app.jar

# fat jar를 압축 해제된 형태로 풀어냄 (AOT Cache가 classpath 순서에 민감하기 때문)
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# 훈련 실행 - AOT Cache(app.aot) 생성
WORKDIR /app/extracted
RUN java -XX:AOTCacheOutput=app.aot \
         -Dspring.profiles.active=training \
         -Dspring.context.exit=onRefresh \
         -jar app.jar; \
    test -f app.aot


# stage 2 : 실행 단계
# jre -> jdk
# AOT Cache가 훈련 때와 다른 JVM으로 판단하여 캐시 거부 문제 해결을 위함이다.
FROM eclipse-temurin:25.0.3_9-jdk-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

# 압축 해제된 애플리케이션 전체(app.jar + lib/ + app.aot)를 그대로 복사
COPY --from=builder --chown=app:app /app/extracted /app

USER app

EXPOSE 8080

# AOT Cache를 사용해 시작
ENTRYPOINT ["java", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
