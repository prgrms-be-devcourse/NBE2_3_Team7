import React, { useEffect, useState } from 'react';
//import axios from 'axios';
import api from '../axios';
import { useParams, Link, useNavigate } from 'react-router-dom';
import Markdown from 'markdown-to-jsx';
import './NoticeDetailPage.css';
import './ErrorMessage.css';

import NoticeListPage from "./NoticeListPage";

const NoticeDetailPage = () => {
    const { id } = useParams();
    const [notice, setNotice] = useState(null);
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState(''); // 에러 메시지 상태 추가

    useEffect(() => {
        api.get(`/notices/${id}`)
        //axios.get(`/api/notices/${id}`)
            .then(response => {
                setNotice(response.data);
            })
            .catch(error => {
                if (error.response && error.response.data) {
                    setErrorMessage(`Error: ${error.response.data.error}`); // 에러 메시지를 팝업으로 표시
                    console.error(error)
                } else {
                    setErrorMessage("There was an error fetching/deleting the notice!");
                }
            });
    }, [id]);

    const handleDelete = () => {
        api.delete(`/notices/${id}`)
            .then(response => {
                if (response.data.result === 'success') {
                    alert('공지사항이 삭제되었습니다.');
                    navigate('/notices');
                } else {
                    alert('공지사항 삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                if (error.response && error.response.data) {
                    setErrorMessage(`Error: ${error.response.data.error}`);
                    console.error(error);
                } else {
                    setErrorMessage("There was an error fetching/deleting the notice!");
                }
            });
    };

    const handleGoHome = () => {
        navigate('/notices');
    };

    if (!notice) return <div className="loading">Loading...</div>;

    return (
        <div className="container">
            <div className="banner">
                <div className="icon"></div>
                <div className="header-text">훈민정음 2.0</div>
            </div>
            {errorMessage && (
                <div className="error-message" style={{color: 'red', marginBottom: '10px'}}>
                    {errorMessage}
                </div>
            )}
            <h1 className="title">{notice.title}</h1>
            <div className="content">
                <Markdown>{notice.content}</Markdown>
            </div>
            <Link to={`/edit-notice/${notice.noticeId}`} className="edit-link">공지사항 수정</Link>
            <button onClick={handleGoHome} className="home-button">공지사항 목록으로 돌아가기</button>
            <button onClick={handleDelete} className="delete-button">공지사항 삭제</button>
        </div>
    );
};

export default NoticeDetailPage;

