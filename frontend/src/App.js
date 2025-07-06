import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
import Header from './components/Header';
import PayoutsList from './components/PayoutsList';
import CreatePayout from './components/CreatePayout';
import PayoutDetail from './components/PayoutDetail';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <div className="container-fluid">
          <Routes>
            <Route path="/" element={<PayoutsList />} />
            <Route path="/create" element={<CreatePayout />} />
            <Route path="/payout/:id" element={<PayoutDetail />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
