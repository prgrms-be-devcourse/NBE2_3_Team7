// src/components/DeleteFollower.js
import React, { useState } from 'react';
import api from '../axios';

function DeleteFollower() {
    const [memberId, setMemberId] = useState('');

    const handleDelete = async () => {
        try {
            const response = await api.delete(`/follow/${memberId}`);
            alert('팔로우를 삭제했습니다.');
        } catch (error) {
            console.error(error);
            alert('팔로우 삭제에 실패했습니다.');
        }
    };

    return (
        <div>
            <h2>팔로우 삭제하기</h2>
            <input
                type="text"
                placeholder="삭제할 멤버 ID"
                value={memberId}
                onChange={(e) => setMemberId(e.target.value)}
            />
            <button onClick={handleDelete}>팔로우 삭제</button>
        </div>
    );
}

export default DeleteFollower;
