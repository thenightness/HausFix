FROM jelastic/maven:3.9.9-openjdk-21.0.2-almalinux-9 AS build

COPY backend/src /usr/src/app/src
COPY backend/pom.xml /usr/src/app
# Maven-Dependencies werden gecached      Sagt wo die pom.xml liegt
# clean: räumt auf
# package: erstellt die .jar
RUN --mount=type=cache,target=/root/.m2 mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip


FROM alpine/java:21-jre
WORKDIR /usr/app
# Kopiert aus dem Build-Image die fertige .jar
COPY --from=build /usr/src/app/target/HausFixBackend-1.0-SNAPSHOT.jar HausFixBackend-1.0-SNAPSHOT.jar
COPY --from=build /usr/src/app/src/main/resources/. .
# Command welcher beim starten des Images ausgeführt wird
CMD ["java","-jar","/usr/app/HausFixBackend-1.0-SNAPSHOT.jar"]