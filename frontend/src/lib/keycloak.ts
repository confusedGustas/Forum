import Keycloak from 'keycloak-js';
import config from './config';

const keycloak = new Keycloak({
    url: config.keycloak.url,
    realm: config.keycloak.realm,
    clientId: config.keycloak.clientId
});

keycloak.onAuthSuccess = () => {
    if (keycloak.token) {
        localStorage.setItem('keycloak_token', keycloak.token);
    }
};

keycloak.onAuthError = (error) => {
    localStorage.removeItem('keycloak_token');
};

keycloak.onAuthRefreshSuccess = () => {
    if (keycloak.token) {
        localStorage.setItem('keycloak_token', keycloak.token);
    }
};

keycloak.onAuthRefreshError = () => {
    localStorage.removeItem('keycloak_token');
};

keycloak.onAuthLogout = () => {
    localStorage.removeItem('keycloak_token');
};

keycloak.onTokenExpired = () => {
    keycloak.updateToken(70)
        .then((refreshed) => {
            if (refreshed) {
                if (keycloak.token) {
                    localStorage.setItem('keycloak_token', keycloak.token);
                }
            }
        })
        .catch(() => {
            localStorage.removeItem('keycloak_token');
        });
};

export default keycloak;