import React, { useState } from 'react';
const OrderForm = () => {
    const [order, setOrder] = useState({
        meno: '',
        priezvisko: '',
        email: '',
        adresa1: '', // Ulica a číslo domu
        adresa2: '', // PSČ a mesto
        adresa3: ''  // Štát
    });

    const [error, setError] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();

        // Kontrola, či boli všetky povinné polia vyplnené
        if (!order.meno || !order.priezvisko || !order.email || !order.adresa1 || !order.adresa2 || !order.adresa3) {
            setError("Nezadali ste všetky povinné údaje!");
            return; // Zastaví vykonávanie funkcie, ak nie sú vyplnené všetky polia
        }

        setError(''); // Vymaže chybovú správu, ak boli všetky polia správne vyplnené
        console.log('Objednávka odoslaná:', order);
        // Implementácia odosielania dát alebo presmerovanie
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setOrder(prevOrder => ({
            ...prevOrder,
            [name]: value
        }));
    };

    return (
        <form onSubmit={handleSubmit} className="form-container">
            <h2>Objednávkový formulár</h2>
            <div>
                <label htmlFor="meno">Meno:<span className="required">*</span></label>
                <input
                    id="meno"
                    name="meno"
                    type="text"
                    value={order.meno}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label htmlFor="priezvisko">Priezvisko:<span className="required">*</span></label>
                <input
                    id="priezvisko"
                    name="priezvisko"
                    type="text"
                    value={order.priezvisko}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label htmlFor="email">Email:<span className="required">*</span></label>
                <input
                    id="email"
                    name="email"
                    type="email"
                    value={order.email}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label htmlFor="adresa1">Adresa (Ulica a číslo domu):<span className="required">*</span></label>
                <input
                    id="adresa1"
                    name="adresa1"
                    type="text"
                    value={order.adresa1}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label htmlFor="adresa2">Adresa (PSČ a mesto):<span className="required">*</span></label>
                <input
                    id="adresa2"
                    name="adresa2"
                    type="text"
                    value={order.adresa2}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label htmlFor="adresa3">Adresa (Štát):<span className="required">*</span></label>
                <input
                    id="adresa3"
                    name="adresa3"
                    type="text"
                    value={order.adresa3}
                    onChange={handleChange}
                    required
                />
            </div>
            <button type="submit">Objednať</button>
        </form>
    );
};

export default OrderForm;
