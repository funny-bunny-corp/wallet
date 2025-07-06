import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import apiService from '../services/apiService';

const CreatePayout = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    amount: {
      value: '',
      currency: 'USD'
    },
    seller: {
      account: ''
    },
    note: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const currencies = ['USD', 'EUR', 'GBP', 'JPY', 'CAD', 'AUD', 'CHF', 'CNY'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    if (name === 'currency') {
      setFormData(prev => ({
        ...prev,
        amount: {
          ...prev.amount,
          currency: value
        }
      }));
    } else if (name === 'value') {
      setFormData(prev => ({
        ...prev,
        amount: {
          ...prev.amount,
          value: value
        }
      }));
    } else if (name === 'account') {
      setFormData(prev => ({
        ...prev,
        seller: {
          ...prev.seller,
          account: value
        }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const validateForm = () => {
    if (!formData.amount.value || parseFloat(formData.amount.value) <= 0) {
      return 'Please enter a valid amount greater than 0';
    }
    if (!formData.seller.account.trim()) {
      return 'Please enter a seller account ID';
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const payoutData = {
        ...formData,
        amount: {
          ...formData.amount,
          value: parseFloat(formData.amount.value).toFixed(2)
        }
      };

      await apiService.createPayout(payoutData);
      setSuccess(true);
      
      // Reset form
      setFormData({
        amount: {
          value: '',
          currency: 'USD'
        },
        seller: {
          account: ''
        },
        note: ''
      });

      // Navigate back to list after a short delay
      setTimeout(() => {
        navigate('/');
      }, 2000);
    } catch (err) {
      setError('Failed to create payout. Please try again.');
      console.error('Error creating payout:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="mt-4">
      <Row className="justify-content-center">
        <Col md={8} lg={6}>
          <Card className="custom-card">
            <Card.Header className="custom-card-header">
              <h3 className="mb-0">➕ Create New Payout</h3>
            </Card.Header>
            <Card.Body>
              {error && (
                <Alert variant="danger" className="mb-4">
                  {error}
                </Alert>
              )}
              
              {success && (
                <Alert variant="success" className="mb-4">
                  🎉 Payout created successfully! Redirecting to payouts list...
                </Alert>
              )}

              <Form onSubmit={handleSubmit}>
                <Row>
                  <Col md={8}>
                    <Form.Group className="mb-3">
                      <Form.Label>💰 Amount</Form.Label>
                      <Form.Control
                        type="number"
                        step="0.01"
                        min="0"
                        name="value"
                        value={formData.amount.value}
                        onChange={handleChange}
                        placeholder="Enter amount"
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>💱 Currency</Form.Label>
                      <Form.Select
                        name="currency"
                        value={formData.amount.currency}
                        onChange={handleChange}
                        required
                      >
                        {currencies.map(currency => (
                          <option key={currency} value={currency}>
                            {currency}
                          </option>
                        ))}
                      </Form.Select>
                    </Form.Group>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Label>🏦 Seller Account ID</Form.Label>
                  <Form.Control
                    type="text"
                    name="account"
                    value={formData.seller.account}
                    onChange={handleChange}
                    placeholder="Enter seller account ID (e.g., 28c80b82-3917-11ee-b450-325096b39f47)"
                    required
                  />
                  <Form.Text className="text-muted">
                    Enter the unique account identifier for the seller
                  </Form.Text>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label>📝 Note (Optional)</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="note"
                    value={formData.note}
                    onChange={handleChange}
                    placeholder="Add a note about this payout (optional)"
                  />
                </Form.Group>

                <div className="d-grid gap-2 d-md-flex justify-content-md-end">
                  <Button 
                    variant="outline-secondary" 
                    onClick={() => navigate('/')}
                    disabled={loading}
                  >
                    Cancel
                  </Button>
                  <Button 
                    type="submit" 
                    className="btn-primary-custom"
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" />
                        Creating...
                      </>
                    ) : (
                      '💸 Create Payout'
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default CreatePayout;