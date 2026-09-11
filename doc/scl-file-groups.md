# SCL File Groups

We have replaced the fixed file types SCD, SSD, ICT, etc with a dedicated table in the database `scl_file_type`, so different companies can have different file groups / types. By default this includes SSD, IID, ICD, SCD, CID, SED, ISD, STD, LNT and BAY. Users will only see groups for which they have at least `READ` permissions.

### How to add a new file group

* Add an entry to the `scl_file_type` database table, take note that the `code` value must be unique, at most 3 characters long and will be used as is path parameter in requests.
* Add a permission profile to the `application.properties`.

For example if you wanted to add the Basic Application Profile (BAP) file group, you can insert it in the db with.

```
insert into scl_file_group(code, name, description) values ('BAP', 'BasicApplicationProfile', 'Basic Application Profile');
```

And then add the permission profile to `application.properties`.

```
# Permissions for Basic Application Profile
quarkus.http.auth.policy.BAP_READ.roles-allowed=BAP_READ
quarkus.http.auth.permission.BAP_READ_GET.paths=/compas-scl-data-service/scl/v1/BAP/*
quarkus.http.auth.permission.BAP_READ_GET.policy=BAP_READ
...
```
