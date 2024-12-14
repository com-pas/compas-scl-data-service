package org.lfenergy.compas.scl.data.rest.v1;

import org.apache.commons.lang3.ObjectUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {
    }

    public static OffsetDateTime convertToOffsetDateTime(Date date) {
        return ObjectUtils.isEmpty(date) ? null : date.toInstant().atOffset(ZoneOffset.UTC);
    }

    public static Date convertToDate(OffsetDateTime date) {
        return ObjectUtils.isEmpty(date) ? null : new Date(date.toInstant().toEpochMilli());
    }
}
