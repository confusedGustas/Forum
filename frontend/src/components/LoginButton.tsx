import React, { useContext } from "react";
import { login } from "../utils/keycloakUtils";
import { KeycloakContext } from "../context/KeycloakContext";
import { Button } from "@mui/material";

const LoginButton = () => {
    const { authenticated } = useContext(KeycloakContext);

    if (authenticated) {
        return null;
    }

    return (
        <Button 
            onClick={login}
            className="retro-button"
            sx={{ 
                fontFamily: "'VT323', monospace",
                fontSize: "1.2rem",
                bgcolor: "transparent",
                color: "var(--success-color)",
                border: "2px solid var(--success-color)",
                borderRadius: 0,
                padding: "8px 16px",
                "&:hover": {
                    bgcolor: "var(--success-color)",
                    color: "var(--bg-color)"
                }
            }}
        >
            &gt; LOGIN &lt;
        </Button>
    );
};

export default LoginButton;
