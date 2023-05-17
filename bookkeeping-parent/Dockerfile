FROM openjdk

COPY ./target/bookkeeping*.jar /usr/app/bookkeeping.jar

WORKDIR /usr/app

EXPOSE 8080

ENTRYPOINT ["java","-jar","bookkeeping.jar"]
