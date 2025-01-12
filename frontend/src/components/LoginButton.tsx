import React, { useContext } from "react";
import { login } from "../utils/keycloakUtils";
import { KeycloakContext } from "../context/KeycloakContext";

const LoginButton = () => {
    const { authenticated } = useContext(KeycloakContext);

    if (authenticated) {
        return null;
    }

    return <button onClick={login}>Login</button>;
};

export default LoginButton;
