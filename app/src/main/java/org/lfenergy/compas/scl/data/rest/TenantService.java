// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.GLOBAL_TENANT;

/**
 * Service responsible for resolving the tenant name for the current request.
 * <p>
 * Tenant resolution rules:
 * <ul>
 *   <li>When authentication is <b>disabled</b> (no JWT issuer), the hardcoded
 *       tenant {@code "global"} is used.</li>
 *   <li>When <b>OIDC authentication is enabled</b>, the tenant name is derived
 *       from the realm segment of the JWT issuer URL
 *       (e.g. {@code http://host/auth/realms/compas} → tenant {@code "compas"}).</li>
 * </ul>
 */
@ApplicationScoped
public class TenantService {

    private final JsonWebToken jsonWebToken;

    @Inject
    public TenantService(JsonWebToken jsonWebToken) {
        this.jsonWebToken = jsonWebToken;
    }

    /**
     * Resolve the tenant name for the active request.
     *
     * @return The resolved tenant name; never {@code null}.
     */
    public String resolveTenant() {
        try {
            String issuer = jsonWebToken.getIssuer();
            if (issuer == null || issuer.isBlank()) {
                return GLOBAL_TENANT;
            }
            // Issuer URL format (Keycloak): http://<host>/auth/realms/<realm>
            // Strip any trailing slash so that "http://host/realms/compas/" is treated
            // the same as "http://host/realms/compas".
            String normalized = issuer.endsWith("/") ? issuer.substring(0, issuer.length() - 1) : issuer;
            int lastSlash = normalized.lastIndexOf('/');
            // Guard against URLs with no path (e.g. "http://host") by ensuring the
            // character before lastSlash is not also a slash (i.e. not the "://" part).
            if (lastSlash > 0 && normalized.charAt(lastSlash - 1) != '/') {
                String segment = normalized.substring(lastSlash + 1);
                if (!segment.isBlank()) {
                    return segment;
                }
            }
        } catch (Exception e) {
            // No token available – fall back to global tenant.
        }
        return GLOBAL_TENANT;
    }
}
