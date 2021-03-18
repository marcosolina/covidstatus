FROM maven:3.6.3-jdk-11 AS MAVEN_IMAGE
WORKDIR /tmp
RUN git clone https://github.com/marcosolina/javautils.git
RUN git clone https://github.com/marcosolina/WebJar.git
RUN git clone https://github.com/marcosolina/covidstatus.git

RUN mvn clean install -f WebJar
RUN mvn clean install -f javautils/Utils
RUN mvn clean install -f covidstatus

FROM openjdk:11
COPY ./application.yml application.yml
COPY ./start.sh start.sh
RUN chmod +x start.sh
EXPOSE 8080:8080
COPY --from=MAVEN_IMAGE /tmp/covidstatus/target/covidstatus*.jar covidstatus.jar
ENTRYPOINT ["/start.sh"]

