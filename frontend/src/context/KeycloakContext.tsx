import React, { createContext, useEffect, useState, ReactNode } from "react";
import type Keycloak from "keycloak-js";
import keycloak from "../lib/keycloak";

interface KeycloakContextType {
    keycloak: Keycloak | null;
    authenticated: boolean;
    userDetails: Record<string, any> | null;
    accessToken: string | null;
}

export const KeycloakContext = createContext<KeycloakContextType>({
    keycloak: null,
    authenticated: false,
    userDetails: null,
    accessToken: null,
});

export const KeycloakProvider = ({ children }: { children: ReactNode }) => {
    const [authenticated, setAuthenticated] = useState(false);
    const [userDetails, setUserDetails] = useState<Record<string, any> | null>(null);
    const [accessToken, setAccessToken] = useState<string | null>(null);

    useEffect(() => {
        const initKeycloak = async () => {
            try {
                const auth = await keycloak.init({
                    onLoad: "check-sso",
                    silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
                });

                setAuthenticated(auth);

                if (auth) {
                    setAccessToken(keycloak.token || null);

                    try {
                        const profile = await keycloak.loadUserProfile();
                        setUserDetails(profile);
                    } catch (error) {
                        console.error("Failed to load user profile:", error);
                    }
                }
            } catch (error) {
                console.error("Failed to initialize Keycloak:", error);
            }
        };

        void initKeycloak();
    }, []);

    return (
        <KeycloakContext.Provider value={{ keycloak, authenticated, userDetails, accessToken }}>
            {children}
        </KeycloakContext.Provider>
    );
};
