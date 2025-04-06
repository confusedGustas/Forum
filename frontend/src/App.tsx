import React from "react";
import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import {KeycloakProvider} from "./context/KeycloakContext";
import Home from "./pages/Home";
import NewPost from "./pages/NewPost";
import Profile from "./pages/Profile";
import TopicPage from "./pages/TopicPage";
import Navbar from "./components/Navbar";

const App = () => {
    return (
        <Router>
            <KeycloakProvider>
                <Navbar/>
                <Routes>
                    <Route path="/" element={<Navigate to="/home" replace />}/>
                    <Route path="/home" element={<Home/>}/>
                    <Route path="/new-post" element={<NewPost/>}/>
                    <Route path="/profile" element={<Profile/>}/>
                    <Route path="/topic/:topicId" element={<TopicPage/>}/>
                    <Route path="*" element={<Navigate to="/home" replace />}/>
                </Routes>
            </KeycloakProvider>
        </Router>
    );
};

export default App;
