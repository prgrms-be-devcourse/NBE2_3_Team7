import React, { useEffect, useState } from 'react';
import api from '../axios';
import { Box, Typography, CircularProgress } from '@mui/material';

const ChatRoomInfo = ({ chatRoomId,  setPartnerName }) => {
    const [loading, setLoading] = useState(true);
    const [chatRoom, setChatRoom] = useState(null);
    const currentMemberId = localStorage.getItem('memberId'); // 현재 사용자 ID 가져오기

    useEffect(() => {
        const fetchChatRoomDetails = async () => {
            setLoading(true);
            try {
                const response = await api.get(`/chat-room/list`);
                const roomData = response.data;
                setChatRoom(response.data);
                // 상대방 이름 설정
                const displayName = roomData.memberId === Number(currentMemberId)
                    ? roomData.partnerName
                    : roomData.nickName;
                setPartnerName(displayName);
            } catch (error) {
                console.error('채팅방 정보를 불러오는 데 실패했습니다.', error);
            } finally {
                setLoading(false);
            }
        };

        if (chatRoomId) {
            fetchChatRoomDetails();
        }
    }, [chatRoomId]);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
                <CircularProgress />
            </Box>
        );
    }

    if (!chatRoom) {
        return <Typography variant="body1">채팅방 정보를 불러올 수 없습니다.</Typography>;
    }

    const displayName = chatRoom.memberId === Number(currentMemberId)
        ? chatRoom.partnerName // 현재 사용자가 memberId와 동일하면 partnerName 표시
        : chatRoom.nickName; // 아니라면 nickName 표시

};

export default ChatRoomInfo;
