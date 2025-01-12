import React, { useContext } from "react";
import { KeycloakContext } from "../context/KeycloakContext";
import { logout } from "../utils/keycloakUtils";

const UserDetails = () => {
    const { userDetails, authenticated } = useContext(KeycloakContext);

    if (!authenticated) {
        return null;
    }

    return (
        <div>
            <h2>User Details</h2>
            <pre>{JSON.stringify(userDetails, null, 2)}</pre>
            <button onClick={logout}>Logout</button>
        </div>
    );
};

export default UserDetails;
