// components/NavBar.js
import React from 'react';
import { Link } from 'react-router-dom';

const NavBar = () => {
    return (
        <nav>
            <ul>
                <li><Link to="/">홈</Link></li>
                <li><Link to="/main">계정 권한</Link></li>
                <li><Link to="/register">회원가입</Link></li>
                <li><Link to="/login">로그인</Link></li>
                <li><Link to="/update">회원정보 수정</Link></li>
                <li><Link to="/create-board">게시물 작성</Link></li>
                <li><Link to="/admin">관리자</Link></li>
            </ul>
        </nav>
    );
};

export default NavBar;
