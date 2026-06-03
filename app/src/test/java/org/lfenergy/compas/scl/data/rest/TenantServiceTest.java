// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.GLOBAL_TENANT;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private JsonWebToken jsonWebToken;

    @InjectMocks
    private TenantService tenantService;

    @Test
    void resolveTenant_WhenIssuerIsNull_ThenReturnsGlobalTenant() {
        when(jsonWebToken.getIssuer()).thenReturn(null);

        assertEquals(GLOBAL_TENANT, tenantService.resolveTenant());
    }

    @Test
    void resolveTenant_WhenIssuerIsBlank_ThenReturnsGlobalTenant() {
        when(jsonWebToken.getIssuer()).thenReturn("  ");

        assertEquals(GLOBAL_TENANT, tenantService.resolveTenant());
    }

    @Test
    void resolveTenant_WhenIssuerHasRealmPath_ThenReturnsRealmAsTenant() {
        when(jsonWebToken.getIssuer()).thenReturn("http://host/auth/realms/compas");

        assertEquals("compas", tenantService.resolveTenant());
    }

    @Test
    void resolveTenant_WhenIssuerHasDifferentRealm_ThenReturnsCorrectTenant() {
        when(jsonWebToken.getIssuer()).thenReturn("http://keycloak.example.com/realms/my-company");

        assertEquals("my-company", tenantService.resolveTenant());
    }

    @Test
    void resolveTenant_WhenIssuerHasNoPath_ThenReturnsGlobalTenant() {
        when(jsonWebToken.getIssuer()).thenReturn("http://host");

        assertEquals(GLOBAL_TENANT, tenantService.resolveTenant());
    }

    @Test
    void resolveTenant_WhenIssuerHasTrailingSlash_ThenReturnsRealmAsTenant() {
        when(jsonWebToken.getIssuer()).thenReturn("http://host/realms/compas/");

        assertEquals("compas", tenantService.resolveTenant());
    }

    @Test
    void resolveTenant_WhenGetIssuerThrowsException_ThenReturnsGlobalTenant() {
        when(jsonWebToken.getIssuer()).thenThrow(new RuntimeException("No token"));

        assertEquals(GLOBAL_TENANT, tenantService.resolveTenant());
    }
}
