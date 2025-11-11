# Use Java 17 (Eclipse Temurin) como base
FROM eclipse-temurin:17-jdk-alpine

# Definir diretório de trabalho
WORKDIR /app

# Copiar o pom.xml e baixar dependências (para cache de builds)
COPY pom.xml .
# Criar pasta src temporária para o Maven offline
RUN mkdir -p src && echo "" > src/placeholder
# Baixar dependências offline
RUN ./mvnw dependency:go-offline || mvn dependency:go-offline

# Copiar todo o código
COPY . .

# Build do projeto
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Expor a porta que o Spring Boot vai usar
EXPOSE 8080

# Comando para iniciar a aplicação
CMD ["java", "-jar", "target/base-0.0.1-SNAPSHOT.jar"]
