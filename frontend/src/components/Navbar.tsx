import React, { useContext } from "react";
import { AppBar, Toolbar, Typography, Container, Box, Button } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import "./css/Navbar.css";
import { KeycloakContext } from "../context/KeycloakContext";
import keycloak from "../lib/keycloak";

const Navbar = () => {
    const navigate = useNavigate();
    const { authenticated, login, logout, userDetails } = useContext(KeycloakContext);
    
    const handleRegister = () => {
        keycloak.register({
            redirectUri: `${window.location.origin}/home`
        });
    };

    return (
        <AppBar position="sticky" className="navbar crt-effect">
            <Toolbar>
                <Container maxWidth="lg">
                    <Box className="navbar-container" sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                        <Typography className="brand-name" variant={"h5"} component={Link} to="/home" sx={{ textDecoration: 'none', color: 'inherit' }}>
                            &gt;_Forum<span className="blink">|</span>
                        </Typography>
                        
                        <Box>
                            {authenticated ? (
                                <>
                                    <Button 
                                        color="inherit" 
                                        component={Link} 
                                        to="/profile"
                                        sx={{ marginRight: 2 }}
                                    >
                                        Profile
                                    </Button>
                                    <Button 
                                        color="inherit" 
                                        onClick={logout}
                                    >
                                        Logout
                                    </Button>
                                </>
                            ) : (
                                <>
                                    <Button 
                                        color="inherit" 
                                        onClick={login}
                                        sx={{ marginRight: 2 }}
                                    >
                                        Login
                                    </Button>
                                    <Button 
                                        color="inherit" 
                                        onClick={handleRegister}
                                    >
                                        Register
                                    </Button>
                                </>
                            )}
                        </Box>
                    </Box>
                </Container>
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;
