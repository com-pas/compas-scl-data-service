<!--
SPDX-FileCopyrightText: 2025 BearingPoint GmbH

SPDX-License-Identifier: Apache-2.0
-->

# Development

Run application with local postgresql database in development mode:
```bash
  cd src/test/resources/docker-compose.yml
  docker-compose up keycloak

  mvn -DskipTests=true -Dquarkus.profile=local package io.quarkus:quarkus-maven-plugin::dev
```

In case of issues enable full debug logging by uncommenting the enable `ALL` logs in the `src/main/resources/application.properties` file:
```properties
    quarkus.log.level = ALL
    quarkus.log.category."org.lfenergy.compas.scl.data".level = ALL
```

## Verify setup

To verify that the setup is correct, you can use the following curl command to list all ICDs in the database:
```
    curl -X GET "http://localhost:8080/scl-data-service/icd/list" -H "Authorization: Bearer <ACCESS_TOKEN>" -H "accept: application
```

The `<ACCESS_TOKEN>` should be replaced with a valid JWT token obtained from Keycloak with following command:
```bash
    curl -X POST "http://localhost:8089/auth/realms/compas/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=scl-data-service&username=scl-data-editor&password=editor&grant_type=password"
```
