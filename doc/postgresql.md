<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# PostgreSQL Implementation

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
docker run --rm --name compas_postgres \
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

You can run the following command to build the BaseX version of the application.

```shell script
./mvnw -P postgres clean verify
```

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw -P postgres -DskipTests=true -Dquarkus.profile=dev,prod-postgres package io.quarkus:quarkus-maven-plugin::dev
```

