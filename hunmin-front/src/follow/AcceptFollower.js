// src/components/AcceptFollower.js
import React, { useState } from 'react';
import api from '../axios';

function AcceptFollower() {
    const [memberId, setMemberId] = useState('');

    const handleAccept = async () => {
        try {
            const response = await api.get(`/follow/${memberId}`);
            alert('팔로우 요청을 수락했습니다.');
        } catch (error) {
            console.error(error);
            alert('팔로우 요청 수락에 실패했습니다.');
        }
    };

    return (
        <div>
            <h2>팔로우 요청 수락하기</h2>
            <input
                type="text"
                placeholder="수락할 멤버 ID"
                value={memberId}
                onChange={(e) => setMemberId(e.target.value)}
            />
            <button onClick={handleAccept}>팔로우 수락</button>
        </div>
    );
}

export default AcceptFollower;
