import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

const CategoryBlock = ({ categoryId, categoryName, imageUrl }) => (
    <div className="category-block">
        {/* Pridanie categoryId do URL */}
        <Link to={`/products/category/${categoryId}`} className="category-link">
            <div className="category-name">{categoryName}</div>
        </Link>
        <img src={imageUrl} alt={`Obrázok ${categoryName}`} width="200" height="150" />
    </div>
);

const CategoriesPage = () => {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        fetch('http://localhost:5000/Category/GetCategories')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log(data);  // Tu si môžete pozrieť, čo presne API vracia
                setCategories(data);
            })
            .catch(error => console.error('Error fetching categories:', error));
    }, []);

    return (
        <div className="categories-page">
            <h2>Kategórie produktov</h2>
            <div className="categories-container">
                {categories.map(category => (
                    // Prenos categoryId do CategoryBlock
                    <CategoryBlock key={category.category_id} categoryId={category.category_id} categoryName={category.name} imageUrl={category.photo_url} />
                ))}
            </div>
        </div>
    );
};

export default CategoriesPage;