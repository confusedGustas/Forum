import React, { useState, useContext, useCallback, useEffect } from "react";
import { Card, CardContent, Typography, IconButton, Box, Link } from "@mui/material";
import { ArrowUpward, ArrowDownward, AccountCircle, AttachFile } from "@mui/icons-material";
import { TopicResponseDto } from "../lib/topicService";
import apiProxy from "../lib/apiProxy";
import config from "../lib/config";
import { KeycloakContext } from "../context/KeycloakContext";

interface TopicProps {
    topic: TopicResponseDto;
}

const Topic: React.FC<TopicProps> = ({ topic }) => {
    const { id, title, content, createdAt, author, rating, userRating, files, authorId, authorName } = topic;
    const [votes, setVotes] = useState(rating || 0);
    const [currentUserRating, setCurrentUserRating] = useState(userRating || 0);
    const [isUpdating, setIsUpdating] = useState(false);
    const [error, setError] = useState("");
    const { authenticated, login } = useContext(KeycloakContext);

    const hasFiles = files && (
        Array.isArray(files) ? files.length > 0 : false
    );

    const formatDate = (dateValue: string | number[]) => {
        if (Array.isArray(dateValue)) {
            const [year, month, day, hour, minute] = dateValue;
            return new Date(year, month - 1, day, hour, minute).toLocaleString();
        } else {
            return new Date(dateValue).toLocaleString();
        }
    };

    const handleRatingChange = useCallback(async (newRating: number) => {
        if (!isUpdating) {
            setIsUpdating(true);
            
            try {
                const effectiveRating = newRating === currentUserRating ? 0 : newRating;
                
                const response = await apiProxy.ratings.rateTopic(id.toString(), effectiveRating);
                
                if (response && response.data) {
                    setVotes(response.data.rating || 0);
                    setCurrentUserRating(response.data.userRating || 0);
                }
                
                setError("");
            } catch (error: any) {
                if (error.response?.status === 401) {
                    setError("Authentication required: Please log in to vote");
                    setTimeout(() => {
                        login();
                    }, 1500);
                } else {
                    setError("Failed to update rating: " + (error.message || "Unknown error"));
                    setTimeout(() => setError(""), 3000);
                }
            } finally {
                setIsUpdating(false);
            }
        }
    }, [id, isUpdating, login, currentUserRating]);

    const handleUpvote = async () => {
        if (!authenticated) {
            setError("Please login to vote");
            setTimeout(() => {
                login();
            }, 1500);
            return;
        }
        
        try {
            await handleRatingChange(1);
        } catch (error) {
        }
    };

    const handleDownvote = async () => {
        if (!authenticated) {
            setError("Please login to vote");
            setTimeout(() => {
                login();
            }, 1500);
            return;
        }
        
        try {
            await handleRatingChange(-1);
        } catch (error) {
        }
    };

    return (
        <Card className="retro-card" sx={{ mb: 2, mt: 2, p: 2 }}>
            <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <AccountCircle sx={{ color: "var(--text-color)" }} />
                    <Typography variant="body2" sx={{ fontFamily: "'VT323', monospace", fontSize: "1.2rem", color: "var(--accent-color)" }}>
                        {author?.name || authorName || "User " + authorId?.substring(0, 8) || "Anonymous"} • {formatDate(createdAt)}
                    </Typography>
                </Box>

                <Typography variant="h6" sx={{ 
                    mt: 1, 
                    fontFamily: "'Courier Prime', monospace", 
                    fontSize: "1.3rem",
                    fontWeight: "bold",
                    color: "var(--text-color)",
                    pb: 1,
                    borderBottom: "1px solid var(--border-color)"
                }}>
                    {title}
                </Typography>
                <Typography variant="body1" sx={{ 
                    my: 1, 
                    fontFamily: "'Courier Prime', monospace",
                    color: "var(--text-color)",
                    borderLeft: "2px solid var(--border-color)",
                    pl: 2,
                    py: 1
                }}>
                    {content}
                </Typography>
                
                {hasFiles && (
                    <Box sx={{ 
                        mt: 2, 
                        p: 1, 
                        borderTop: "1px dashed var(--border-color)",
                        display: "flex",
                        flexDirection: "column",
                        gap: 1
                    }}>
                        <Typography variant="body2" sx={{ fontFamily: "'VT323', monospace", color: "var(--accent-color)" }}>
                            Attachments:
                        </Typography>
                        
                        <Box sx={{ 
                            display: "flex",
                            flexWrap: "wrap",
                            gap: 1
                        }}>
                            {Array.isArray(files) && files.map((file, index) => {
                                const fileExtension = file.minioObjectName?.split('.').pop()?.toUpperCase() || 'FILE';
                                const displayUrl = `http://localhost:9000/forum-bucket/${file.minioObjectName}`;
                                
                                return (
                                    <Link 
                                        key={index}
                                        href={displayUrl}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        sx={{
                                            display: "flex",
                                            alignItems: "center",
                                            gap: 0.5,
                                            color: "var(--accent-color)",
                                            textDecoration: "none",
                                            border: "1px solid var(--border-color)",
                                            borderRadius: "4px",
                                            p: 0.75,
                                            "&:hover": {
                                                backgroundColor: "rgba(255, 255, 255, 0.1)",
                                                textDecoration: "underline"
                                            }
                                        }}
                                    >
                                        <AttachFile fontSize="small" />
                                        <Typography 
                                            variant="body2" 
                                            sx={{ 
                                                fontFamily: "'Courier Prime', monospace",
                                                fontSize: "0.9rem",
                                                fontWeight: "bold"
                                            }}
                                        >
                                            {fileExtension}
                                        </Typography>
                                    </Link>
                                );
                            })}
                        </Box>
                    </Box>
                )}
                
                <Box sx={{ 
                    display: "flex", 
                    alignItems: "center",
                    justifyContent: "space-between",
                    mt: 2,
                    pt: 1, 
                    borderTop: "1px solid var(--border-color)"
                }}>
                    <Box sx={{ display: "flex", alignItems: "center" }}>
                        <IconButton 
                            onClick={handleUpvote}
                            color={currentUserRating === 1 ? "primary" : "default"}
                            disabled={isUpdating}
                            sx={{ 
                                color: currentUserRating === 1 ? "var(--success-color)" : "var(--text-color)",
                            }}
                        >
                            <ArrowUpward />
                        </IconButton>
                        <Typography sx={{ 
                            mx: 1, 
                            fontFamily: "'VT323', monospace",
                            fontSize: "1.2rem",
                            color: votes > 0 
                                ? "var(--success-color)" 
                                : votes < 0 
                                    ? "var(--error-color)" 
                                    : "var(--text-color)"
                        }}>
                            {votes}
                        </Typography>
                        <IconButton 
                            onClick={handleDownvote}
                            color={currentUserRating === -1 ? "secondary" : "default"}
                            disabled={isUpdating}
                            sx={{ 
                                color: currentUserRating === -1 ? "var(--error-color)" : "var(--text-color)",
                            }}
                        >
                            <ArrowDownward />
                        </IconButton>
                    </Box>
                    
                    {error && (
                        <Typography sx={{ 
                            color: "var(--error-color)",
                            fontFamily: "'Courier Prime', monospace",
                            fontSize: "0.9rem"
                        }}>
                            {error}
                        </Typography>
                    )}
                </Box>
            </CardContent>
        </Card>
    );
};

export default Topic;
