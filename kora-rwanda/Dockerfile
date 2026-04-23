# =========================================================
# Stage 1 — BUILD: Maven + Java 17
# =========================================================
FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app

# Copy POM first (layer-cache dependencies)
COPY pom.xml .

# Download all dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the fat JAR, skip tests (tests run in CI)
RUN mvn clean package -DskipTests -B

# =========================================================
# Stage 2 — RUNTIME: minimal JRE
# =========================================================
FROM eclipse-temurin:17-jre-alpine AS runtime

# Non-root user for security
RUN addgroup -S kora && adduser -S kora -G kora

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/kora-*.jar app.jar

# Create uploads directory (persisted via Render Disk)
RUN mkdir -p /app/uploads && chown -R kora:kora /app

USER kora

# Render injects PORT env variable; default 8080 for local
ENV PORT=8080
EXPOSE ${PORT}

# JVM tuning for container environments
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
