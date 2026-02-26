# ==================================================
# STAGE 1: Build
# ==================================================
FROM docker.io/eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copiar archivos de Gradle primero (cache de dependencias)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos al wrapper
RUN chmod +x ./gradlew

# Descargar dependencias (se cachean si build.gradle no cambia)
RUN ./gradlew dependencies --no-daemon || true

# Copiar el código fuente
COPY src src

# Compilar y generar el JAR (sin tests para build más rápido)
RUN ./gradlew bootJar --no-daemon -x test

# ==================================================
# STAGE 2: Runtime (imagen ligera)
# ==================================================
FROM docker.io/eclipse-temurin:25-jre

WORKDIR /app

# Crear usuario no-root por seguridad
RUN groupadd -r spring && useradd -r -g spring spring

# Copiar el JAR generado en el stage anterior
COPY --from=builder /app/build/libs/ApiSensoresDAW-0.0.1-SNAPSHOT.jar app.jar

# Cambiar propietario
RUN chown spring:spring app.jar

USER spring

# Puerto por defecto de Spring Boot
EXPOSE 8080

# Variables de entorno configurables
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod

# Arrancar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
