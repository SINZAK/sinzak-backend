FROM adoptopenjdk/openjdk11
COPY *.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]