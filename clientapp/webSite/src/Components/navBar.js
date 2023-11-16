// CustomNavbar.js

import React, { useEffect, useState } from 'react';
import { Navbar, Nav, Form, Button } from 'react-bootstrap';

const CustomNavbar = ({ loggedIn, setLoggedin }) => {

    const [user, setUser] = useState(null);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    useEffect(() => {
        const user = JSON.parse(localStorage.getItem("user"))
    
        // If the token/email does not exist, mark the user as logged out
        if (user && user.token) {
            setUser({'email':user.email, 'token':user.token})
            setLoggedin(true)
        }
    }, [])

    const handleEmailChange = (e) => {
        setEmail(e.target.value);
    };

    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
    };

    const handleLogout = () => {
        setUser(null)
        setEmail('')
        setPassword('')
        setLoggedin(false)
        localStorage.removeItem("user")
    }

    const handleLogin = () => {
        setUser({'email':email, 'token':'dummy-token'})
        setLoggedin(true)
        localStorage.setItem("user", JSON.stringify({email, token: 'dummy-token'}))
    }

    return (
        <Navbar bg="dark" variant="dark">
        <Navbar.Brand>SDLE</Navbar.Brand>
        <Nav className="mr-auto">
            <Nav.Link href="/">Shopping Lists</Nav.Link>
        </Nav>
        <Form inline='True'>
            {loggedIn ? (
            <>
                <Navbar.Text>{user.email}</Navbar.Text>
                <Button variant="primary" onClick={handleLogout} className="ml-2">
                    Logout
                </Button>
            </>
            )  : (
                <>
                <div className="d-flex">
                <Form.Control
                    type="text"
                    placeholder="Email"
                    value={email}
                    onChange={handleEmailChange}
                    className="mr-2"
                />
                <Form.Control
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={handlePasswordChange}
                    className="mr-2"
                />
                <Button variant="primary" onClick={handleLogin}>
                    Login
                </Button>
                </div>
            </>
            )}
        </Form>
        </Navbar>
    );
};

export default CustomNavbar;
