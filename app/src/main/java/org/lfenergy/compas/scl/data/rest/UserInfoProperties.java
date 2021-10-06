// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "compas.userinfo")
public interface UserInfoProperties {
    @WithName("name.claimname")
    String name();

    @WithName("who.claimname")
    String who();

    @WithName("session.warning")
    int sessionWarning();

    @WithName("session.expires")
    int sessionExpires();
}
