import React, { useContext } from "react";
import { register } from "../utils/keycloakUtils";
import { KeycloakContext } from "../context/KeycloakContext";

const RegisterButton = () => {
    const { authenticated } = useContext(KeycloakContext);

    if (authenticated) {
        return null;
    }

    return <button onClick={register}>Register</button>;
};

export default RegisterButton;
