package org.lfenergy.compas.scl.data.rest.v1;

import org.lfenergy.compas.scl.data.rest.PluginsCustomResourcesApi;
import org.lfenergy.compas.scl.data.rest.dto.DataEntryWithContent;
import org.lfenergy.compas.scl.data.rest.dto.GetAllData200Response;
import org.lfenergy.compas.scl.data.rest.dto.UploadData201Response;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class CompasPluginsResource implements PluginsCustomResourcesApi {

    @Override
    public GetAllData200Response getAllData(String type,
                                            Date uploadedAfter,
                                            Date uploadedBefore,
                                            String name,
                                            Integer page,
                                            Integer size) {
        // TODO
        return null;
    }

    @Override
    public DataEntryWithContent getDataById(UUID id) {
        // TODO
        return null;
    }

    @Override
    public UploadData201Response uploadData(String type,
                                            String name,
                                            String contentType,
                                            InputStream contentInputStream,
                                            String dataCompatibilityVersion,
                                            String description,
                                            String version,
                                            String nextVersionType) {
        // TODO
        return null;
    }
}
