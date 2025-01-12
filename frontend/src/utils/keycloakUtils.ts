import keycloak from "../lib/keycloak";

export const login = () => {
    keycloak.login().then(r => console.log("Logged in"));
};

export const register = () => {
    keycloak.register().then(r => console.log("Registered"));
};

export const logout = () => {
    keycloak.logout().then(r => console.log("Logged out"));
};
