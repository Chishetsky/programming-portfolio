import React from 'react';

const Pagination = ({ currentPage, totalPages, paginate }) => {
    return (
        <div className="pagination">
            <button onClick={() => paginate(1)} disabled={currentPage === 1}>
                {'<<'}
            </button>
            <button onClick={() => paginate(currentPage - 1)} disabled={currentPage === 1}>
                {'<'}
            </button>
            <span>{currentPage}</span>
            <button onClick={() => paginate(currentPage + 1)} disabled={currentPage === totalPages}>
                {'>'}
            </button>
            <button onClick={() => paginate(totalPages)} disabled={currentPage === totalPages}>
                {'>>'}
            </button>
        </div>
    );
};

export default Pagination;
