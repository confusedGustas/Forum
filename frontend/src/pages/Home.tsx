import React from "react";
import {Button, Container} from "@mui/material";
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
        <Container sx={{display: 'flex', flexDirection: 'column'}}>
            <Button sx={{  mt: 2, alignSelf: 'flex-end'}} variant="contained">Add new post</Button>
            <Topic topic={dummyTopic} />
        </Container>
    );
};

export default Home;
