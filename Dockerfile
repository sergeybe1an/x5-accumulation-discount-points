FROM openjdk:11
ADD build/libs/x5-accumulation-discount-points-0.0.1-SNAPSHOT.jar xfive.jar
ENTRYPOINT ["java","-jar","xfive.jar"]