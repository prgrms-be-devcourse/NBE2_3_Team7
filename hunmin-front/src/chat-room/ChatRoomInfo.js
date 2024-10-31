import React, { useEffect, useState } from 'react';
import api from '../axios';

const ChatRoomInfo = ({ chatRoomId, setNickname }) => {
    const [loading, setLoading] = useState(true);

    // API를 통해 채팅방 정보 가져오기
    useEffect(() => {
        const fetchRoomDetails = async () => {
            try {
                const response = await api.get(`/chat-room/${chatRoomId}`);
                setNickname(response.data.nickname); // 부모 컴포넌트의 setNickname 호출
            } catch (error) {
                console.error('채팅방 정보를 불러오는 데 실패했습니다.', error);
            } finally {
                setLoading(false);
            }
        };

        fetchRoomDetails();
    }, [chatRoomId, setNickname]);

    if (loading) return <div>로딩 중...</div>;

    return null;
};

export default ChatRoomInfo;
