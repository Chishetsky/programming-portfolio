import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Ak potrebujete presmerovať po prihlásení

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate(); // Na navigáciu po úspešnom prihlásení

    const handleSubmit = async (event) => {
        event.preventDefault();

        // Kontrola, či boli všetky polia vyplnené
        if (!username.trim() || !password.trim()) {
            setError("Nezadali ste všetky povinné údaje!");
            return; // Predčasné ukončenie, ak údaje chýbajú
        }

        try {
            const response = await fetch('http://localhost:5000/Auth/Login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || "Nesprávne meno alebo heslo!");
            }

            console.log('Prihlásenie úspešné:', data.user);
            setError('');
            // Reset formulára alebo presmerovanie na inú stránku
            navigate('/'); // Zmeniť na cieľovú URL po prihlásení
        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="login-form">
            {error && <div className="error">{error}</div>}
            <div>
                <label htmlFor="username">Meno:<span className="required">*</span></label>
                <input
                    id="username"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div>
                <label htmlFor="password">Heslo:<span className="required">*</span></label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <button type="submit">Odoslať</button>
            <div className="account-query">
                <Link to="/register">Nemáte ešte účet?</Link>
            </div>
        </form>
    );
};

export default LoginForm;
