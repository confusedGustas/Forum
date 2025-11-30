package org.site.forum.config.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleUtils {

    public static final String ROLES = "roles";
    public static final String ROLE_CLIENT_ADMIN = "ROLE_client_admin";
    public static final String ROLE_ADMIN = "ROLE_admin";
    public static final String ADMIN = "admin";
    public static final String CLIENT_ADMIN = "client_admin";
    public static final String REALM_ACCESS = "realm_access";
    public static final String RESOURCE_ACCESS = "resource_access";

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        if (hasAuthority(authentication, ROLE_CLIENT_ADMIN, ROLE_ADMIN)) {
            return true;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            if (hasRealmRole(jwt)) {
                return true;
            }

            return hasResourceRole(jwt);
        }

        return false;
    }

    private static boolean hasRealmRole(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (!(realmAccess != null && realmAccess.get(ROLES) instanceof Collection<?> roles)) {
            return false;
        }

        return roles.contains(ADMIN);
    }

    private static boolean hasResourceRole(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess == null) {
            return false;
        }

        return resourceAccess.values().stream()
                .filter(v -> v instanceof Map<?, ?>)
                .map(v -> ((Map<?, ?>) v).get(ROLES))
                .filter(Collection.class::isInstance)
                .map(Collection.class::cast)
                .anyMatch(collection -> collection.contains(CLIENT_ADMIN));
    }

    private static boolean hasAuthority(Authentication auth, String... roles) {
        Set<String> target = Set.of(roles);
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(target::contains);
    }

}
