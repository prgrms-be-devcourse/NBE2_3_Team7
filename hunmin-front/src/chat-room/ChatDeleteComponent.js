import React from 'react';
import api from '../axios';

const ChatDeleteComponent = ({ chatMessageId, onDeleteSuccess, onCancel }) => {
    const handleDelete = async () => {
        try {
            const response = await api.delete(`/chat/${chatMessageId}`);
            onDeleteSuccess(chatMessageId);
        } catch (error) {
            console.error('메시지 삭제 실패:', error);
        }
    };

    return (
        <div className="chat-delete-container">
            <p>이 메시지를 삭제하시겠습니까?</p>
            <div className="chat-delete-buttons">
                <button onClick={handleDelete} className="chat-delete-confirm-button">
                    삭제
                </button>
                <button onClick={onCancel} className="chat-delete-cancel-button">
                    취소
                </button>
            </div>
        </div>
    );
};

export default ChatDeleteComponent;
