import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    url: 'http://localhost:8181',
    realm: 'forum',
    clientId: 'client'
});

keycloak.onAuthSuccess = () => {
    console.log('Auth Success');
};

keycloak.onAuthError = (error) => {
    console.error('Auth Error:', error);
};

keycloak.onAuthRefreshSuccess = () => {
    console.log('Auth Refresh Success');
};

keycloak.onAuthRefreshError = () => {
    console.log('Auth Refresh Error');
};

keycloak.onAuthLogout = () => {
    console.log('Auth Logout');
};

keycloak.onTokenExpired = () => {
    console.log('Token Expired');
    keycloak.updateToken(30).catch(() => {
        console.log('Failed to refresh token');
    });
};

export default keycloak;