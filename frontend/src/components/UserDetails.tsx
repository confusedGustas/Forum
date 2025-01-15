import React, { useContext } from "react";
import { KeycloakContext } from "../context/KeycloakContext";

const AccessTokenDisplay = () => {
    const { accessToken } = useContext(KeycloakContext);

    if (!accessToken) {
        return <div>Not authenticated</div>;
    }

    return (
        <div>
            <h3>Access Token:</h3>
            <textarea readOnly rows={10} cols={50} value={accessToken} />
        </div>
    );
};

export default AccessTokenDisplay;
