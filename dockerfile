# -------- STAGE 1: BUILD --------
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia só o pom primeiro (melhora cache)
COPY pom.xml .

# Baixa dependências
RUN mvn dependency:go-offline

# Copia o resto do projeto
COPY src ./src

# Builda o jar
RUN mvn clean package -DskipTests


# -------- STAGE 2: RUNTIME --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/target/*.jar app.jar

# Porta (documentação apenas)
EXPOSE 8080

# Variáveis úteis
ENV JAVA_OPTS=""

# Comando de inicialização
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]