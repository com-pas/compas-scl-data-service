<!--
SPDX-FileCopyrightText: 2026 BearingPoint GmbH

SPDX-License-Identifier: Apache-2.0
-->

API Module
==========

This module contains the **API contract** and **generated API code** for the custom plugins resources.

The OpenAPI specification is defined in `plugins-custom-resources.yaml`.
If changes are made to the specification file, the API code must be re-generated using:

```bash
cd ./api
mvn openapi-generator:generate@generate-api
```
