<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

[![Maven Build Github Action Status](<https://img.shields.io/github/workflow/status/com-pas/compas-scl-data-service/Maven%20Build?logo=GitHub>)](https://github.com/com-pas/compas-scl-data-service/actions?query=workflow%3A%22Maven+Build%22)
[![REUSE status](https://api.reuse.software/badge/github.com/com-pas/compas-scl-data-service)](https://api.reuse.software/info/github.com/com-pas/compas-scl-data-service)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com-pas_compas-scl-data-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=com-pas_compas-scl-data-service)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/5925/badge)](https://bestpractices.coreinfrastructure.org/projects/5925)
[![Slack](https://raw.githubusercontent.com/com-pas/compas-architecture/master/public/LFEnergy-slack.svg)](http://lfenergy.slack.com/)

# CoMPAS SCL Data Service

Service to store and retrieve the SCL XML to a database.

In the standard configuration this component is using Quarkus to run and create a native image from it. The different
parts of this repository can also be used separately in your own component to manage SCL XML Files in a database. The
Service Layer can be used as Java component to manage them. For more information about the architecture go
to [documentation](doc/compas-scl-data-service.md).

There is currently one database implementations available.

- For more development information about the PostgreSQL Implementation go to [PostgreSQL](doc/postgresql.md).

> **Note:** When switching between implementation it's a good practise to first execute a maven clean to remove
> old dependencies from the target directory in the app module.

## Development

See [development](doc/development.md) documentation.

## Common Environment variables

Below environment variable(s) can be used to configure which claims and information are used to fill the UserInfo
response.

| Environment variable             | Java Property                   | Description                                                 | Example          |
| -------------------------------- | ------------------------------- | ----------------------------------------------------------- | ---------------- |
| USERINFO_NAME_CLAIMNAME          | compas.userinfo.name.claimname  | The Name of the user logged in.                             | name             |
| USERINFO_WHO_CLAIMNAME           | compas.userinfo.who.claimname   | The Name of the user used in the Who History.               | name             |
| USERINFO_SESSION_WARNING         | compas.userinfo.session.warning | Number of minutes a Session Warning can be displayed.       | 20               |
| USERINFO_SESSION_EXPIRES         | compas.userinfo.session.expires | Number of minutes a Session Expires to display in Frontend. | 30               |

## Security

To use most of the endpoints the users needs to be authenticated using JWT in the authorization header. There are 4
environment variables that can be set in the container to configure the validation/processing of the JWT.

| Environment variable             | Java Property                    | Description                                        | Example                                                                |
| -------------------------------- | -------------------------------- | -------------------------------------------------- | ---------------------------------------------------------------------- |
| JWT_VERIFY_KEY                   | smallrye.jwt.verify.key.location | Location of certificates to verify the JWT.        | http://localhost:8089/auth/realms/compas/protocol/openid-connect/certs |
| JWT_VERIFY_ISSUER                | mp.jwt.verify.issuer             | The issuer of the JWT.                             | http://localhost:8089/auth/realms/compas                               |
| JWT_VERIFY_CLIENT_ID             | mp.jwt.verify.audiences          | The Client ID that should be in the "aud" claim.   | scl-data-service                                                       |
| JWT_GROUPS_PATH                  | smallrye.jwt.path.groups         | The JSON Path where to find the roles of the user. | resource_access/scl-data-service/roles                                 |

The application uses the following list of roles. The fine-grained roles are built up of the types of SCL Files this
service supports and the rights READ/CREATE/UPDATE/DElETE. This way the mapping of the roles to groups/users can be
configured as needed.

- ICD_CREATE
- ICD_DELETE
- ICD_READ
- ICD_UPDATE
- SCD_CREATE
- SCD_DELETE
- SCD_READ
- SCD_UPDATE
- SSD_CREATE
- SSD_DELETE
- SSD_READ
- SSD_UPDATE
- ISD_CREATE
- ISD_DELETE
- ISD_READ
- ISD_UPDATE
- CID_CREATE
- CID_DELETE
- CID_READ
- CID_UPDATE
- IID_CREATE
- IID_DELETE
- IID_READ
- IID_UPDATE
- SED_CREATE
- SED_DELETE
- SED_READ
- SED_UPDATE
- STD_CREATE
- STD_DELETE
- STD_READ
- STD_UPDATE
