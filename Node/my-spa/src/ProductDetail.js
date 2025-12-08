import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const ProductDetail = () => {
    const [product, setProduct] = useState(null);
    const { productId } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        if (productId) {
            fetch(`http://localhost:5000/Product/GetProduct/${productId}`)
                .then(response => {
                    if (!response.ok) throw new Error('Failed to fetch');
                    return response.json();
                })
                .then(data => {
                    setProduct(data);
                })
                .catch(error => {
                    console.error('Failed to load product:', error);
                });
        } else {
            console.log('Product ID is undefined');
        }
    }, [productId]);

    if (!product) {
        return <div>Loading...</div>;
    }

    const handleBackClick = () => {
        navigate(-1);
    };
    console.log('Product obrazok: ', product.photo_url);
    const handleOrderClick = () => {
        navigate('/order-form', { state: { product } });
    };

    return (
        <div id="product_detail">
            <div className="product_image">
                {product.photo_url ? (
                    <img src={product.photo_url} alt="Produktový obrázok" style={{ width: '100%', height: '100%' }} />
                ) : (
                    <span>Tu bude obrázok</span>
                )}
            </div>
            <div className="product_info">
                <h2>{product.name}</h2>
                <div><strong>Cena:</strong> {product.price} Kč </div>
                <div><strong>Popis:</strong> {product.description}</div>
                <div><strong>Dostupné množstvo:</strong> {product.stock_quantity} ks</div>
                <div><strong>DIN:</strong> {product.din}</div>
                <button onClick={handleBackClick}>Späť na zoznam produktov</button>
                <button onClick={handleOrderClick}>Objednať</button>
            </div>
        </div>
    );
};

export default ProductDetail;
