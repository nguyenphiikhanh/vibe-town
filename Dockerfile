# ==========================================
# STAGE 1: BUILD MÃ NGUỒN BẰNG MAVEN
# ==========================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# 1. Copy pom.xml trước để tận dụng Docker Cache layer tải dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy toàn bộ mã nguồn và build jar + copy dependencies
COPY src ./src
RUN mvn clean package -DskipTests && \
    mvn dependency:copy-dependencies -DoutputDirectory=target/libs

# ==========================================
# STAGE 2: RUNTIME SIÊU NHẸ (CHỈ ~150MB)
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Cài đặt múi giờ Việt Nam
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime && \
    echo "Asia/Ho_Chi_Minh" > /etc/timezone

# Copy file jar đã build và các thư viện dependency từ Stage 1
COPY --from=builder /build/target/*.jar ./app.jar
COPY --from=builder /build/target/libs/ ./libs/

# Mount tài nguyên và cấu hình từ máy host (tránh nhúng tĩnh vào image)
# res/, config.properties, database.properties

EXPOSE 19128

# Chạy server với tối ưu RAM container
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xms256m", "-Xmx1g", "-cp", "app.jar:libs/*", "avatar.server.Avatar"]
