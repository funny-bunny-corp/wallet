import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Badge } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import apiService from '../services/apiService';

const PayoutsList = () => {
  const [payouts, setPayouts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadPayouts();
  }, []);

  const loadPayouts = async () => {
    try {
      setLoading(true);
      const data = await apiService.getAllPayouts();
      setPayouts(data);
    } catch (err) {
      setError('Failed to load payouts');
      console.error('Error loading payouts:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: amount.currency || 'USD'
    }).format(parseFloat(amount.value));
  };

  const formatAccountId = (accountId) => {
    return `${accountId.slice(0, 8)}...${accountId.slice(-4)}`;
  };

  if (loading) {
    return (
      <Container className="mt-4">
        <div className="loading-spinner"></div>
        <p className="text-center mt-3">Loading payouts...</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-4">
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <Row>
        <Col>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2>💰 All Payouts</h2>
            <Button 
              as={Link} 
              to="/create" 
              className="btn-primary-custom"
            >
              ➕ Create New Payout
            </Button>
          </div>
          
          {payouts.length === 0 ? (
            <Card className="custom-card">
              <Card.Body className="text-center py-5">
                <h4 className="text-muted">🏦 No payouts found</h4>
                <p className="text-muted">Start by creating your first payout!</p>
                <Button 
                  as={Link} 
                  to="/create" 
                  className="btn-secondary-custom"
                >
                  Create First Payout
                </Button>
              </Card.Body>
            </Card>
          ) : (
            <Row>
              {payouts.map((payout, index) => (
                <Col md={6} lg={4} key={index} className="mb-4">
                  <Card className="payout-item h-100">
                    <Card.Body>
                      <div className="d-flex justify-content-between align-items-start mb-3">
                        <div>
                          <div className="payout-amount">
                            {formatCurrency(payout.amount)}
                          </div>
                          <Badge bg="secondary" className="payout-currency">
                            {payout.amount.currency}
                          </Badge>
                        </div>
                        <div className="text-end">
                          <small className="text-muted">Account</small>
                          <div className="payout-account">
                            {formatAccountId(payout.seller.account)}
                          </div>
                        </div>
                      </div>
                      
                      {payout.note && (
                        <div className="mt-3">
                          <small className="text-muted">Note:</small>
                          <p className="payout-note mb-0">{payout.note}</p>
                        </div>
                      )}
                      
                      <div className="mt-3">
                        <Button 
                          as={Link} 
                          to={`/payout/${index}`} 
                          className="btn-secondary-custom btn-sm"
                        >
                          View Details
                        </Button>
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </Row>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default PayoutsList;