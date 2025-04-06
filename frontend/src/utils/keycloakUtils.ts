import keycloak from "../lib/keycloak";

export const login = () => {
    keycloak.login({
        redirectUri: `${window.location.origin}/home`
    })
    .then(() => {})
    .catch(error => {});
};

export const register = () => {
    keycloak.register({
        redirectUri: `${window.location.origin}/home`
    })
    .then(() => {})
    .catch(error => {});
};

export const logout = () => {
    localStorage.removeItem('keycloak_token');
    
    keycloak.logout({
        redirectUri: window.location.origin
    })
    .then(() => {})
    .catch(error => {});
};

export const isAuthenticated = () => {
    return !!keycloak.authenticated;
};

export const getToken = () => {
    return localStorage.getItem('keycloak_token') || keycloak.token;
};

export const refreshToken = async () => {
    try {
        const refreshed = await keycloak.updateToken(30);
        if (refreshed && keycloak.token) {
            localStorage.setItem('keycloak_token', keycloak.token);
            return true;
        }
        return false;
    } catch (error) {
        return false;
    }
};
