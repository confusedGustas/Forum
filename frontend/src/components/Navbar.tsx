import React, { useState } from "react";
import { AppBar, Toolbar, Typography, TextField, IconButton, Container, Box } from "@mui/material";
import { Search as SearchIcon } from "@mui/icons-material";
import "./css/Navbar.css"

const Navbar = () => {
    const [searchQuery, setSearchQuery] = useState("");

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchQuery(e.target.value);
    };

    const handleSearch = () => {
        console.log("Search Query:", searchQuery);
    };

    return (
        <AppBar position="sticky" className="navbar">
            <Toolbar>
                <Container maxWidth="lg">
                    <Box className="navbar-search-container">
                        <Typography className="brand-name" variant={"h5"}>
                            Forum
                        </Typography>
                        <Box sx={{ flexGrow: 1 }} />
                        <TextField
                            value={searchQuery}
                            onChange={handleSearchChange}
                            label="Search"
                            variant="outlined"
                            size="small"
                            className="search-bar"
                        />
                        <IconButton onClick={handleSearch} className="search-button">
                            <SearchIcon />
                        </IconButton>
                    </Box>
                </Container>
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;
