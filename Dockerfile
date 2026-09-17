FROM openjdk:21-ea
LABEL authors="samue"
COPY target/medical-clinic-0.0.1-SNAPSHOT.jar medical-clinic-1.jar
ENTRYPOINT ["java","-jar","medical-clinic-1.jar"]