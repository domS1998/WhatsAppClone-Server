#FROM ubuntu:latest
#LABEL authors="ubuntu"
#ENTRYPOINT ["top", "-b"]

FROM openjdk:17
LABEL authors="ubuntu"
WORKDIR /app
COPY target/webservice-1.0-jar-with-dependencies.jar .
EXPOSE 8080
#RUN chmod 777 target/messenger_app-1.0-SNAPSHOT-jar-with-dependencies.jar
CMD ["java", "-jar", "webservice-1.0-jar-with-dependencies.jar"]

# docker anschalten:
# - docker deamon starten (cmd line oder Docker Desktop), automatisch bei Ubuntu

# docker images
# - list all images: docker images
# - remove image:    docker image rm <id>
# - docker image aus dockerfile in working dir bauen : docker image build -t webservice-1.0 .

# docker container
# - image als container bauen und starten:  docker run -d -p 8080:8080 --name webservice-1.0 <id>
# - container aus image bauen, starten und ggf. alten mit gleichem image löschen:
#    '--> docker run --rm -d -p 8000-9000:8000-9000 --name webservice-1.0 <id>
# - container aus image bauen: docker container create webservice-1.0
# - container starten: docker run webservice-1.0
# - container starten mit port mapping: docker run -p 8080:8080 webservice-1.0
# - laufenden container stoppen: docker stop <id>
# - alle container auflisten: docker ps -a
# - laufende container auflisten: docker ps

# docker netzwerk einstellungen
# - container ip adresse (alle config Daten eines Containers) anzeigen: docker inspect <id>

# Verbindung testen:
#  curl localhost:8080