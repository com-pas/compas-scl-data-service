<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# PostgreSQL Implementation

## PostgreSQL Environment variables

Below environment variable(s) can be used to configure the connection to PostgreSQL, if PostgreSQL Server is used.

| Environment variable             | Java Property               | Description                                   | Example          |
| -------------------------------- | --------------------------- | --------------------------------------------- | ---------------- |
| POSTGRESQL_HOST                  | -                           | Name of the Host where PostgreSQL runs.       | localhost        |
| POSTGRESQL_PORT                  | -                           | Port on the Host on which PostgreSQL runs.    | 5432             |
| POSTGRESQL_DB                    | -                           | The database used in PostgreSQL               | compas           |
| POSTGRESQL_USERNAME              | quarkus.datasource.username | Username under which the application logs in. | postgres         |
| POSTGRESQL_PASSWORD              | quarkus.datasource.password | Password of the username used above.          | postgres         |

The variables POSTGRESQL_HOST, POSTGRESQL_PORT and POSTGRESQL_DB are used to created the JDBC URL in the property file.
The value is build like 'jdbc:postgresql://${POSTGRESQL_HOST}:${POSTGRESQL_PORT}/${POSTGRESQL_DB}'. The variables have
respectively the following defaults, 'localhost', '5432' and 'compas'.

## Database Model

For the SCL Data Service the following table is automatically created using FlyWay.

Table: scl_file

| Column        | Type         | Required | Description                                                        |
|---------------|--------------|----------|--------------------------------------------------------------------|
| id            | uuid         | True     | Unique ID generated according to standards                         |
| major_version | smallint     | True     | Versioning according to [Semantic Versioning](https://semver.org/) |
| minor_version | smallint     | True     | Versioning according to [Semantic Versioning](https://semver.org/) |
| patch_version | smallint     | True     | Versioning according to [Semantic Versioning](https://semver.org/) |
| type          | varchar(3)   | True     | The type of SCL stored                                             |
| name          | varchar(255) | True     | The name of the SCL File                                           |
| scl_data      | text         | True     | The SCL XML Content                                                |
| is_deleted    | boolean      | False    | Flag indicating whether the record is marked as deleted            |

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
./mvnw -DskipTests=true -Dquarkus.profile=dev-postgresql package io.quarkus:quarkus-maven-plugin::dev
```

#### Application depends on a running PostgreSQL instance

Check [PostgreSQL on DockerHub](https://hub.docker.com/_/postgres?tab=description) for a running PostgreSQL docker
container. Use the following command to start the docker container.

```shell
docker run --rm --name compas_postgresql \
   -e POSTGRES_PASSWORD=postgres \
   -e POSTGRES_DB=compas \
   -e PGDATA=/var/lib/postgresql/data/compas \
   -v <LOCAL-DATA-DIR>:/var/lib/postgresql/data \
   -p 5432:5432 \
   -d postgres:latest
```

> **Note:** Replace <LOCAL-DATA-DIR> with a directory on your local machine, for instance "~/postgres".
> All data will be stored in this directory under "compas". This way data isn't lost after stopping the docker
> container.

#### Application depends on a running KeyCloak instance

Beside a PostgreSQL Database there is also a KeyCloak instance need to be running on port 8089 by default.
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

You can then execute your native executable with: `./app/target/postgresql-quarkus-app/app-local-SNAPSHOT-runner`

### Creating a Docker image with JVM executable

There is also a profile to create a Docker Image which runs the application using a JVM. You can create a Docker Image
with JVM executable using:

```shell script
./mvnw package -Pjvm-image
```

The JVM Image can also (temporary) be created by the release action if there are problems creating or running the
native executable.
