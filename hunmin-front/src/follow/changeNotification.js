// src/components/ChangeNotification.js
import React, { useState } from 'react';
import api from '../axios';

function ChangeNotification() {
    const [memberId, setMemberId] = useState('');

    const handleChangeNotification = async () => {
        try {
            const response = await api.post(`/follow/notification/${memberId}`);
            alert('알림 설정이 변경되었습니다.');
        } catch (error) {
            console.error(error);
            alert('알림 설정 변경에 실패했습니다.');
        }
    };

    return (
        <div>
            <h2>알림 설정 변경</h2>
            <input
                type="text"
                placeholder="멤버 ID"
                value={memberId}
                onChange={(e) => setMemberId(e.target.value)}
            />
            <button onClick={handleChangeNotification}>알림 설정 변경</button>
        </div>
    );
}

export default ChangeNotification;
