import React, { useState, useEffect } from 'react';
import Pagination from './Pagination';

const Users = () => {
    const [users, setUsers] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [usersPerPage] = useState(6); // počet zamestnancov na stránku

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await fetch('http://localhost:5000/User/GetUsers');
                const data = await response.json();
                setUsers(data);
            } catch (error) {
                console.error("Error loading users:", error);
            }
        };
        fetchUsers();
    }, []);

    // Získanie indexov pre aktuálnu stránku
    const indexOfLastUser = currentPage * usersPerPage;
    const indexOfFirstUser = indexOfLastUser - usersPerPage;
    const currentUsers = users.slice(indexOfFirstUser, indexOfLastUser);

    // Zmena stránky
    const paginate = pageNumber => setCurrentPage(pageNumber);
    const totalPages = Math.ceil(users.length / usersPerPage);

    return (
        <div className="users-component">
            <h2>Používatelia</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Meno</th>
                        <th>E-mail</th>
                        <th>Oprávnenie</th>
                    </tr>
                </thead>
                <tbody>
                    {currentUsers.map(user => (
                        <tr key={user.user_id}>
                            <td>{user.user_id}</td>
                            <td>{user.username}</td>
                            <td>{user.email}</td>
                            <td>{user.role}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate} />
        </div>
    );
};

export default Users;
