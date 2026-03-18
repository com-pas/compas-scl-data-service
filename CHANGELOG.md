# Changelog

## [0.17.4](https://github.com/com-pas/compas-scl-data-service/compare/v0.17.3...v0.17.4) (2026-03-18)


### Bug Fixes

* Add role check to WebSocket Create/Update endpoints ([33e6225](https://github.com/com-pas/compas-scl-data-service/commit/33e622580682fe724bc8e015d20d398abda8cc3e))
* Bump microprofile-openapi-api to 4.1.1 for Quarkus 3.31 compatibility ([b4713ed](https://github.com/com-pas/compas-scl-data-service/commit/b4713ed307b1db2505ea668e3eed3037260f21db))
* checkout base repository instead of fork in SonarCloud workflow ([a18a075](https://github.com/com-pas/compas-scl-data-service/commit/a18a07536a02a763a38983b148a3fd442e25b666))
* Correct XPath references and namespace handling in REST endpoint tests ([a0c8372](https://github.com/com-pas/compas-scl-data-service/commit/a0c83723b0e0a1b864611f110f9a2c33a2c576c8))
* enforce LF for SCD files via .gitattributes and revert dependabot actor check ([e30ae9d](https://github.com/com-pas/compas-scl-data-service/commit/e30ae9d52e2b87711293281575c881fb4b3250e4))
* explicit config setting for namespace awareness ([cad7539](https://github.com/com-pas/compas-scl-data-service/commit/cad7539489a9f63ac508b5f6de2b16270e127598))
* Normalize line endings in REST test SCL fixtures ([3ccb826](https://github.com/com-pas/compas-scl-data-service/commit/3ccb82657548eb57f0a1c14d5dc063c93aa7816e))
* now using shas instead of versions in workflows. Recommended by SonarCube ([d0f029a](https://github.com/com-pas/compas-scl-data-service/commit/d0f029add28774aa22d7e04bd5c5fab0e046423b))
* prevent forgeable actor checks and command injection in CI workflows ([f97e083](https://github.com/com-pas/compas-scl-data-service/commit/f97e08368c803dc306ba5b1181f4d46914c08119))
* Renamed resteasy-reactive artifacts for Quarkus 3.31 compatibility ([a501b6f](https://github.com/com-pas/compas-scl-data-service/commit/a501b6fa314cc499e5cf2152628ad29a49d6bb8f))
* revert SonarCloud checkout to use fork repo and head branch Rest… ([df0335e](https://github.com/com-pas/compas-scl-data-service/commit/df0335e34f2c14b0ecefd4a5aeccceb0a910477c))
* revert SonarCloud checkout to use fork repo and head branch Restores head_repository.full_name and head_branch in checkout step,reverting a18a075 which broke fork PR analysis. ([1b2571c](https://github.com/com-pas/compas-scl-data-service/commit/1b2571cf153d8d893dd0be11e1568c758996a642))
* reverted changes for unneccessary dependabot check ([f2c40d4](https://github.com/com-pas/compas-scl-data-service/commit/f2c40d44ca9c8bd5c673360d4fa318223d9740b0))
* set docker api version release please action ([1a23775](https://github.com/com-pas/compas-scl-data-service/commit/1a23775800e8b68b53a756d0945f5f3b8939b8c0))
* set docker api version release please action ([db3bfc9](https://github.com/com-pas/compas-scl-data-service/commit/db3bfc9390cedf4a05e934ffc604fbd581dd577d))
* Update InjectMock import for Quarkus 3.31 compatibility ([0253f6b](https://github.com/com-pas/compas-scl-data-service/commit/0253f6b410300834bdf7547310fe91576b228249))
* Update WebSocket reader tests for Quarkus 3.31 behavior ([10ee979](https://github.com/com-pas/compas-scl-data-service/commit/10ee979c19e03d0058b89883960697448ad6a1df))
* upgrade Quarkus to 3.31.4 with compatibility and security fixes ([8283262](https://github.com/com-pas/compas-scl-data-service/commit/82832623b7d6690331d10571d9b5885a4543f281))

## [0.17.3](https://github.com/com-pas/compas-scl-data-service/compare/v0.17.2...v0.17.3) (2026-02-17)


### Bug Fixes

* add Docker API version in sonarcloud-build ([236279d](https://github.com/com-pas/compas-scl-data-service/commit/236279d68db02440c2c05b632235aa7fbff7bcf5))
* set docker api version release please action ([1a23775](https://github.com/com-pas/compas-scl-data-service/commit/1a23775800e8b68b53a756d0945f5f3b8939b8c0))
* set docker api version release please action ([db3bfc9](https://github.com/com-pas/compas-scl-data-service/commit/db3bfc9390cedf4a05e934ffc604fbd581dd577d))
* set Docker API version to 1.44 for compatibility with Testcontainer ([653981b](https://github.com/com-pas/compas-scl-data-service/commit/653981b574937a594e9de11fcb171e7732557150))
* set Docker API version to 1.44 for compatibility with Testcontainers ([bffb323](https://github.com/com-pas/compas-scl-data-service/commit/bffb3233436f6b33c0743f2a25209a4f54108627))
* trigger release ([a5bb594](https://github.com/com-pas/compas-scl-data-service/commit/a5bb594b1953728d9fc6e23b6ef1eb856bdee75f))

## [0.17.2](https://github.com/com-pas/compas-scl-data-service/compare/v0.17.1...v0.17.2) (2026-02-02)


### Bug Fixes

* trigger release ([bc223d8](https://github.com/com-pas/compas-scl-data-service/commit/bc223d8369d2515104140082072327b3280635ab))

## [0.17.1](https://github.com/com-pas/compas-scl-data-service/compare/v0.17.0...v0.17.1) (2026-01-21)


### Bug Fixes

* move metrics endpoint to 9090 ([c99675a](https://github.com/com-pas/compas-scl-data-service/commit/c99675a4d1a67d9e1b8a76bbfa5c3016b7785e03))
* move metrics to 9090 ([eb96b4d](https://github.com/com-pas/compas-scl-data-service/commit/eb96b4d4c59653249e63962653debcb728d8cb0b))

## [0.17.0](https://github.com/com-pas/compas-scl-data-service/compare/v0.16.3...v0.17.0) (2026-01-20)


### Features

* add micrometer prometheus registry and configure metrics endpoint ([5b60e50](https://github.com/com-pas/compas-scl-data-service/commit/5b60e50f587d6970c5b5542333b8e372e6b2cdd7))
* add Micrometer Prometheus registry and configure metrics endpoint permissions ([e96e452](https://github.com/com-pas/compas-scl-data-service/commit/e96e45263869af43a4fd9a71bb2264a2595f24f3))
* update authorization for metrics endpoint and adjust allowed paths ([7a38bb0](https://github.com/com-pas/compas-scl-data-service/commit/7a38bb0af6f42265e5705e41c315e09b8520c18b))

## [0.16.3](https://github.com/com-pas/compas-scl-data-service/compare/v0.16.2...v0.16.3) (2026-01-06)


### Bug Fixes

* trigger release-please ([4dbc9ab](https://github.com/com-pas/compas-scl-data-service/commit/4dbc9abddc7f2388c6f2b5553ce5dc1773556c0d))

## [0.16.2](https://github.com/com-pas/compas-scl-data-service/compare/v0.16.1...v0.16.2) (2025-11-27)


### Bug Fixes

* trigger release ([24e84e7](https://github.com/com-pas/compas-scl-data-service/commit/24e84e73bb9b5032c6af041be1749eb22c5d43ce))

## [0.16.1](https://github.com/com-pas/compas-scl-data-service/compare/v0.16.0...v0.16.1) (2025-09-01)


### Documentation

* Update readme ([4b84f36](https://github.com/com-pas/compas-scl-data-service/commit/4b84f36e37f9a1d70a89e2930ee7f21dd94a3c19))

## [0.16.0](https://github.com/com-pas/compas-scl-data-service/compare/v0.15.6...v0.16.0) (2025-07-30)


### Features

* [[#464](https://github.com/com-pas/compas-scl-data-service/issues/464)] added soft-delete to database (flyway file version collision) ([2c8d481](https://github.com/com-pas/compas-scl-data-service/commit/2c8d481c199700c7b67701a2b33365d0de2b6a73))
* Add default lnodetype library migration ([03f3dcf](https://github.com/com-pas/compas-scl-data-service/commit/03f3dcff1543150390fd0df7090f9e74bc6552da))
* Do nothing if scl file already exists ([0244162](https://github.com/com-pas/compas-scl-data-service/commit/0244162eccdadeba8da473c360e6184665ab1723))
* upgraded compas-core version to 0.22.0 ([4e0dbd6](https://github.com/com-pas/compas-scl-data-service/commit/4e0dbd61d5b378b4d2a124b0f55b220bc084e5f4))


### Documentation

* add required docs and codeowners config ([43eea51](https://github.com/com-pas/compas-scl-data-service/commit/43eea510827530764d9b81c5db429d31c07d7cf3))

## [0.15.6](https://github.com/com-pas/compas-scl-data-service/compare/v0.15.5...v0.15.6) (2025-02-10)


### Bug Fixes

* Remove custom set version steps ([60cc386](https://github.com/com-pas/compas-scl-data-service/commit/60cc386be4a0fe47dfc00e35c210338fe941c74f))

## [0.15.5](https://github.com/com-pas/compas-scl-data-service/compare/v0.15.4...v0.15.5) (2025-02-06)


### Bug Fixes

* Add write permissions for packages ([c49c4bf](https://github.com/com-pas/compas-scl-data-service/commit/c49c4bf2245dae274409f11f4ac30165e0b6c186))


## [0.15.4](https://github.com/com-pas/compas-scl-data-service/compare/v0.15.3...v0.15.4) (2025-02-06)


### Bug Fixes

* Trigger release please ([5e2c2bf](https://github.com/com-pas/compas-scl-data-service/commit/5e2c2bf8001b73bb1b5ed87d1557528d378e8712))

## [0.15.3](https://github.com/com-pas/compas-scl-data-service/compare/v0.15.2...v0.15.3) (2025-02-04)


### Documentation

* add changelog ([76f44e5](https://github.com/com-pas/compas-scl-data-service/commit/76f44e56466822fe1469448052080fc098eabbe5))

## [0.15.2](https://github.com/com-pas/compas-scl-data-service/compare/compas-scl-data-service-v0.15.1...compas-scl-data-service-v0.15.2) (2024-03-26)


### Documentation

* add changelog ([76f44e5](https://github.com/com-pas/compas-scl-data-service/commit/76f44e56466822fe1469448052080fc098eabbe5))

<!--
SPDX-FileCopyrightText: 2023 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->
For older changelogs, please check the release tag on GitHub.
