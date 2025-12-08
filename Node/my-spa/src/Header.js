import React from 'react';
import { Link } from 'react-router-dom';

const HeaderTop = ({ onLoginClick }) => (
    <div id="head_top">
        WAREHOUSE
        <Link to="/login" className="user_block" onClick={() => onLoginClick()}>
            Prihlásiť sa
        </Link>
    </div>
);

const HeaderBottom = () => (
    <div id="head_bottom">
        <Link to="/" className="header-link">Kategórie</Link>
        <Link to="/orders" className="header-link">Objednávky</Link>
        <Link to="/users" className="header-link">Používatelia</Link>
    </div>
);

const Header = ({ onLoginClick }) => (
    <div id="head_content">
        <HeaderTop onLoginClick={onLoginClick} />
        <HeaderBottom />
    </div>
);

export default Header;