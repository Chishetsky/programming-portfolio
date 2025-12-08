import React from 'react';
import { Link } from 'react-router-dom';

const Table = ({ data }) => (
    <div className="table-scrollable">
        <table>
            <thead>
                <tr>
                    <th>Poradové číslo</th>
                    <th>Názov produktu</th>
                    <th>Cena za 1ks</th>
                    <th>Na sklade</th>
                </tr>
            </thead>
            <tbody>
                {data.map((product, index) => {
                    console.log("Product ID v tabulke:", product);  // Overenie ID každého produktu
                    console.log("Product ID v tabulke ked pouzijem .product_id:", product.product_id);  // Overenie ID každého produktu
                    return (
                        <tr key={product.id}>

                            <td>{index + 1}</td>
                            <td>
                                <Link to={`/product/${product.product_id}`} style={{ cursor: 'pointer', textDecoration: 'none', color: 'black' }}>
                                    {product.name}
                                </Link>
                            </td>
                            <td>{product.price}</td>
                            <td>{product.stock_quantity}</td>
                        </tr>
                    );
                })}
            </tbody>
        </table>
    </div>
);

export default Table;

