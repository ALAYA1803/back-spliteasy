

# Etapa única: compila y ejecuta en la misma imagen
FROM eclipse-temurin:24-jdk

WORKDIR /app

# Copiamos todo el proyecto
COPY . .

# Instalamos Maven (si no está incluido)
RUN apt-get update && apt-get install -y maven

# Compilamos el proyecto
RUN mvn clean package -DskipTests

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Ejecutamos el jar generado
CMD ["java", "-jar", "target/platform-0.0.1-SNAPSHOT.jar"]

