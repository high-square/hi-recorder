FROM openjdk:11-jdk
LABEL title="hi-recorder"
ARG JAR_FILE=build/libs/hi-recoder-0.0.1-SNAPSHOT.jar
ARG JASYPT
ADD ${JAR_FILE} docker-springboot.jar
ENTRYPOINT ["java","-jar","/docker-springboot.jar", "jasypt.encryptor.password=${JASYPT}"]