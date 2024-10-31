import React, { useEffect, useState } from 'react';
import axios from 'axios';

function MainComponent() {
    const [data, setData] = useState({ name: '', role: '' });

    useEffect(() => {
        axios.get('/main')
            .then(response => {
                setData(response.data);
            })
            .catch(error => {
                console.error(error);
                alert('권한 데이터를 가져오는데 실패했습니다.');
            });
    }, []);

    return (
        <div>
            <h2>메인 페이지</h2>
            <p>이름: {data.name}</p>
            <p>역할: {data.role}</p>
        </div>
    );
}

export default MainComponent;
