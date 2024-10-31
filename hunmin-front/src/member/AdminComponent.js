import React, { useEffect, useState } from 'react';
import axios from 'axios';

function AdminComponent() {
    const [message, setMessage] = useState('');

    useEffect(() => {
        axios.get('/api/members/admin')
            .then(response => {
                setMessage(response.data);
            })
            .catch(error => {
                console.error(error);
                alert('관리자 데이터를 가져오는데 실패했습니다.');
            });
    }, []);

    return (
        <div>
            <h2>관리자 페이지</h2>
            <p>{message}</p>
        </div>
    );
}

export default AdminComponent;
