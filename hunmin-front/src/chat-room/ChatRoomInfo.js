import React, { useEffect, useState } from 'react';
import api from '../axios';
import { Box, Typography, CircularProgress } from '@mui/material';

const ChatRoomInfo = ({ chatRoomId }) => {
    const [loading, setLoading] = useState(true);
    const [chatRoom, setChatRoom] = useState(null);

    useEffect(() => {
        const fetchChatRoomDetails = async () => {
            setLoading(true);
            try {
                const response = await api.get(`/chat-room/${chatRoomId}`);
                setChatRoom(response.data);
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

    return (
        <Box>
            <Typography variant="h5" gutterBottom>
                채팅방 정보
            </Typography>
            <Typography variant="body1">채팅방 ID: {chatRoom.chatRoomId}</Typography>
            <Typography variant="body1">내 닉네임: {chatRoom.nickName}</Typography>
            <Typography variant="body1">상대방 닉네임: {chatRoom.partnerName}</Typography>
            <Typography variant="body1">
                생성일: {chatRoom.createdAt ? new Date(chatRoom.createdAt).toLocaleString() : 'N/A'}
            </Typography>
        </Box>
    );
};

export default ChatRoomInfo;
