// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;

import static org.lfenergy.compas.scl.data.rest.Constants.*;

@TestSecurity(user = "test-editor", roles = {"SCD_" + READ_ROLE, "SCD_" + CREATE_ROLE, "SCD_" + UPDATE_ROLE})
@JwtSecurity(claims = {
        // Default the claim "name" is configured for Who, so we will set this claim for the test.
        @Claim(key = "name", value = AbstractServerEndpointAsEditorTest.USERNAME)
})
public abstract class AbstractServerEndpointAsEditorTest extends AbstractServerEndpointTest {
    public static final String USERNAME = "Test Editor";

}
