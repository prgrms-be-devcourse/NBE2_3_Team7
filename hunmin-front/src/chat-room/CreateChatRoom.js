import React, { useState } from 'react';
import api from '../axios';
import ChatRoomList from './ChatRoomList';
import './styles.css';

const CreateChatRoom = () => {
    const [nickname, setNickname] = useState('');

    const createRoom = () => {
        if (!nickname) {
            alert('닉네임을 입력해주세요.');
            return;
        }

        // 경로 파라미터로 닉네임 전달
        api.post(`/api/chat-room/${nickname}`)
            .then(response => {
                alert(`채팅방 [${response.data.partnerName}]이 생성되었습니다.`);
                setNickname('');
            })
            .catch(error => {
                alert('채팅방 생성에 실패했습니다.');
                console.error('Error creating chat room:', error);
            });
    };

    return (
        <div className="container" style={{ maxWidth: '800px', marginTop: '20px' }}>
            <div className="header">
                <a className="btn-home" href="/">Home</a>
            </div>
            <h1>채팅방 목록</h1>
            <div className="input-group">
                <input
                    type="text"
                    className="form-control"
                    value={nickname}
                    onChange={e => setNickname(e.target.value)}
                    placeholder="닉네임을 입력하시오."
                />
                <button className="btn btn-primary" onClick={createRoom}>채팅방 개설</button>
            </div>
            <ChatRoomList />
        </div>
    );
};

export default CreateChatRoom;
