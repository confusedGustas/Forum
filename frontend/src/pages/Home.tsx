import React, { useEffect, useState } from "react";
import axios from "axios";
import { Button, Container } from "@mui/material";
import Topic from "../components/Topic";

const Home = () => {
    const [topics, setTopics] = useState([]);

    useEffect(() => {
        fetchTopics();
    }, []);

    const fetchTopics = async () => {
        try {
            const response = await axios.get("http://localhost:8080/api/v1/search/topics?offset=0&limit=10", {
                params: {
                    limit: 10,
                    offset: 0
                }
            });
            setTopics(response.data.items);
        } catch (error) {
            console.error("Error fetching topics:", error);
        }
    };

    return (
        <Container sx={{ display: 'flex', flexDirection: 'column' }}>
            <Button sx={{ mt: 2, alignSelf: 'flex-end' }} variant="contained">
                Add new post
            </Button>
            {topics.map((topic) => (
                <Topic key={topic.id} topic={topic} />
            ))}
        </Container>
    );
};

export default Home;
