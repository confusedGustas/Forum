import React from "react";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import {KeycloakProvider} from "./context/KeycloakContext";
import LandingPage from "./components/LandingPage";
import Home from "./pages/Home";
import Navbar from "./components/Navbar";
import "./components/css/Navbar.css";

const App = () => {
    return (
        <KeycloakProvider>
            <Navbar/>
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage/>}/>
                    <Route path="/home" element={<Home/>}/>
                </Routes>
            </Router>
        </KeycloakProvider>
    );
};

export default App;
