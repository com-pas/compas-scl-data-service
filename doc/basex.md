<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# BaseX Implementation

## BaseX Environment variables

Below environment variable(s) can be used to configure the connection to BaseX, if BaseX Server is used.

| Environment variable             | Java Property             | Description                                   | Example          |
| -------------------------------- | ------------------------- | --------------------------------------------- | ---------------- |
| BASEX_HOST                       | basex.host                | Name of the Host where BaseX runs.            | localhost        |
| BASEX_PORT                       | basex.port                | Port on the Host on which BaseX runs.         | 1984             |
| BASEX_USERNAME                   | basex.username            | Username under which the application logs in. | admin            |
| BASEX_PASSWORD                   | basex.password            | Password of the username used above.          | admin            |

## Development

### Building the application

You can use Maven to build the application and see if all tests are working using:

```shell script
./mvnw clean verify
```

This should normally be enough to also run the application, but there were cases that we need to build using:

```shell script
./mvnw clean install
```

This to make the local modules available for the app module to run the application.

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw -DskipTests=true -Dquarkus.profile=dev-basex package io.quarkus:quarkus-maven-plugin::dev
```

#### Application depends on a running BaseX instance

Check [basexhttp on DockerHub](https://hub.docker.com/r/basex/basexhttp) for a running BaseX docker container. This is
needed when running the SCL Data Service locally. Not needed to run the tests.

```shell
mkdir -p <BASEX-DIR>/data
sudo chown -R 1984:1984 <BASEX-DIR>/data

docker run --rm --name compas_basex \
   -p 1984:1984 \
   -v <BASEX-DIR>/data:/srv/basex/data \
   -d basex/basexhttp:latest
```

> **Note:** Replace <BASEX-DIR> with a directory on your local machine, for instance "~/basex".
> All data will be stored in this directory under "data". This way data isn't lost after stopping the docker container.

#### Application depends on a running KeyCloak instance

Beside a BaseX Database there is also a KeyCloak instance need to be running on port 8089 by default.
See [README.md](../README.md#security) for default values, if custom keycloak is used.

There is a preconfigured keycloak instance available in
the [CoMPAS Deployment Repository](https://github.com/com-pas/compas-deployment). This repository can be cloned and
when going to this directory the following command can be executed to create a local Docker Image with configuration.

```shell
cd <CoMPAS Deployment Repository Directory>/compas/keycloak
docker build -t compas_keycloak . 
```

There is now a Docker Image `compas_keycloak` created that can be started using the following command

```shell
docker run --rm --name compas_keycloak \
   -p 8089:8080 
   -d compas_keycloak:latest
```

### Creating a Docker image with native executable

The releases created in the repository will create a docker image with a native executable. If you're running a Linux
system it's possible to create and run the executable locally. You can create a Docker image with native executable
using:

```shell script
./mvnw package -Pnative-image
```

You can then execute your native executable with: `./app/target/basex-quarkus-app/app-local-SNAPSHOT-runner`

### Creating a Docker image with JVM executable

There is also a profile to create a Docker Image which runs the application using a JVM. You can create a Docker Image
with JVM executable using:

```shell script
./mvnw package -Pjvm-image
```

The JVM Image can also (temporary) be created by the release action if there are problems creating or running the
native executable.
