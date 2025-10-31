# Imagen base con Java 21
FROM eclipse-temurin:21-jdk

# Crear directorio para la app
WORKDIR /app

# Copiar los archivos del proyecto
COPY . .

# Compilar el proyecto con Maven (sin ejecutar tests)
RUN ./mvnw clean package -DskipTests

# Exponer el puerto usado por Spring Boot
EXPOSE 8081

# Comando para ejecutar la app (ajusta el .jar si tiene otro nombre)
CMD ["java", "-jar", "target/NutriAI-1.0.0.jar"]


