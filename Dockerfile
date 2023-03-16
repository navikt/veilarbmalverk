FROM ghcr.io/navikt/poao-baseimages/java:17
COPY /target/veilarbmalverk.jar app.jar
