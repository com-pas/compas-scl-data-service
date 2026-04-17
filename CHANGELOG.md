# Changelog

## [0.18.0](https://github.com/com-pas/compas-scl-data-service/compare/v0.17.4...v0.18.0) (2026-04-17)


### Features

* Add ([3c7919c](https://github.com/com-pas/compas-scl-data-service/commit/3c7919c841d58c177d584c63d015a8b5b75c706a))
* Added endpoint tests for CompasPluginsResource ([1d7529f](https://github.com/com-pas/compas-scl-data-service/commit/1d7529f5fc020696bbcb88f2f2c7440db39fcf4f))
* Added exception classes for plugins custom resources ([328c490](https://github.com/com-pas/compas-scl-data-service/commit/328c490f53dc767c9c786d8e879da66aa331cdf7))
* Added Hibernate ORM config and auth permissions for plugins endpoints ([0635710](https://github.com/com-pas/compas-scl-data-service/commit/06357106b319825ee5a5f43826ee89c8f385c14c))
* Added PluginsCustomResource entity ([f630819](https://github.com/com-pas/compas-scl-data-service/commit/f630819509e8e34e71899726758a7fd722591a3f))
* Added rest endpoints for 594_plugins_custom_resources ([5a3b3aa](https://github.com/com-pas/compas-scl-data-service/commit/5a3b3aaa97e8aa34d75a04d6ec27e3af350c15bd))
* Implemented plugins custom resources REST endpoints ([88a0a61](https://github.com/com-pas/compas-scl-data-service/commit/88a0a618971473b03902ddebe49d5782397de96c))
* Registered generated  DTOs for native image reflection ([3b6fe6b](https://github.com/com-pas/compas-scl-data-service/commit/3b6fe6b2914bc9bab1c1df8239bc2d417736f0cc))


### Bug Fixes

* added api module with the sources generated from OpenAPI ([2ae84ff](https://github.com/com-pas/compas-scl-data-service/commit/2ae84ffd854b67c0a7f660cc60310f4c5fdefed6))
* Added hibernate processor config ([6a589ab](https://github.com/com-pas/compas-scl-data-service/commit/6a589abae18015245a2c55b91edb7a2d61ad85d4))
* added index dependency for repository-postgresql and hibernate orm config in application.properties ([e282956](https://github.com/com-pas/compas-scl-data-service/commit/e2829568fbee20925b3d8537877fc10b75b82a22))
* Added license to CompasPluginsResource ([6fd9a80](https://github.com/com-pas/compas-scl-data-service/commit/6fd9a80c411575738773cbe35409647c6d07276a))
* added licenses for the generated files ([75e7a13](https://github.com/com-pas/compas-scl-data-service/commit/75e7a131280b9569f3575b011a40250aa4ab9f7b))
* added licenses for the new readme files ([c44fef6](https://github.com/com-pas/compas-scl-data-service/commit/c44fef61593e40b26152b261f1c68b1baa0eae60))
* added list for content types ([8ceb204](https://github.com/com-pas/compas-scl-data-service/commit/8ceb20410000890d51ecbb2c3395feb321b7d46b))
* added missing license comments ([5fb2490](https://github.com/com-pas/compas-scl-data-service/commit/5fb249069818a541c65fe4d183d72ee34594911d))
* added missing license to file repository-postgresql/src/test/resources/application.properties ([7d6b9f7](https://github.com/com-pas/compas-scl-data-service/commit/7d6b9f724e5974544f43f6ed3b6404d6444315d7))
* Added missing licensing file ([ab8807a](https://github.com/com-pas/compas-scl-data-service/commit/ab8807aa56feb45c0e1a7c50226b8c8c0f96897b))
* added new step in build-project.yml that installs parent POM ([0192ac3](https://github.com/com-pas/compas-scl-data-service/commit/0192ac3ba9f7c22581c77e7beecfc4d955d8cb2f))
* added quarkus reactive dependencies to native-image profile ([2e76d4c](https://github.com/com-pas/compas-scl-data-service/commit/2e76d4cbf7a4081de376bc03b5aa1925e219f4ce))
* Added quarkus-jdbc-postgresql dependency ([5491705](https://github.com/com-pas/compas-scl-data-service/commit/5491705747848f67eaea08041b5d580044bcdac1))
* added readme for quarkus hibernate with panache next ([98d983a](https://github.com/com-pas/compas-scl-data-service/commit/98d983a47bb890bb901570825e5f6f42bead6602))
* added relative path for parent ([3f747d7](https://github.com/com-pas/compas-scl-data-service/commit/3f747d7f66de5a566d511ac16aa31153668ade38))
* added schema definition for getAllData definiton in the openApi. ToDo: same has to be done for UploadData201Response ([24bb8da](https://github.com/com-pas/compas-scl-data-service/commit/24bb8daa0e6f5fcd6f9a42e9ebe5dc25420ad9c5))
* extracted new UploadRequest(...) from assertThrows each lambda has therefore only one invocation ([f19c4dc](https://github.com/com-pas/compas-scl-data-service/commit/f19c4dca0610e26e47b1257e97a3fb0d84498195))
* Fixed license ([e6ab13f](https://github.com/com-pas/compas-scl-data-service/commit/e6ab13f045b090822496a72b32281e14ad93206b))
* map common data entry fields in a dedicated method ([490a76c](https://github.com/com-pas/compas-scl-data-service/commit/490a76ccbc3cdbb9a8a6c42807a66a1479a15184))
* Modified all PluginsCustomResource fields to be public ([0ffb4fd](https://github.com/com-pas/compas-scl-data-service/commit/0ffb4fd08edf25437750a7eb4cc5edca05532020))
* moved panache-next dependency to repository module ([849b1fa](https://github.com/com-pas/compas-scl-data-service/commit/849b1fa528ff56b0d8a4e3d0f7050085b1cdcde6))
* moved PluginsCustomResource entity to repository module ([02235d4](https://github.com/com-pas/compas-scl-data-service/commit/02235d4f031e2094fdc2c642617eb85c52cc87a4))
* prevent forgeable actor checks and command injection in CI workflows ([c9a006e](https://github.com/com-pas/compas-scl-data-service/commit/c9a006e31afc0a67795ca5545b24a1aac2cc92dd))
* remove unused import ([4cae00c](https://github.com/com-pas/compas-scl-data-service/commit/4cae00c21f69aa7a88c787aa9cf2c87e575b527e))
* removed code smell on method that has more than 7 attributes. Recommended by SonarCube ([fc13b0c](https://github.com/com-pas/compas-scl-data-service/commit/fc13b0c44a1e87e90939cf49484e110fe0a99c62))
* removed new step in build-project.yml that installs parent POM ([e0e4ccd](https://github.com/com-pas/compas-scl-data-service/commit/e0e4ccdd7094c3a3f2557b6bbaac8273e95b99cc))
* Removed wrong mock in CompasPluginsResourceGetDataTest ([0d4d112](https://github.com/com-pas/compas-scl-data-service/commit/0d4d112b7f2e5f64db40f1616b391ee4396e90e3))
* rename custom plugins paths application.properties ([363dcee](https://github.com/com-pas/compas-scl-data-service/commit/363dceebc1c0ffd4556b787a90a0f0f4cd8bf092))
* rename paths for plugins custom resources ([02af6ab](https://github.com/com-pas/compas-scl-data-service/commit/02af6ab2f9d09d1eedb19c0332c85aa20ac2d683))
* renamed UpdateRequest to UploadCustomPluginsResourceData ([5c17c77](https://github.com/com-pas/compas-scl-data-service/commit/5c17c77fcbd2556d197ee52b01728767587f1446))
* rewrite new role names in uppercase for consistency ([e66b22a](https://github.com/com-pas/compas-scl-data-service/commit/e66b22a210967c439891d5988b829afa5f5345fe))
* schema definition for inline defined  UploadDataResponse ([9a1d5cc](https://github.com/com-pas/compas-scl-data-service/commit/9a1d5cc73b3ab82b698dcb0767e83c8d189324dd))
* typo in scope ([a8869af](https://github.com/com-pas/compas-scl-data-service/commit/a8869af636e31d6dd75b65e76d779ae3d898e1c3))
* use inheritance for DataEntry and DataEntryWithContent ([0afc97d](https://github.com/com-pas/compas-scl-data-service/commit/0afc97d6af6ffc3d212ea7196de39e7256e9a6ab))
* version of api has been updated to 0.17.4 ([8c11713](https://github.com/com-pas/compas-scl-data-service/commit/8c11713f4277b904dbaf26bf36a31d8b516e2c9e))

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
