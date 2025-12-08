import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Pagination from './Pagination.js';
import Table from './Table.js'; // Ako predpokladáme, že Table bude používať <Link>
import ProductDetail from './ProductDetail.js';
const MainContent = () => {
    const [products, setProducts] = useState([]);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const productsPerPage = 25;
    const { categoryId } = useParams();

    useEffect(() => {
        setLoading(true);
        fetch(`http://localhost:5000/Product/GetProductsByCategory/${categoryId}`)
            .then(response => response.json())
            .then(data => {
                setProducts(data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching products:', error);
                setError(error.message);
                setLoading(false);
            });
    }, [categoryId]);

    const indexOfLastProduct = currentPage * productsPerPage;
    const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
    const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);
    const totalPages = Math.ceil(products.length / productsPerPage);

    const handleProductClick = (product) => {
        setSelectedProduct(product);
    };

    const handleBackClick = () => {
        setSelectedProduct(null);
    };

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div id="main_content">
            {selectedProduct ? (
                <ProductDetail
                    product={selectedProduct}
                    onBackClick={handleBackClick}
                />
            ) : (
                <>
                    <h2>Dostupné položky</h2>
                        <Table data={currentProducts} onProductClick={handleProductClick} />

                    <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        paginate={handlePageChange}
                    />
                </>
            )}
        </div>
    );
};


export default MainContent;
