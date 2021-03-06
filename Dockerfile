FROM adoptopenjdk/openjdk11:latest
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
COPY target/frontend /app

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-cp","app:app/lib/*","rso.frontend.FrontendApplication"]