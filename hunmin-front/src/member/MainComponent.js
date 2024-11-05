import React, { useEffect, useState } from 'react';
import axios from 'axios';

function MainComponent() {
    const [data, setData] = useState({ name: '', role: '' });

    useEffect(() => {
        console.log('*** MainComponent 마운트');
        console.log('*** 현재 localStorage image:', localStorage.getItem('image'));

        // localStorage 확인

        axios.get('/main')
            .then(response => {
                console.log('*** 서버 응답:', response.data); // 서버 응답 데이터 확인
                setData(response.data);
            })
            .catch(error => {
                console.error('*** 에러 상세:', error.response); // 에러 상세 정보
                alert('권한 데이터를 가져오는데 실패했습니다.');
            });
    }, []);

    return (
        <div>
            <h2>메인 페이지</h2>
            <p>이름: {data.name}</p>
            <p>역할: {data.role}</p>
            {data.image && (
                <div>
                    <p>프로필 이미지 URL: {data.image}</p>
                    <img
                        src={data.image}
                        alt="프로필"
                        style={{width: '100px', height: '100px'}}
                        onError={(e) => {
                            console.error('이미지 로드 실패:', e);
                            console.log('실패한 이미지 URL:', data.image);
                        }}
                    />
                </div>
            )}
        </div>
    );
}