import React, { useState } from 'react';
import api from '../axios';

const ChatEditComponent = ({ chatMessageId, originalMessage, onUpdateSuccess, onCancel }) => {
    const [editedMessage, setEditedMessage] = useState(originalMessage);

    const handleUpdate = async () => {
        try {
            const response = await api.put('/chat', {
                chatMessageId: chatMessageId,
                message: editedMessage,
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
