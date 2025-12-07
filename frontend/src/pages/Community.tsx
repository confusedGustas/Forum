import {useNavigate} from 'react-router-dom';
import React, {useContext, useEffect, useState} from 'react';
import {KeycloakContext} from '../context/KeycloakContext';
import apiProxy from '../lib/apiProxy';
import {Box, Button, CircularProgress, Container, Typography} from '@mui/material';
import Community from '../components/Community';

const CommunityList = () => {
    const navigate = useNavigate();
    const { authenticated, login } = useContext(KeycloakContext);

    const [communities, setCommunities] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchCommunities = async () => {
            setLoading(true);
            try {
                const response = await apiProxy.communities.getAll();
                setCommunities(response.data);
                setLoading(false);
            } catch (err: any) {
                if (err.response?.status === 401) {
                    setError("Authentication required: Please log in to view communities");
                    setTimeout(() => {
                        login();
                    }, 1500);
                } else {
                    setError("Failed to load communities: " + (err.message || "Unknown error"));
                }
                setLoading(false);
            }
        };

        fetchCommunities();
    }, [login]);

    const handleNewCommunity = () => {
        if (!authenticated) {
            setError("You must be logged in to create a community");
            setTimeout(() => {
                login();
            }, 1500);
            return;
        }
        navigate("/new-community");
    };

    const dismissError = () => {
        setError("");
    };

    return (
        <Container className="retro-container crt-effect" sx={{ display: 'flex', flexDirection: 'column', mt: 4 }}>
            <Box className="ascii-art" sx={{ mb: 3, color: "var(--accent-color)", textAlign: "center" }}>
                <Typography variant="h4">Communities</Typography>
            </Box>

            {error && (
                <Box sx={{
                    bgcolor: "rgba(255, 0, 0, 0.1)",
                    p: 2,
                    mb: 3,
                    border: "2px solid var(--danger-color)",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center"
                }}>
                    <Typography sx={{
                        fontFamily: "'VT323', monospace",
                        color: "var(--danger-color)",
                        fontSize: "1.2rem"
                    }}>
                        {error}
                    </Typography>
                    <Button
                        onClick={dismissError}
                        sx={{
                            fontFamily: "'VT323', monospace",
                            fontSize: "1rem",
                            bgcolor: "transparent",
                            color: "var(--danger-color)",
                            border: "1px solid var(--danger-color)",
                            borderRadius: 0,
                            ml: 2,
                            "&:hover": {
                                bgcolor: "rgba(255, 0, 0, 0.1)",
                            }
                        }}
                    >
                        [DISMISS]
                    </Button>
                </Box>
            )}

            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Button
                    onClick={handleNewCommunity}
                    sx={{
                        alignSelf: 'flex-end',
                        fontFamily: "'VT323', monospace",
                        fontSize: "1.2rem",
                        bgcolor: "transparent",
                        color: "var(--text-color)",
                        border: "2px solid var(--border-color)",
                        borderRadius: 0,
                        padding: "8px 16px",
                        "&:hover": {
                            bgcolor: "var(--text-color)",
                            color: "var(--bg-color)"
                        }
                    }}
                    variant="contained"
                >
                    [NEW COMMUNITY]
                </Button>
            </Box>

            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4, mb: 4 }}>
                    <CircularProgress sx={{ color: 'var(--accent-color)' }} />
                </Box>
            ) : communities.length > 0 ? (
                <>
                    {communities.map((community, index) => (
                        <Community key={community.id || index} community={community} />
                    ))}
                </>
            ) : (
                <Box
                    sx={{
                        p: 4,
                        border: '2px dashed var(--border-color)',
                        textAlign: 'center',
                        mt: 2
                    }}
                >
                    <Typography
                        sx={{
                            fontFamily: "'VT323', monospace",
                            color: "var(--text-color)",
                            fontSize: "1.2rem"
                        }}
                    >
                        NO COMMUNITIES FOUND <span className="blink">_</span>
                    </Typography>
                </Box>
            )}
        </Container>
    );
};

export default CommunityList;