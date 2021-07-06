<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

[![Gradle Build Github Action Status](<https://img.shields.io/github/workflow/status/com-pas/compas-scl-data-service/Gradle%20Build?logo=GitHub>)](https://github.com/com-pas/compas-scl-data-service/actions?query=workflow%3A%22Gradle+Build%22)
[![REUSE status](https://api.reuse.software/badge/github.com/com-pas/compas-scl-data-service)](https://api.reuse.software/info/github.com/com-pas/compas-scl-data-service)

# compas-scl-data-service

Service to store and retrieve the SCL XML to a database.

In the standard configuration this component is using Quarkus to run and create a native image from it. The different
parts of this repository can also be used separately in your own component to manage SCL XML Files in a database. The
Service Layer can be used as Java component to manage them.

For more information about the architecture take a look at

## Application depends on a running BaseX instance

Check [basexhttp on DockerHub](https://hub.docker.com/r/basex/basexhttp/) for a running BaseX docker container.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```
./gradlew quarkusDev
```

## Packaging and running the application

The application can be packaged using `./gradlew quarkusBuild`. It produces
the `compas-scl-data-service-1.0-SNAPSHOT-runner.jar` file in the `build` directory. Be aware that it’s not an _
über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/compas-cim-mapping-1.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:

```
./gradlew quarkusBuild --uber-jar
```

## Creating a native executable

You can create a native executable using: `./gradlew build -Dquarkus.package.type=native`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container
using: `./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./build/compas-scl-data-service-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please
consult https://quarkus.io/guides/gradle-tooling#building-a-native-executable.