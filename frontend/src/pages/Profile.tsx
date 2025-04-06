import React, { useContext, useState, useEffect, useCallback } from 'react';
import { 
    Container, Typography, Box, Avatar, Divider, 
    Pagination, Stack, CircularProgress, FormControl,
    InputLabel, Select, MenuItem, SelectChangeEvent,
    Button, Alert
} from '@mui/material';
import { KeycloakContext } from '../context/KeycloakContext';
import { Navigate } from 'react-router-dom';
import Topic from '../components/Topic';
import apiProxy from '../lib/apiProxy';
import { TopicResponseDto } from '../lib/topicService';

const profileAsciiArt = `
  _____            __ _ _      
 |  __ \\          / _(_) |     
 | |__) | __ ___ | |_ _| | ___ 
 |  ___/ '__/ _ \\|  _| | |/ _ \\
 | |   | | | (_) | | | | |  __/
 |_|   |_|  \\___/|_| |_|_|\\___|
`;

const Profile = () => {
    const { authenticated, userDetails } = useContext(KeycloakContext);
    const [topics, setTopics] = useState<TopicResponseDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [totalPages, setTotalPages] = useState(1);
    const [totalItems, setTotalItems] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [currentPage, setCurrentPage] = useState(1);

    const fetchUserTopics = useCallback(async () => {
        setLoading(true);
        try {
            const params = {
                page: currentPage - 1,
                pageSize: pageSize
            };
            
            const response = await apiProxy.users.getTopics(params);
            const data = response.data;
            
            setTopics(data.content || data.items || []);
            setTotalPages(data.totalPages || 1);
            setTotalItems(data.totalElements || data.totalItems || 0);
            setLoading(false);
        } catch (err: any) {
            setError("Failed to load topics: " + (err.message || "Unknown error"));
            setLoading(false);
        }
    }, [currentPage, pageSize]);

    useEffect(() => {
        if (authenticated) {
            fetchUserTopics();
        }
    }, [fetchUserTopics, authenticated]);

    const dismissError = () => {
        setError("");
    };

    if (!authenticated) {
        return <Navigate to="/home" replace />;
    }

    const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
        setCurrentPage(value);
    };
    
    const handlePageSizeChange = (event: SelectChangeEvent<number>) => {
        const newSize = event.target.value as number;
        setPageSize(newSize);
        setCurrentPage(1);
    };

    return (
        <Container className="retro-container crt-effect" sx={{ display: 'flex', flexDirection: 'column', mt: 4 }}>
            <Box className="ascii-art" sx={{ mb: 3, color: "var(--accent-color)", textAlign: "center" }}>
                <pre>{profileAsciiArt}</pre>
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
            
            <Box sx={{ p: 3, border: "2px solid var(--border-color)", bgcolor: "rgba(0, 0, 0, 0.2)", mb: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                    <Avatar 
                        sx={{ 
                            width: 80, 
                            height: 80, 
                            mr: 3, 
                            bgcolor: 'var(--secondary-color)',
                            border: '2px solid var(--border-color)'
                        }}
                    >
                        {userDetails?.name?.charAt(0)?.toUpperCase() || 'U'}
                    </Avatar>
                    <Box>
                        <Typography sx={{ 
                            fontFamily: "'VT323', monospace", 
                            color: "var(--accent-color)",
                            fontSize: "2rem"
                        }} gutterBottom>
                            {userDetails?.name || 'User'}
                        </Typography>
                        <Typography sx={{ 
                            fontFamily: "'Courier Prime', monospace", 
                            color: "var(--text-color)",
                            fontSize: "1rem"
                        }}>
                            {userDetails?.email || 'No email available'}
                        </Typography>
                    </Box>
                </Box>
                
                <Divider sx={{ my: 3, borderColor: 'var(--border-color)' }} />
                
                <Box>
                    <Typography sx={{ 
                        fontFamily: "'VT323', monospace", 
                        color: "var(--accent-color)",
                        fontSize: "1.2rem",
                        mb: 1
                    }}>
                        &gt; ACCOUNT INFORMATION &lt;
                    </Typography>
                    {userDetails?.firstName && userDetails?.lastName && (
                        <Typography sx={{ 
                            fontFamily: "'Courier Prime', monospace", 
                            color: "var(--text-color)",
                            fontSize: "1rem"
                        }}>
                            <strong>Name:</strong> {userDetails.firstName} {userDetails.lastName}
                        </Typography>
                    )}
                </Box>
            </Box>

            <Box sx={{ mb: 3, p: 2, border: "1px dashed var(--border-color)", bgcolor: "rgba(0, 0, 0, 0.2)" }}>
                <Typography sx={{ 
                    fontFamily: "'VT323', monospace", 
                    color: "var(--accent-color)",
                    fontSize: "1.2rem",
                    mb: 2
                }}>
                    &gt; MY TOPICS &lt;
                </Typography>
                
                {loading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4, mb: 4 }}>
                        <CircularProgress sx={{ color: 'var(--accent-color)' }} />
                    </Box>
                ) : topics.length > 0 ? (
                    <>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                                <Typography sx={{ 
                                    fontFamily: "'VT323', monospace", 
                                    color: "var(--text-color)",
                                    fontSize: "1rem"
                                }}>
                                    Showing {topics.length} of {totalItems} topics
                                </Typography>
                                
                                <FormControl 
                                    variant="outlined" 
                                    size="small"
                                    sx={{ 
                                        minWidth: 120,
                                        '& .MuiOutlinedInput-root': {
                                            fontFamily: "'VT323', monospace",
                                            fontSize: "1rem",
                                            color: "var(--text-color)",
                                            borderColor: "var(--border-color)",
                                            '& fieldset': {
                                                borderColor: "var(--border-color)",
                                            },
                                            '&:hover fieldset': {
                                                borderColor: "var(--accent-color)",
                                            },
                                        },
                                        '& .MuiInputLabel-root': {
                                            fontFamily: "'VT323', monospace",
                                            fontSize: "1rem",
                                            color: "var(--text-color)",
                                        },
                                    }}
                                >
                                    <InputLabel id="items-per-page-label">Items per page</InputLabel>
                                    <Select
                                        labelId="items-per-page-label"
                                        value={pageSize}
                                        onChange={handlePageSizeChange}
                                        label="Items per page"
                                    >
                                        <MenuItem value={5}>5</MenuItem>
                                        <MenuItem value={10}>10</MenuItem>
                                        <MenuItem value={15}>15</MenuItem>
                                    </Select>
                                </FormControl>
                            </Box>
                        </Box>
                        
                        {topics.map((topic) => (
                            <Topic key={topic.id} topic={topic} />
                        ))}
                        
                        {totalPages > 1 && (
                            <Stack spacing={2} sx={{ mt: 3, alignItems: 'center' }}>
                                <Pagination 
                                    count={totalPages} 
                                    page={currentPage} 
                                    onChange={handlePageChange}
                                    sx={{
                                        '& .MuiPaginationItem-root': {
                                            fontFamily: "'VT323', monospace",
                                            fontSize: "1.2rem",
                                            color: "var(--text-color)",
                                            border: "1px solid var(--border-color)",
                                        },
                                        '& .MuiPaginationItem-root.Mui-selected': {
                                            backgroundColor: "var(--secondary-color)",
                                            color: "var(--bg-color)",
                                        }
                                    }}
                                />
                            </Stack>
                        )}

                        {topics.length > 0 && (
                            <Box sx={{ 
                                border: "2px dashed var(--border-color)", 
                                padding: 2, 
                                mt: 2,
                                display: "flex",
                                justifyContent: "center" 
                            }}>
                                <Typography sx={{ 
                                    fontFamily: "'VT323', monospace", 
                                    color: "var(--text-color)", 
                                    textAlign: "center",
                                    fontSize: "1.2rem"
                                }}>
                                    -- End of Page {currentPage} / {totalPages} -- <span className="blink">█</span>
                                </Typography>
                            </Box>
                        )}
                    </>
                ) : (
                    <Box sx={{ 
                        p: 4, 
                        border: '2px dashed var(--border-color)', 
                        textAlign: 'center', 
                        mt: 2 
                    }}>
                        <Typography sx={{ 
                            fontFamily: "'VT323', monospace", 
                            color: "var(--text-color)", 
                            fontSize: "1.2rem" 
                        }}>
                            NO TOPICS FOUND <span className="blink">_</span>
                        </Typography>
                        <Typography 
                            sx={{ 
                                fontFamily: "'Courier Prime', monospace", 
                                color: "var(--accent-color)", 
                                mt: 1
                            }}
                        >
                            You haven't created any topics yet.
                        </Typography>
                    </Box>
                )}
            </Box>
        </Container>
    );
};

export default Profile; 