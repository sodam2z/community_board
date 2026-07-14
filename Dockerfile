# syntax=docker/dockerfile:1

# stage 1: 빌드 단계
# Java 25 버전으로 컴파일
FROM eclipse-temurin:25.0.3_9-jdk-alpine AS builder

# Docker 이미지 빌드 작업 디렉터리
WORKDIR /app

# 프로젝트 실행 권한 부여 -> ./gradlew 컨테이너 안에서 실행 가능하게 만들기
COPY --chmod=0755 gradlew gradlew

# gradle wrapper가 사용하는 wrapper jar, 설정 파일 복사
COPY gradle/ gradle/

# 의존성 정의 파일 우선 복사
COPY settings.gradle build.gradle ./

# 의존성 먼저 다운로드
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon dependencies

# 애플리케이션 소스 코드 복사
COPY src/ src/

# spring boot 실행 가능한 jar 파일 생성
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon bootJar


# stage 2 : 실행 단계
# 컴파일 도구가 필요 없으므로 jre 이미지 사용
FROM eclipse-temurin:25.0.3_9-jre-alpine

# 애플리케이션 실행 디렉터리
WORKDIR /app

# root 대신에 애플리케이션 전용 non-root 사용자, 그룹 생성
RUN addgroup -S app && adduser -S app -G app

# builder 단계에서 만든 jar 파일만 최종 이미지로 복사, 소유권 - app 사용자
COPY --from=builder --chown=app:app /app/build/libs/*.jar app.jar

# 자바 프로세스는 root가 아닌 app 사용자로 실행
USER app

# ALB 뒤에서 컨테이너가 HTTP로 응답할 포트 문서화
EXPOSE 8080

#java 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]