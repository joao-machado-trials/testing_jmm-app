# Etapa 1 — Build
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar pom.xml e baixar dependências primeiro (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código-fonte e construir o JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2 — Runtime
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiar o JAR compilado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Permitir acesso nativo (evita warning do Tomcat)
ENV JAVA_OPTS="--enable-native-access=ALL-UNNAMED"

# Porta dinâmica (Render define a variável PORT)
ENV PORT=8080
EXPOSE 8080

# Executar Spring Boot e garantir que usa o PORT do ambiente
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]
