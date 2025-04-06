import React, { useContext } from "react";
import { register } from "../utils/keycloakUtils";
import { KeycloakContext } from "../context/KeycloakContext";
import { Button } from "@mui/material";

const RegisterButton = () => {
    const { authenticated } = useContext(KeycloakContext);

    if (authenticated) {
        return null;
    }

    return (
        <Button 
            onClick={register}
            className="retro-button"
            sx={{ 
                fontFamily: "'VT323', monospace",
                fontSize: "1.2rem",
                bgcolor: "transparent",
                color: "var(--warning-color)",
                border: "2px solid var(--warning-color)",
                borderRadius: 0,
                padding: "8px 16px",
                "&:hover": {
                    bgcolor: "var(--warning-color)",
                    color: "var(--bg-color)"
                }
            }}
        >
            &gt; REGISTER &lt;
        </Button>
    );
};

export default RegisterButton;
