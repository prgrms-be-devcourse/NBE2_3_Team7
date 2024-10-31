import React, { useEffect, useState } from 'react';
//import axios from 'axios';
import api from '../axios';
import { Link } from 'react-router-dom';

const NoticeList = () => {
    const [notices, setNotices] = useState([]);

    useEffect(() => {
        api.get('/notices/list/1') // 1 페이지를 가져옵니다.
            .then(response => {
                setNotices(response.data);
            })
            .catch(error => {
                console.error("There was an error fetching the notices!", error);
            });
    }, []);

    return (
        <div>
            <h1>공지사항 목록</h1>
            <ul>
                {notices.map(notice => (
                    <li key={notice.noticeId}>
                        <Link to={`/notices/${notice.noticeId}`}>{notice.title}</Link>
                    </li>
                ))}
            </ul>
            <Link to="/create">공지사항 생성</Link>
        </div>
    );
};

export default NoticeList;
