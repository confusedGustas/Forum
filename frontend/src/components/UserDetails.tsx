import React, { useContext, useId } from "react";
import { KeycloakContext } from "../context/KeycloakContext";
import { Box, Typography, TextField, Paper, Divider } from "@mui/material";

const AccessTokenDisplay = () => {
    const { accessToken } = useContext(KeycloakContext);

    if (!accessToken) {
        return (
            <Typography sx={{ 
                fontFamily: "'VT323', monospace", 
                color: "var(--danger-color)", 
                textAlign: "center",
                fontSize: "1.2rem",
                mt: 2
            }}>
                [ACCESS DENIED] - AUTHENTICATION REQUIRED<span className="blink">█</span>
            </Typography>
        );
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
            // Failed to parse JWT
            return null;
        }
    };

    const userDetails = parseJwt(accessToken);

    return (
        <Paper className="retro-card" sx={{ width: "100%", maxWidth: "600px", mt: 2 }}>
            <Box sx={{ p: 2 }}>
                <Typography variant="h6" sx={{ 
                    fontFamily: "'Press Start 2P', cursive", 
                    color: "var(--success-color)", 
                    fontSize: "1rem",
                    mb: 2
                }}>
                    &gt; USER AUTHENTICATED &lt;
                </Typography>
                
                <Divider sx={{ borderColor: "var(--border-color)", mb: 2 }} />
                
                <Typography sx={{ 
                    fontFamily: "'VT323', monospace", 
                    color: "var(--text-color)", 
                    fontSize: "1.2rem",
                    mb: 1
                }}>
                    ACCESS TOKEN:
                </Typography>
                
                <TextField
                    multiline
                    rows={3}
                    value={accessToken}
                    fullWidth
                    InputProps={{
                        readOnly: true,
                        sx: { 
                            fontFamily: "'Courier Prime', monospace", 
                            fontSize: "0.7rem",
                            color: "var(--accent-color)",
                            bgcolor: "rgba(0,0,0,0.3)"
                        }
                    }}
                    sx={{ mb: 2 }}
                />
                
                <Typography sx={{ 
                    fontFamily: "'VT323', monospace", 
                    color: "var(--text-color)", 
                    fontSize: "1.2rem",
                    mb: 1
                }}>
                    USER DETAILS:
                </Typography>
                
                {userDetails ? (
                    <Box sx={{ 
                        bgcolor: "rgba(0,0,0,0.3)", 
                        p: 1, 
                        border: "1px solid var(--border-color)",
                        overflowX: "auto" 
                    }}>
                        <pre style={{ 
                            fontFamily: "'Courier Prime', monospace", 
                            color: "var(--success-color)",
                            fontSize: "0.8rem",
                            margin: 0
                        }}>
                            {JSON.stringify(userDetails, null, 2)}
                        </pre>
                    </Box>
                ) : (
                    <Typography sx={{ 
                        fontFamily: "'VT323', monospace", 
                        color: "var(--danger-color)"
                    }}>
                        ERROR: FAILED TO DECODE USER DETAILS
                    </Typography>
                )}
            </Box>
        </Paper>
    );
};

export default AccessTokenDisplay;