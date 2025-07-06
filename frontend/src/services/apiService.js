import axios from 'axios';

// Mock data for demonstration
const mockPayouts = [
  {
    id: 1,
    amount: {
      currency: 'USD',
      value: '125.50'
    },
    seller: {
      account: '28c80b82-3917-11ee-b450-325096b39f47'
    },
    note: 'Monthly commission payment',
    createdAt: new Date('2024-01-15T10:30:00Z').toISOString(),
    status: 'processed'
  },
  {
    id: 2,
    amount: {
      currency: 'EUR',
      value: '89.99'
    },
    seller: {
      account: 'a1b2c3d4-5678-90ab-cdef-123456789012'
    },
    note: 'Product sale commission',
    createdAt: new Date('2024-01-14T14:15:00Z').toISOString(),
    status: 'processed'
  },
  {
    id: 3,
    amount: {
      currency: 'USD',
      value: '250.00'
    },
    seller: {
      account: 'f9e8d7c6-b5a4-3210-9876-543210fedcba'
    },
    note: 'Thanks for your excellent service!',
    createdAt: new Date('2024-01-13T09:45:00Z').toISOString(),
    status: 'processed'
  },
  {
    id: 4,
    amount: {
      currency: 'GBP',
      value: '75.25'
    },
    seller: {
      account: '12345678-90ab-cdef-1234-567890abcdef'
    },
    note: 'Weekly bonus payment',
    createdAt: new Date('2024-01-12T16:20:00Z').toISOString(),
    status: 'processed'
  }
];

// Base URL for the API - in a real application, this would be your backend URL
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add request interceptor for logging
apiClient.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('API Response Error:', error);
    if (error.response?.status === 401) {
      // Handle unauthorized access
      console.error('Unauthorized access - redirecting to login');
    }
    return Promise.reject(error);
  }
);

// API Service class
class ApiService {
  // Get all payouts
  async getAllPayouts() {
    try {
      // In a real application, this would be an actual API call
      // const response = await apiClient.get('/payouts');
      // return response.data;
      
      // For demonstration, return mock data after a delay
      await new Promise(resolve => setTimeout(resolve, 1000));
      return mockPayouts;
    } catch (error) {
      console.error('Error fetching payouts:', error);
      throw new Error('Failed to fetch payouts');
    }
  }

  // Get a specific payout by ID
  async getPayoutById(id) {
    try {
      // In a real application, this would be an actual API call
      // const response = await apiClient.get(`/payouts/${id}`);
      // return response.data;
      
      // For demonstration, find in mock data
      await new Promise(resolve => setTimeout(resolve, 500));
      const payout = mockPayouts.find(p => p.id === parseInt(id));
      if (!payout) {
        throw new Error('Payout not found');
      }
      return payout;
    } catch (error) {
      console.error('Error fetching payout:', error);
      throw new Error('Failed to fetch payout details');
    }
  }

  // Create a new payout
  async createPayout(payoutData) {
    try {
      // In a real application, this would be an actual API call
      // const response = await apiClient.post('/payouts', payoutData);
      // return response.data;
      
      // For demonstration, simulate creation
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Validate the data structure matches OpenAPI schema
      this.validatePayoutData(payoutData);
      
      const newPayout = {
        id: mockPayouts.length + 1,
        ...payoutData,
        createdAt: new Date().toISOString(),
        status: 'processed'
      };
      
      mockPayouts.push(newPayout);
      return newPayout;
    } catch (error) {
      console.error('Error creating payout:', error);
      throw new Error('Failed to create payout');
    }
  }

  // Update a payout
  async updatePayout(id, payoutData) {
    try {
      // In a real application, this would be an actual API call
      // const response = await apiClient.put(`/payouts/${id}`, payoutData);
      // return response.data;
      
      // For demonstration, simulate update
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const index = mockPayouts.findIndex(p => p.id === parseInt(id));
      if (index === -1) {
        throw new Error('Payout not found');
      }
      
      mockPayouts[index] = { ...mockPayouts[index], ...payoutData };
      return mockPayouts[index];
    } catch (error) {
      console.error('Error updating payout:', error);
      throw new Error('Failed to update payout');
    }
  }

  // Delete a payout
  async deletePayout(id) {
    try {
      // In a real application, this would be an actual API call
      // await apiClient.delete(`/payouts/${id}`);
      
      // For demonstration, simulate deletion
      await new Promise(resolve => setTimeout(resolve, 500));
      
      const index = mockPayouts.findIndex(p => p.id === parseInt(id));
      if (index === -1) {
        throw new Error('Payout not found');
      }
      
      mockPayouts.splice(index, 1);
      return true;
    } catch (error) {
      console.error('Error deleting payout:', error);
      throw new Error('Failed to delete payout');
    }
  }

  // Validate payout data against OpenAPI schema
  validatePayoutData(payoutData) {
    if (!payoutData.amount || !payoutData.seller) {
      throw new Error('Invalid payout data: amount and seller are required');
    }
    
    if (!payoutData.amount.currency || !payoutData.amount.value) {
      throw new Error('Invalid amount data: currency and value are required');
    }
    
    if (!payoutData.seller.account) {
      throw new Error('Invalid seller data: account is required');
    }
    
    // Additional validations can be added here
    const value = parseFloat(payoutData.amount.value);
    if (isNaN(value) || value <= 0) {
      throw new Error('Invalid amount value: must be a positive number');
    }
  }

  // Get payout statistics (bonus feature)
  async getPayoutStatistics() {
    try {
      await new Promise(resolve => setTimeout(resolve, 800));
      
      // Simple currency conversion rates to USD for demonstration
      const exchangeRates = {
        'USD': 1.0,
        'EUR': 1.09,
        'GBP': 1.27,
        'CAD': 0.74,
        'AUD': 0.67
      };
      
      const stats = {
        totalPayouts: mockPayouts.length,
        totalAmount: mockPayouts.reduce((sum, payout) => {
          // Convert all to USD using exchange rates
          const value = parseFloat(payout.amount.value);
          const rate = exchangeRates[payout.amount.currency] || 1.0;
          return sum + (value * rate);
        }, 0),
        currencies: [...new Set(mockPayouts.map(p => p.amount.currency))],
        lastPayout: mockPayouts.length > 0 ? mockPayouts[mockPayouts.length - 1] : null
      };
      
      return stats;
    } catch (error) {
      console.error('Error fetching statistics:', error);
      throw new Error('Failed to fetch payout statistics');
    }
  }
}

// Export a singleton instance
const apiService = new ApiService();
export default apiService;