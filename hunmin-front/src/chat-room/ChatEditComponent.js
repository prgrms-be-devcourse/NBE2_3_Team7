import React, { useState, useEffect } from 'react';
import api from '../axios';

const ChatEditComponent = ({
                               chatMessageId,
                               chatRoomId,
                               memberId,
                               originalMessage,
                               onUpdateSuccess,
                               onCancel,
                           }) => {
    const [editedMessage, setEditedMessage] = useState(originalMessage);
    const [nickName, setNickName] = useState('');
    const [date, setDate] = useState(''); // 날짜 상태 추가

    useEffect(() => {
        const storedNickName = localStorage.getItem('nickname');
        if (storedNickName) {
            setNickName(storedNickName);
        }
    }, []);

    const handleUpdate = async () => {
        if (editedMessage.trim() === '') {
            alert('메시지를 입력해주세요.');
            return;
        }

        try {
            const token = localStorage.getItem('token');

            const requestBody = {
                chatMessageId: chatMessageId,
                chatRoomId: chatRoomId,
                memberId: memberId,
                nickName: nickName,
                message: editedMessage,
                type: 'TALK',
            };

            const response = await api.put('/chat', requestBody, {
                headers: {
                    Authorization: `${token}`,
                    'Content-Type': 'application/json',
                },
            });

            // 서버에서 오는 날짜 값으로 설정
            setDate(response.data.date); // 날짜 상태 업데이트
            onUpdateSuccess({ ...response.data, date: response.data.date });
        } catch (error) {
            console.error('메시지 수정 실패:', error);
            alert('메시지 수정에 실패했습니다. 다시 시도해주세요.');
        }
    };


    return (
        <div className="chat-edit-container">
            <textarea
                value={editedMessage}
                onChange={(e) => setEditedMessage(e.target.value)}
                className="chat-edit-textarea"
            />
            <div className="chat-edit-info">
            </div>
            <div className="chat-edit-buttons">
                <button onClick={handleUpdate} className="chat-edit-save-button">
                    저장
                </button>
                <button onClick={onCancel} className="chat-edit-cancel-button">
                    취소
                </button>
            </div>
        </div>
    );
};

export default ChatEditComponent;
