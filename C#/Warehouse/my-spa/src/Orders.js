import React, { useState, useEffect } from 'react';
import Pagination from './Pagination';

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [ordersPerPage] = useState(6); // počet objednávok na stránku

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const response = await fetch('http://localhost:5000/Order/GetOrders');
                const data = await response.json();
                setOrders(data);
            } catch (error) {
                console.error("Error loading orders:", error);
            }
        };
        fetchOrders();
    }, []);

    const indexOfLastOrder = currentPage * ordersPerPage;
    const indexOfFirstOrder = indexOfLastOrder - ordersPerPage;
    const currentOrders = orders.slice(indexOfFirstOrder, indexOfLastOrder);

    const paginate = pageNumber => setCurrentPage(pageNumber);
    const totalPages = Math.ceil(orders.length / ordersPerPage);


    return (
        <div className="main-content">
            <h2>Objednávky</h2>
            <table>
                <thead>
                    <tr>
                        <th>Číslo objednávky</th>
                        <th>Objednávateľ</th>
                        <th>Dátum objednávky</th>
                        <th>Stav</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map(order => (
                        <tr key={order.order_id}>
                            <td>{order.order_id}</td>
                            <td>{`UserID ${order.user_id}`}</td>
                            <td>{order.order_date}</td>
                            <td>{order.status}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate} />
        </div>
    );
};

export default Orders;
