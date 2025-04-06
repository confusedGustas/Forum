import React, { useState, useEffect, useContext, useCallback } from "react";
import { 
    Button, 
    Container, 
    Typography, 
    Box, 
    CircularProgress, 
    Alert, 
    Pagination,
    Stack,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    SelectChangeEvent,
    TextField,
    Grid
} from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import Topic from "../components/Topic";
import { TopicResponseDto } from "../lib/topicService";
import apiProxy from "../lib/apiProxy";
import { KeycloakContext } from "../context/KeycloakContext";

const asciiArt = `
  ______                               
 |  ____|                              
 | |__    ___   _ __  _   _  _ __ ___  
 |  __|  / _ \\ | '__|| | | || '_ \` _ \\ 
 | |    | (_) || |   | |_| || | | | | |
 |_|     \\___/ |_|    \\__,_||_| |_| |_|
`;

const Home = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { authenticated, login } = useContext(KeycloakContext);

    const [topics, setTopics] = useState<TopicResponseDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [totalPages, setTotalPages] = useState(1);
    const [totalItems, setTotalItems] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [currentPage, setCurrentPage] = useState(1);
    const [sortBy, setSortBy] = useState("createdAt");
    const [sortOrder, setSortOrder] = useState("DESC");
    const [searchTerms, setSearchTerms] = useState("");
    const [searchInput, setSearchInput] = useState("");

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const searchParam = params.get('search');
        const sortByParam = params.get('sortBy');
        const sortOrderParam = params.get('sortOrder');
        const pageParam = params.get('page');
        const pageSizeParam = params.get('pageSize');
        
        if (searchParam) setSearchTerms(searchParam);
        if (searchParam) setSearchInput(searchParam);
        if (sortByParam && ['title', 'rating'].includes(sortByParam)) setSortBy(sortByParam);
        if (sortOrderParam && ['ASC', 'DESC'].includes(sortOrderParam)) setSortOrder(sortOrderParam);
        if (pageParam) setCurrentPage(parseInt(pageParam, 10) || 1);
        if (pageSizeParam) setPageSize(parseInt(pageSizeParam, 10) || 10);
    }, [location.search]);

    const fetchTopics = useCallback(async () => {
        setLoading(true);
        try {
            const params: {
                limit: number;
                offset: number;
                search?: string;
                sortBy?: string;
                sortOrder?: 'ASC' | 'DESC';
            } = {
                limit: pageSize,
                offset: (currentPage - 1),
            };
            
            if (sortBy !== "createdAt") {
                params.sortBy = sortBy;
            }
            
            if (sortOrder) {
                params.sortOrder = sortOrder as 'ASC' | 'DESC';
            }
            
            if (searchTerms) {
                params.search = searchTerms;
            }
            
            const response = await apiProxy.search.topics(params);
            
            const data = response.data;
            
            setTopics(data.items || []);
            setTotalPages(data.totalPages || 1);
            setTotalItems(data.totalItems || 0);
            setLoading(false);
        } catch (err: any) {
            if (err.response?.status === 401) {
                setError("Authentication required: Please log in to view topics");
                setTimeout(() => {
                    login();
                }, 1500);
            } else {
                setError("Failed to load topics: " + (err.message || "Unknown error"));
            }
            setLoading(false);
        }
    }, [currentPage, pageSize, sortBy, sortOrder, searchTerms, login]);

    useEffect(() => {
        fetchTopics();
    }, [fetchTopics]);

    const handleNewPost = () => {
        if (!authenticated) {
            setError("You must be logged in to create a post");
            setTimeout(() => {
                login();
            }, 1500);
            return;
        }
        navigate("/new-post");
    };

    const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
        setCurrentPage(value);
        updateUrlParams({ page: value.toString() });
    };
    
    const handlePageSizeChange = (event: SelectChangeEvent<number>) => {
        const newSize = event.target.value as number;
        setPageSize(newSize);
        setCurrentPage(1);
        updateUrlParams({ pageSize: newSize.toString(), page: '1' });
    };

    const handleSortByChange = (event: SelectChangeEvent<string>) => {
        const newSortBy = event.target.value;
        setSortBy(newSortBy);
        updateUrlParams({ sortBy: newSortBy });
    };

    const handleSortOrderChange = (event: SelectChangeEvent<string>) => {
        const newSortOrder = event.target.value;
        setSortOrder(newSortOrder);
        updateUrlParams({ sortOrder: newSortOrder });
    };

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearchInput(event.target.value);
    };

    const handleSearchSubmit = (event: React.FormEvent) => {
        event.preventDefault();
        setSearchTerms(searchInput);
        setCurrentPage(1);
        updateUrlParams({ search: searchInput, page: '1' });
    };

    const clearSearch = () => {
        setSearchInput("");
        setSearchTerms("");
        updateUrlParams({ search: null });
    };

    const updateUrlParams = (params: Record<string, string | null>) => {
        const searchParams = new URLSearchParams(location.search);
        
        Object.entries(params).forEach(([key, value]) => {
            if (value === null) {
                searchParams.delete(key);
            } else {
                searchParams.set(key, value);
            }
        });
        
        navigate({
            pathname: location.pathname,
            search: searchParams.toString()
        }, { replace: true });
    };

    const dismissError = () => {
        setError("");
    };
    
    return (
        <Container className="retro-container crt-effect" sx={{ display: 'flex', flexDirection: 'column', mt: 4 }}>
            <Box className="ascii-art" sx={{ mb: 3, color: "var(--accent-color)", textAlign: "center" }}>
                <pre>{asciiArt}</pre>
            </Box>
            
            {!authenticated && (
                <Alert 
                    severity="info" 
                    sx={{ 
                        backgroundColor: 'rgba(0, 170, 255, 0.1)', 
                        color: 'var(--secondary-color)',
                        border: '2px solid var(--secondary-color)',
                        fontFamily: "'VT323', monospace",
                        fontSize: "1.2rem",
                        mb: 3
                    }}
                    action={
                        <Button
                            onClick={login}
                            sx={{ 
                                fontFamily: "'VT323', monospace",
                                fontSize: "1.2rem",
                                bgcolor: "transparent",
                                color: "var(--success-color)",
                                border: "2px solid var(--success-color)",
                                borderRadius: 0,
                                "&:hover": {
                                    bgcolor: "var(--success-color)",
                                    color: "var(--bg-color)"
                                }
                            }}
                        >
                            [LOGIN]
                        </Button>
                    }
                >
                    You are browsing as a guest. Login to create posts and participate!
                </Alert>
            )}
            
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

            <Box sx={{ mb: 3, p: 2, border: "1px dashed var(--border-color)", bgcolor: "rgba(0, 0, 0, 0.2)" }}>
                <Typography sx={{ 
                    fontFamily: "'VT323', monospace", 
                    color: "var(--accent-color)",
                    fontSize: "1.2rem",
                    mb: 2
                }}>
                    &gt; SEARCH AND FILTER OPTIONS &lt;
                </Typography>
                
                <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} sm={6}>
                        <form onSubmit={handleSearchSubmit}>
                            <Box sx={{ display: 'flex', gap: 1 }}>
                                <TextField
                                    value={searchInput}
                                    onChange={handleSearchChange}
                                    placeholder="Search topics..."
                                    fullWidth
                                    size="small"
                                    InputProps={{
                                        sx: { 
                                            fontFamily: "'Courier Prime', monospace",
                                            color: "var(--text-color)",
                                            bgcolor: "rgba(0, 0, 0, 0.3)",
                                            '& .MuiOutlinedInput-notchedOutline': {
                                                borderColor: 'var(--border-color) !important',
                                            },
                                        }
                                    }}
                                />
                                <Button
                                    type="submit"
                                    sx={{ 
                                        fontFamily: "'VT323', monospace",
                                        fontSize: "1rem",
                                        bgcolor: "transparent",
                                        color: "var(--accent-color)",
                                        border: "1px solid var(--accent-color)",
                                        borderRadius: 0,
                                        "&:hover": {
                                            bgcolor: "rgba(0, 255, 255, 0.1)",
                                        }
                                    }}
                                >
                                    [SEARCH]
                                </Button>
                                {searchTerms && (
                                    <Button
                                        onClick={clearSearch}
                                        sx={{ 
                                            fontFamily: "'VT323', monospace",
                                            fontSize: "1rem",
                                            bgcolor: "transparent",
                                            color: "var(--danger-color)",
                                            border: "1px solid var(--danger-color)",
                                            borderRadius: 0,
                                            "&:hover": {
                                                bgcolor: "rgba(255, 0, 0, 0.1)",
                                            }
                                        }}
                                    >
                                        [CLEAR]
                                    </Button>
                                )}
                            </Box>
                        </form>
                    </Grid>
                    <Grid item xs={12} sm={6}>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                            <FormControl 
                                size="small"
                                sx={{ 
                                    flex: 1,
                                    '& .MuiOutlinedInput-root': {
                                        fontFamily: "'VT323', monospace",
                                        color: "var(--text-color)",
                                        bgcolor: "rgba(0, 0, 0, 0.3)",
                                        '& fieldset': {
                                            borderColor: "var(--border-color)",
                                        },
                                    },
                                    '& .MuiInputLabel-root': {
                                        fontFamily: "'VT323', monospace",
                                        color: "var(--text-color)",
                                    },
                                }}
                            >
                                <InputLabel id="sort-by-label">Sort By</InputLabel>
                                <Select
                                    labelId="sort-by-label"
                                    value={sortBy}
                                    onChange={handleSortByChange}
                                    label="Sort By"
                                >
                                    <MenuItem value="createdAt">Date</MenuItem>
                                    <MenuItem value="title">Title</MenuItem>
                                    <MenuItem value="rating">Rating</MenuItem>
                                </Select>
                            </FormControl>
                            
                            <FormControl 
                                size="small"
                                sx={{ 
                                    flex: 1,
                                    '& .MuiOutlinedInput-root': {
                                        fontFamily: "'VT323', monospace",
                                        color: "var(--text-color)",
                                        bgcolor: "rgba(0, 0, 0, 0.3)",
                                        '& fieldset': {
                                            borderColor: "var(--border-color)",
                                        },
                                    },
                                    '& .MuiInputLabel-root': {
                                        fontFamily: "'VT323', monospace",
                                        color: "var(--text-color)",
                                    },
                                }}
                            >
                                <InputLabel id="sort-order-label">Order</InputLabel>
                                <Select
                                    labelId="sort-order-label"
                                    value={sortOrder}
                                    onChange={handleSortOrderChange}
                                    label="Order"
                                >
                                    <MenuItem value="ASC">Ascending</MenuItem>
                                    <MenuItem value="DESC">Descending</MenuItem>
                                </Select>
                            </FormControl>
                        </Box>
                    </Grid>
                </Grid>
            </Box>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                {!loading && topics.length > 0 && (
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
                                <MenuItem value={10}>10</MenuItem>
                                <MenuItem value={15}>15</MenuItem>
                                <MenuItem value={20}>20</MenuItem>
                            </Select>
                        </FormControl>
                    </Box>
                )}
                
                <Button 
                    onClick={handleNewPost}
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
                    [NEW POST]
                </Button>
            </Box>
            
            {searchTerms && (
                <Box sx={{ mb: 2 }}>
                    <Typography sx={{ 
                        fontFamily: "'VT323', monospace", 
                        color: "var(--accent-color)",
                        fontSize: "1.1rem"
                    }}>
                        Searching for: "{searchTerms}"
                    </Typography>
                </Box>
            )}
            
            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4, mb: 4 }}>
                    <CircularProgress sx={{ color: 'var(--accent-color)' }} />
                </Box>
            ) : topics.length > 0 ? (
                <>
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
                        NO TOPICS FOUND <span className="blink">_</span>
                    </Typography>
                    <Typography 
                        sx={{ 
                            fontFamily: "'Courier Prime', monospace", 
                            color: "var(--accent-color)", 
                            mt: 1
                        }}
                    >
                        {searchTerms ? "Try a different search term or" : "Be the first to"} create a topic!
                    </Typography>
                </Box>
            )}
            
            {!loading && topics.length > 0 && (
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
        </Container>
    );
};

export default Home;
