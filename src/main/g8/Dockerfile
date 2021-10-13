FROM hseeberger/scala-sbt:11.0.11_$sbt_version$_$scala_version$ AS build
RUN apt-get install -y unzip
WORKDIR /usr/src
COPY . ./
RUN sbt server/universal:packageBin
RUN unzip /usr/src/server/target/universal/app.zip

FROM openjdk:11
COPY --from=build /usr/src/app /usr/app
WORKDIR /usr/app
EXPOSE 12345
CMD ["./bin/profiles-rpc-server"]

