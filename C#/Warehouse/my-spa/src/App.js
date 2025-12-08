import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import './App.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import LoginForm from './LoginForm.js';
import RegForm from './RegForm.js';
import Orders from './Orders.js';
import OrderForm from './OrderForm'; // Predpokladáme, že OrderForm je správne importovaný
import Users from './Users.js'; // Uistite sa, že cesta k súboru je správna
import MainContent from './MainContent.js';
import Header from './Header.js';
import Footer from './Footer.js';
import CategoriesPage from './CategoriesPage.js';
import ProductDetail from './ProductDetail.js';

function App() {
    const [showLoginForm, setShowLoginForm] = useState(false);

    const toggleLoginForm = () => {
        setShowLoginForm(!showLoginForm);
    };

    return (
        <Router>
            <div className="App">
                <Header onLoginClick={toggleLoginForm} />
                <div id="content_container">
                    <div id="main_content">
                        <Routes>
                            <Route path="/" element={<CategoriesPage />} />                 
                            <Route path="/products/category/:categoryId" element={<MainContent />} />
                            <Route path="/product/:productId" element={<ProductDetail />} />
                            <Route path="/login" element={<LoginForm />} />
                            <Route path="/register" element={<RegForm />} />
                            <Route path="/orders" element={<Orders />} />
                            <Route path="/users" element={<Users />} />
                            <Route path="/order-form" element={<OrderForm />} />
                        </Routes>
                    </div>
                </div>
                <Footer />
            </div>
        </Router>

    );
}

export default App;


