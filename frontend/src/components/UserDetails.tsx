import React, { useContext, useId } from "react";
import { KeycloakContext } from "../context/KeycloakContext";

const AccessTokenDisplay = () => {
    const { accessToken } = useContext(KeycloakContext);

    if (!accessToken) {
        return <div>Not authenticated</div>;
    }

    const parseJwt = (token: string) => {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            console.error('Failed to parse JWT', e);
            return null;
        }
    };

    const userDetails = parseJwt(accessToken);

    return (
        <div>
            <h3>Access Token:</h3>
            <textarea readOnly rows={10} cols={50} value={accessToken} />
            <h3>User Details</h3>
            {userDetails ? (
                <pre>{JSON.stringify(userDetails, null, 2)}</pre>
            ) : (
                <div>Failed to load user details</div>
            )}
        </div>
    );
};

export default AccessTokenDisplay;