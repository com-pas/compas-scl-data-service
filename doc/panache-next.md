Quarkus Hibernate with Panache Next
==================================

Requirements: 
- Quarkus 3.31.x or newer (already updated in the project)

Panache Next API is now used in the module `repository` for custom plugins resource.
It relies on a Hibernate annotation processor, which is enabled in `repository/pom.xml`, and on the 
existing Hibernate ORM datasource configuration, so **no additional database configuration is required** 
beyond what is already documented.

> For more information, please refer to the [official documentation](https://quarkus.io/version/main/guides/hibernate-panache-next).



