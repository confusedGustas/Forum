import React from "react";
import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import {KeycloakProvider} from "./context/KeycloakContext";
import NewPost from "./pages/NewPost";
import Profile from "./pages/Profile";
import TopicPage from "./pages/TopicPage";
import Navbar from "./components/Navbar";
import Community from './pages/Community';
import NewCommunity from "./pages/NewCommunity";
import Home from './pages/Home';

const App = () => {
    return (
        <Router>
            <KeycloakProvider>
                <Navbar/>
                <Routes>
                    <Route path="/" element={<Navigate to="/communities" replace />}/>
                    <Route path="/communities/:communityId" element={<Home />}/>
                    <Route path="/communities" element={<Community/>}/>
                    <Route path="/new-community" element={<NewCommunity/>}/>
                    <Route path="/new-post/:communityId" element={<NewPost/>}/>
                    <Route path="/profile" element={<Profile/>}/>
                    <Route path="/topic/:topicId" element={<TopicPage/>}/>
                    <Route path="*" element={<Navigate to="/communities" replace />}/>
                </Routes>
            </KeycloakProvider>
        </Router>
    );
};

export default App;
