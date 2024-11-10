FROM eclipse-temurin:21.0.4_7-jdk-noble
WORKDIR /app
COPY build/libs/*T.jar rna-analyzer.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/rna-analyzer.jar"]