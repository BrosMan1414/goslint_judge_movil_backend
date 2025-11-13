# Dockerfile multi-stage para backend Spring Boot

# Etapa de build: compila el jar (sin tests)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY mvnw* .
COPY .mvn .mvn
# Pre-descarga dependencias (mejor caching)
RUN ./mvnw -B -q -DskipTests dependency:go-offline || mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
RUN ./mvnw -B -DskipTests clean package || mvn -B -DskipTests clean package

# Etapa de runtime: ejecuta el jar
FROM eclipse-temurin:21-jre
ENV APP_HOME=/app \
	JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:MinRAMPercentage=50 -XX:+HeapDumpOnOutOfMemoryError" \
	TZ=UTC
WORKDIR $APP_HOME
# Crear usuario no root
RUN useradd -r -s /bin/false appuser
COPY --from=build /workspace/target/*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
