import React, { useEffect, useState } from 'react';
//import axios from 'axios';
import api from '../axios';
import { useParams, Link } from 'react-router-dom';

const NoticeDetail = () => {
    const { id } = useParams();
    const [notice, setNotice] = useState(null);

    useEffect(() => {
        //axios.get(`/api/notices/${id}`)
        api.get(`/notices/${id}`)
            .then(response => {
                setNotice(response.data);
            })
            .catch(error => {
                console.error("There was an error fetching the notice!", error);
            });
    }, [id]);

    if (!notice) return <div>Loading...</div>;

    return (
        <div>
            <h1>{notice.title}</h1>
            <p>{notice.content}</p>
            <Link to={`/edit/${notice.noticeId}`}>공지사항 수정</Link>
        </div>
    );
};

export default NoticeDetail;
