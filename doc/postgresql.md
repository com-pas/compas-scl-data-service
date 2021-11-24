<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# PostgreSQL Implementation

## BaseX Environment variables

Below environment variable(s) can be used to configure the connection to BaseX, if BaseX Server is used.

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

| Column            | Type                 | Required  | Description                                                        |
| ----------------- | -------------------- | ----------| ------------------------------------------------------------------ |
| id                | uuid                 | True      | Unique ID generated according to standards                         |
| major_version     | smallint             | True      | Versioning according to [Semantic Versioning](https://semver.org/) |
| minor_version     | smallint             | True      | Versioning according to [Semantic Versioning](https://semver.org/) |
| patch_version     | smallint             | True      | Versioning according to [Semantic Versioning](https://semver.org/) |
| type              | varchar(3)           | True      | The type of SCL stored                                             |
| name              | varchar(255)         | True      | The name of the SCL File                                           |
| scl_data          | text                 | True      | The SCL XML Content                                                |

## Development

### Application depends on a running PostgreSQL instance

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
> All data will be stored in this directory under "compas". This way data isn't lost after stopping the docker container.

### Building the application

You can run the following command to build the PostgreSQL version of the application.

```shell script
./mvnw clean verify
```

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw -DskipTests=true -Dquarkus.profile=dev-postgresql package io.quarkus:quarkus-maven-plugin::dev
```

### Creating a native executable

You can create a native executable using:

```shell script
./mvnw -P native package
```

This will run the native executable build in a container. In the native profile the property
"quarkus.native.container-build" is set to 'true'.

You can then execute your native executable with: `./app/target/postgresql-quarkus-app/app-local-SNAPSHOT-runner`

If you want to learn more about building native executables, please see https://quarkus.io/guides/maven-tooling.html
and https://quarkus.io/guides/writing-native-applications-tips.
