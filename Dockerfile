# Use Java 17 como base
FROM eclipse-temurin:17-jdk-alpine

# Instalar Maven e bash
RUN apk add --no-cache maven bash

# Definir diretório de trabalho
WORKDIR /app

# Copiar pom.xml e baixar dependências offline (cache de build)
COPY pom.xml .
RUN mkdir -p src && echo "" > src/placeholder
RUN mvn dependency:go-offline

# Copiar todo o código da aplicação
COPY . .

# Build do projeto (skip testes para acelerar)
RUN mvn clean package -DskipTests spring-boot:run -Dspring-boot.run.jvmArguments="--enable-native-access=ALL-UNNAMED"

# Expor a porta que Spring Boot vai usar
EXPOSE 8080

# Comando para iniciar a aplicação
CMD ["java", "-jar", "target/Base_Project-1.0.0.jar"]
