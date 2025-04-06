import React, { useState, useContext, useCallback, useEffect } from "react";
import { Card, CardContent, Typography, IconButton, Box, Link, Button } from "@mui/material";
import { ArrowUpward, ArrowDownward, AccountCircle, AttachFile, Comment } from "@mui/icons-material";
import { TopicResponseDto } from "../lib/topicService";
import apiProxy from "../lib/apiProxy";
import config from "../lib/config";
import { KeycloakContext } from "../context/KeycloakContext";
import { Link as RouterLink } from "react-router-dom";

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
        <Card sx={{
            mb: 4,
            backgroundColor: "var(--bg-color)",
            border: "1px solid var(--border-color)",
            position: "relative",
            "&:hover": {
                borderColor: "var(--accent-color)"
            }
        }}>
            {error && (
                <div style={{
                    position: "absolute",
                    top: 0,
                    left: 0,
                    right: 0,
                    padding: "8px",
                    backgroundColor: "var(--error-color)",
                    color: "white",
                    fontSize: "0.8em",
                    zIndex: 1
                }}>
                    {error}
                </div>
            )}
            <CardContent>
                <Typography 
                    variant="h6" 
                    component={RouterLink} 
                    to={`/topic/${id}`}
                    sx={{ 
                        color: "var(--accent-color)",
                        textDecoration: "none",
                        fontFamily: "'VT323', monospace",
                        fontSize: "1.5rem",
                        "&:hover": {
                            textDecoration: "underline"
                        }
                    }}
                >
                    {title}
                </Typography>
                
                <Typography sx={{ 
                    color: 'var(--text-secondary)', 
                    mb: 1, 
                    fontSize: "0.8rem",
                    display: "flex",
                    alignItems: "center",
                    gap: 0.5
                }}>
                    <AccountCircle fontSize="small" /> 
                    {authorName || (author && author.name) || "Anonymous"} | {formatDate(createdAt)}
                    {hasFiles && <AttachFile fontSize="small" />}
                </Typography>
                
                <Typography sx={{ 
                    color: 'var(--text-color)', 
                    whiteSpace: 'pre-wrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    display: '-webkit-box',
                    WebkitLineClamp: 3,
                    WebkitBoxOrient: 'vertical',
                    mb: 2,
                    fontFamily: "monospace"
                }}>
                    {content}
                </Typography>
                
                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
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
                    
                    <Button
                        component={RouterLink}
                        to={`/topic/${id}`}
                        startIcon={<Comment />}
                        size="small"
                        sx={{
                            fontFamily: "'VT323', monospace",
                            color: "var(--text-color)",
                            '&:hover': {
                                color: "var(--accent-color)",
                            }
                        }}
                    >
                        View Discussion
                    </Button>
                </Box>
            </CardContent>
        </Card>
    );
};

export default Topic;
