import React, { useState } from "react";
import { Card, CardContent, Typography, IconButton } from "@mui/material";
import { ArrowUpward, ArrowDownward, AccountCircle } from "@mui/icons-material";

const Topic = ({ topic }) => {
    const { title, content, createdAt, author, rating } = topic;
    const [votes, setVotes] = useState(rating || 0);

    const handleUpvote = () => setVotes(votes + 1);
    const handleDownvote = () => setVotes(votes - 1);

    return (
        <Card sx={{ mb: 2, mt: 2, p: 2 }}>
            <CardContent>
                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <AccountCircle color="action" />
                    <Typography variant="body2">
                        {author?.name || "Anonymous"} • {new Date(createdAt).toLocaleString()}
                    </Typography>
                </div>

                <Typography variant="h6" sx={{ mt: 1 }}>
                    {title}
                </Typography>
                <Typography variant="body1" sx={{ my: 1 }}>
                    {content}
                </Typography>

                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <IconButton onClick={handleUpvote} color="primary">
                        <ArrowUpward />
                    </IconButton>
                    <Typography variant="body2">{votes}</Typography>
                    <IconButton onClick={handleDownvote} color="secondary">
                        <ArrowDownward />
                    </IconButton>
                </div>
            </CardContent>
        </Card>
    );
};

export default Topic;
