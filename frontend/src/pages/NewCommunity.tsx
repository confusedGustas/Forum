import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, Container, TextField, Typography } from "@mui/material";
import { KeycloakContext } from "../context/KeycloakContext";
import apiProxy from "../lib/apiProxy";

const NewCommunity = () => {
    const navigate = useNavigate();
    const { authenticated, login } = useContext(KeycloakContext);

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();

        if (!authenticated) {
            setError("You must be logged in to create a community");
            setTimeout(() => {
                login();
            }, 1500);
            return;
        }

        const formData = new FormData();
        formData.append(
            "data",
            new Blob([JSON.stringify({ title: name, description: description })], { type: "application/json" })
        );

        try {
            await apiProxy.communities.create(formData);
            navigate("/");
        } catch (err: any) {
            setError("Failed to create community: " + (err.message || "Unknown error"));
        }
    };

    return (
        <Container sx={{ mt: 4 }}>
            <Typography variant="h4" sx={{ mb: 3 }}>
                Create New Community
            </Typography>

            {error && (
                <Box sx={{ color: "red", mb: 2 }}>
                    {error}
                </Box>
            )}

            <form onSubmit={handleSubmit}>
                <TextField
                    label="Community Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    fullWidth
                    required
                    sx={{ mb: 2 }}
                />
                <TextField
                    label="Description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    fullWidth
                    multiline
                    rows={4}
                    sx={{ mb: 2 }}
                />
                <Button type="submit" variant="contained" color="primary">
                    Create Community
                </Button>
            </form>
        </Container>
    );
};

export default NewCommunity;
