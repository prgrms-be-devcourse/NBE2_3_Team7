// src/follow/RegisterFollower.js
import React from 'react';
import api from '../axios';

function RegisterFollower({ targetMemberId }) { // props로 대상 멤버 ID 받기
    const handleRegister = async () => {
        try {
            await api.post(`/follow/${targetMemberId}`);
            alert('팔로우 요청이 전송되었습니다.');
        } catch (error) {
            console.error(error);
            alert('팔로우 요청에 실패했습니다.');
        }
    };

    return (
        <div>
            <button onClick={handleRegister}>팔로우 요청</button>
        </div>
    );
}

export default RegisterFollower;
