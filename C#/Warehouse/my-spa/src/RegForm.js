import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const RegForm = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate(); // Na navigáciu po úspešnej registrácii

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!username.trim() || !email.trim() || !password.trim()) {
            setError("Nezadali ste všetky povinné údaje!");
            return;
        }

        try {
            const response = await fetch('http://localhost:5000/UserController/AddUser', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username,
                    password,
                    email,
                    role: 'User' // Prednastavená rola pre nových užívateľov
                })
            });

            if (!response.ok) {
                throw new Error("Registrácia zlyhala!");
            }

            alert('Registrácia úspešná!');
            setError('');
            navigate('/login'); // Presmerovanie na prihlásenie alebo inú stránku
        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
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
                <label htmlFor="email">E-mail:<span className="required">*</span></label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
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
            <button type="submit">Registrovať</button>
        </form>
    );
};

export default RegForm;
