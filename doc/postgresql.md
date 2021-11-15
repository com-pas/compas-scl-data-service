<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# PostgreSQL Implementation

## Database Model

For the SCL Data Service the following table is automatically created using FlyWay.

Table: scl_file

| Column            | Type                 | Description                                                        |
| ----------------- | -------------------- | ------------------------------------------------------------------ |
| ID                | uuid                 | Unique ID generated according to standards                         |
| VERSION           | varchar(20)          | Versioning according to [Semantic Versioning](https://semver.org/) |
| TYPE              | varchar(3)           | The type of SCL stored                                             |
| NAME              | varchar(255)         | The name of the SCL File                                           |
| SCL_DATA          | text                 | The SCL XML Content                                                |
