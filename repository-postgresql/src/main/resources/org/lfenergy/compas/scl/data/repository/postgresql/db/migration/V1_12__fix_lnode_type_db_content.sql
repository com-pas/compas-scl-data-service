-- SPDX-FileCopyrightText: 2026 Alliander N.V.
--
-- SPDX-License-Identifier: Apache-2.0

/* Fix LNodeTypeLibrary type in content */
update scl_file 
set scl_data = replace(scl_data, '<compas:SclFileType>SSD</compas:SclFileType>', '<compas:SclFileType>LNT</compas:SclFileType>') 
where id = 'fc55c46d-c109-4ccd-bf66-9f1d0e135689';
