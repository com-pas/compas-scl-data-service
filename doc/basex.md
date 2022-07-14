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

### Application depends on a running BaseX instance

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

### Application depends on a running KeyCloak instance

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

### Building the application

You can run the following command to build the BaseX version of the application.

```shell script
./mvnw clean verify
```

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw -DskipTests=true -Dquarkus.profile=dev-basex package io.quarkus:quarkus-maven-plugin::dev
```

### Creating a native executable

You can create a native executable using:

```shell script
./mvnw -P native package
```

This will run the native executable build in a container. In the native profile the property
"quarkus.native.container-build" is set to 'true'.

You can then execute your native executable with: `./app/target/basex-quarkus-app/app-local-SNAPSHOT-runner`

If you want to learn more about building native executables, please see https://quarkus.io/guides/maven-tooling.html
and https://quarkus.io/guides/writing-native-applications-tips.
