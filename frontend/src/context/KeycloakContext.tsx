import React, { createContext, useEffect, useState, ReactNode } from "react";
import type Keycloak from "keycloak-js";
import keycloak from "../lib/keycloak";
import config from "../lib/config";

interface KeycloakContextType {
    keycloak: Keycloak | null;
    authenticated: boolean;
    userDetails: Record<string, any> | null;
    accessToken: string | null;
    login: () => void;
    logout: () => void;
}

export const KeycloakContext = createContext<KeycloakContextType>({
    keycloak: null,
    authenticated: false,
    userDetails: null,
    accessToken: null,
    login: () => {},
    logout: () => {},
});

export const KeycloakProvider = ({ children }: { children: ReactNode }) => {
    const [authenticated, setAuthenticated] = useState(false);
    const [userDetails, setUserDetails] = useState<Record<string, any> | null>(null);
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [initializationAttempts, setInitializationAttempts] = useState(0);

    const login = () => {
        keycloak.login({
            redirectUri: `${window.location.origin}/home`
        });
    };

    const logout = () => {
        localStorage.removeItem('keycloak_token');
        keycloak.logout({
            redirectUri: window.location.origin
        });
    };

    const setupTokenRefresh = () => {
        if (keycloak.token) {
            localStorage.setItem('keycloak_token', keycloak.token);
            
            const updateTokenInterval = setInterval(() => {
                keycloak.updateToken(70)
                    .then((refreshed) => {
                        if (refreshed) {
                            localStorage.setItem('keycloak_token', keycloak.token!);
                            setAccessToken(keycloak.token);
                        }
                    })
                    .catch(() => {
                        logout();
                    });
            }, 60000);
            
            return () => clearInterval(updateTokenInterval);
        }
    };

    const extractUserInfoFromToken = (token: string): Record<string, any> => {
        try {
            const payload = token.split('.')[1];
            const decoded = JSON.parse(atob(payload));
            
            return {
                id: decoded.sub,
                name: decoded.preferred_username || decoded.name || 'User',
                email: decoded.email,
            };
        } catch (error) {
            return {
                id: 'unknown',
                name: 'User'
            };
        }
    };

    useEffect(() => {
        const initKeycloak = async () => {
            try {
                const auth = await keycloak.init({
                    onLoad: "check-sso",
                    silentCheckSsoRedirectUri: config.keycloak.silentCheckSsoRedirectUri,
                    checkLoginIframe: false,
                    enableLogging: false,
                    pkceMethod: 'S256',
                    checkLoginIframeInterval: 5,
                    flow: 'standard',
                    responseMode: 'fragment',
                });

                setAuthenticated(auth);

                if (auth) {
                    setAccessToken(keycloak.token || null);
                    
                    const clearTokenRefresh = setupTokenRefresh();
                    
                    if (keycloak.token) {
                        const tokenInfo = extractUserInfoFromToken(keycloak.token);
                        setUserDetails(tokenInfo);
                    }
                    
                    try {
                        keycloak.loadUserProfile()
                            .then(profile => {
                                setUserDetails(prevDetails => ({
                                    ...prevDetails,
                                    ...profile
                                }));
                            })
                            .catch(() => {});
                    } catch (error) {}
                    
                    return clearTokenRefresh;
                }
            } catch (error) {
                if (initializationAttempts < 3) {
                    setInitializationAttempts(prev => prev + 1);
                    
                    try {
                        const auth = await keycloak.init({
                            onLoad: "check-sso",
                            checkLoginIframe: false,
                            pkceMethod: 'S256',
                        });
                        
                        setAuthenticated(auth);
                        
                        if (auth) {
                            setAccessToken(keycloak.token || null);
                            const clearTokenRefresh = setupTokenRefresh();
                            
                            if (keycloak.token) {
                                const tokenInfo = extractUserInfoFromToken(keycloak.token);
                                setUserDetails(tokenInfo);
                            }
                            
                            return clearTokenRefresh;
                        }
                    } catch (fallbackError) {}
                }
            }
        };

        void initKeycloak();
    }, [initializationAttempts]);

    return (
        <KeycloakContext.Provider value={{ keycloak, authenticated, userDetails, accessToken, login, logout }}>
            {children}
        </KeycloakContext.Provider>
    );
};
