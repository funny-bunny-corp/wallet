import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Badge, Alert } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import apiService from '../services/apiService';

const PayoutDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [payout, setPayout] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadPayout();
  }, [id]);

  const loadPayout = async () => {
    try {
      setLoading(true);
      const data = await apiService.getPayoutById(id);
      setPayout(data);
    } catch (err) {
      setError('Failed to load payout details');
      console.error('Error loading payout:', err);
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

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <Container className="mt-4">
        <div className="loading-spinner"></div>
        <p className="text-center mt-3">Loading payout details...</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-4">
        <Alert variant="danger" className="mb-4">
          {error}
        </Alert>
        <Button 
          onClick={() => navigate('/')}
          className="btn-secondary-custom"
        >
          ← Back to Payouts
        </Button>
      </Container>
    );
  }

  if (!payout) {
    return (
      <Container className="mt-4">
        <Alert variant="warning" className="mb-4">
          Payout not found
        </Alert>
        <Button 
          onClick={() => navigate('/')}
          className="btn-secondary-custom"
        >
          ← Back to Payouts
        </Button>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <Row className="justify-content-center">
        <Col md={8} lg={6}>
          <Card className="custom-card">
            <Card.Header className="custom-card-header">
              <div className="d-flex justify-content-between align-items-center">
                <h3 className="mb-0">💰 Payout Details</h3>
                <Button 
                  variant="outline-light" 
                  size="sm" 
                  onClick={() => navigate('/')}
                >
                  ← Back
                </Button>
              </div>
            </Card.Header>
            <Card.Body>
              <Row>
                <Col md={6}>
                  <div className="mb-4">
                    <h5 className="text-muted mb-2">💰 Amount</h5>
                    <div className="payout-amount">
                      {formatCurrency(payout.amount)}
                    </div>
                    <Badge bg="secondary" className="payout-currency mt-2">
                      {payout.amount.currency}
                    </Badge>
                  </div>
                </Col>
                <Col md={6}>
                  <div className="mb-4">
                    <h5 className="text-muted mb-2">🏦 Seller Account</h5>
                    <div className="payout-account">
                      {payout.seller.account}
                    </div>
                  </div>
                </Col>
              </Row>

              {payout.note && (
                <div className="mb-4">
                  <h5 className="text-muted mb-2">📝 Note</h5>
                  <Card className="bg-light">
                    <Card.Body>
                      <p className="payout-note mb-0">{payout.note}</p>
                    </Card.Body>
                  </Card>
                </div>
              )}

              <div className="mb-4">
                <h5 className="text-muted mb-2">📋 Payout Information</h5>
                <Row>
                  <Col md={6}>
                    <div className="mb-3">
                      <strong>Status:</strong>
                      <Badge bg="success" className="ms-2">
                        Processed
                      </Badge>
                    </div>
                  </Col>
                  <Col md={6}>
                    <div className="mb-3">
                      <strong>Created:</strong>
                      <span className="ms-2">
                        {payout.createdAt ? formatDate(payout.createdAt) : 'N/A'}
                      </span>
                    </div>
                  </Col>
                </Row>
              </div>

              <div className="d-grid gap-2 d-md-flex justify-content-md-end">
                <Button 
                  variant="outline-secondary" 
                  onClick={() => navigate('/')}
                >
                  ← Back to List
                </Button>
                <Button 
                  onClick={() => navigate('/create')}
                  className="btn-primary-custom"
                >
                  ➕ Create New Payout
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default PayoutDetail;