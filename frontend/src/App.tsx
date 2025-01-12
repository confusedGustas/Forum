import React from "react";
import { KeycloakProvider } from "./context/KeycloakContext";
import LandingPage from "./components/LandingPage";

const App = () => {
    return (
        <KeycloakProvider>
            <LandingPage />
        </KeycloakProvider>
    );
};

export default App;
