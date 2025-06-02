#FROM ghcr.io/navikt/poao-baseimages/java:17
#COPY /target/veilarbmalverk.jar app.jar
#
#
FROM busybox:1.36.1-uclibc as busybox

FROM gcr.io/distroless/java17

COPY --from=busybox /bin/sh /bin/sh
COPY --from=busybox /bin/printenv /bin/printenv
COPY --from=busybox /bin/mkdir /bin/mkdir
COPY --from=busybox /bin/chown /bin/chown

ENV TZ="Europe/Oslo"
WORKDIR /app
COPY /target/veilarbmalverk.jar app.jar
RUN /bin/mkdir /secure-logs
RUN chown nonroot /secure-logs
EXPOSE 8080
USER nonroot
CMD ["app.jar"]