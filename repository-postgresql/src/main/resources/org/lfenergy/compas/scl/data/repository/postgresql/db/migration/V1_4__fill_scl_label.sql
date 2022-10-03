/**
 * SPDX-FileCopyrightText: 2021 Alliander N.V.
 *
 * SPDX-License-Identifier: Apache-2.0
*/

--
-- Creating table to hold SCL Labels. Labels should be unique, because they are part of the PK.
--
insert into scl_label(scl_id, major_version, minor_version, patch_version, label_value)
select id, major_version, minor_version, patch_version,
       unnest(
          xpath( '(/scl:SCL/scl:Private[@type="compas_scl"]/compas:Labels/compas:Label/text())'
               , scl_data::xml
               , ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL'],
                       ARRAY['compas', 'https://www.lfenergy.org/compas/extension/v1']])
       )::text as label_value
  from scl_file
 -- If the query is executed again, only if a file hasn't been processed will be included.
 where not exists (select 1
                     from scl_label
                    where scl_label.scl_id        = scl_file.id
                    and   scl_label.major_version = scl_file.major_version
                    and   scl_label.minor_version = scl_file.minor_version
                    and   scl_label.patch_version = scl_file.patch_version
                  )
 group by id, major_version, minor_version, patch_version, label_value -- make sure the label_value are unique per file.
 order by id, major_version, minor_version, patch_version, label_value -- Just some nice order to insert rows
