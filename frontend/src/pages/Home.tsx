import React from "react";
import { Container, Typography } from "@mui/material";
import Topic from "../components/Topic";

const dummyTopic = {
    title: "My First Topic",
    content: "This is a sample post for the forum.",
    createdAt: new Date().toISOString(),
    author: { name: "John Doe" },
    rating: 5,
};

const Home = () => {
    return (
        <Container>
            <Topic topic={dummyTopic} />
        </Container>
    );
};

export default Home;
