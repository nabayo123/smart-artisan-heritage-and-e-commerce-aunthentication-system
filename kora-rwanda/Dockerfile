# =========================================================
# Stage 1 — BUILD: Maven + Java 17
# =========================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and POM first (layer-cache dependencies)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Make wrapper executable
RUN chmod +x mvnw

# Download all dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the fat JAR, skip tests (tests run in CI)
RUN ./mvnw clean package -DskipTests -B

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
