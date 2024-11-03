import React, { useState } from 'react';
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

    const handleUpdate = async () => {
        try {
            const token = localStorage.getItem('token');

            const requestBody = {
                chatMessageId: chatMessageId,
                chatRoomId: chatRoomId,
                memberId: memberId,
                message: editedMessage,
                type: 'CHAT',
            };

            const response = await api.put('/chat', requestBody, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            onUpdateSuccess(response.data);
        } catch (error) {
            console.error('메시지 수정 실패:', error);
        }
    };

    return (
        <div className="chat-edit-container">
            <textarea
                value={editedMessage}
                onChange={(e) => setEditedMessage(e.target.value)}
                className="chat-edit-textarea"
            />
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
