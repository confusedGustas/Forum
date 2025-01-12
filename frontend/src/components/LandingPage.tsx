import React from "react";
import LoginButton from "./LoginButton";
import RegisterButton from "./RegisterButton";
import UserDetails from "./UserDetails";

const LandingPage = () => {
    return (
        <div style={{ textAlign: "center", marginTop: "50px" }}>
            <LoginButton />
            <RegisterButton />
            <UserDetails />
        </div>
    );
};

export default LandingPage;
