FROM eclipse-temurin:11

WORKDIR /app

COPY dependencies/ ./
COPY spring-boot-loader ./
COPY spring-dependencies/ ./
COPY snapshot-dependencies/ ./
COPY snapshot-spring-dependencies/ ./
COPY application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]