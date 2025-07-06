import React from 'react';
import { Navbar, Nav, Container } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Header = () => {
  return (
    <Navbar className="header" expand="lg">
      <Container>
        <Navbar.Brand as={Link} to="/">
          <h1>💰 Payouts Manager</h1>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <Nav.Link as={Link} to="/">
              📊 All Payouts
            </Nav.Link>
            <Nav.Link as={Link} to="/create">
              ➕ Create Payout
            </Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;