// src/chat-room/ChatComponent.jsx

import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import api from '../axios';

const ChatComponent = ({ chatRoomId, setMessages, onMessageSend  }) => {
    const [message, setMessage] = useState('');
    const stompClientRef = useRef(null);
    const [type, setType] = useState('TALK');
    const [memberId, setMemberId] = useState(null);
    const [isConnected, setIsConnected] = useState(false);

    // 사용자 정보 가져오기 함수
    const fetchUserInfo = async () => {
        try {
            const response = await api.get('/chat/user-info');
            setMemberId(response.data.memberId);
        } catch (error) {
        }
    };

    // 새로운 메시지 추가 시 중복 확인
    const handleNewMessage = (newMessage) => {
        setMessages((prevMessages) => {
            if (prevMessages.some(msg => msg.chatMessageId === newMessage.chatMessageId)) {
                return prevMessages;
            }
            return [...prevMessages, newMessage];
        });
        if (onMessageSend) {
            onMessageSend();
        }
    };

    // 웹소켓 연결
    const stompConnect = () => {
        try {
            const token = localStorage.getItem('token');
            const sock = new SockJS("http://localhost:8080/ws-stomp");
            const client = new Client({
                webSocketFactory: () => sock,
                connectHeaders: {
                    Authorization: `${token}`,
                },
                debug: (str) => console.log(str),
                onConnect: () => {
                    setIsConnected(true);

                    // 채팅방 구독
                    client.subscribe(
                        `/sub/chat/room/${chatRoomId}`,
                        (message) => {
                            if (message.body) {
                                const newMessage = JSON.parse(message.body);
                                handleNewMessage(newMessage);
                            } else {
                            }
                        },
                        (error) => {
                            console.error('구독 실패:', error);
                        }
                    );
                },
                onStompError: (frame) => {
                    console.error('Broker error:', frame.headers['message']);
                    setIsConnected(false);
                },
                onWebSocketClose: () => {
                    setIsConnected(false);
                },
            });

            client.activate();
            stompClientRef.current = client;
        } catch (err) {
            console.error('WebSocket 연결 실패:', err);
        }
    };

    // 웹소켓 연결 해제
    const stompDisconnect = () => {
        if (stompClientRef.current) {
            stompClientRef.current.deactivate(() => console.log('STOMP 연결 해제'));
        }
    };

    useEffect(() => {
        fetchUserInfo();
        stompConnect();

        return () => {
            stompDisconnect();
        };
    }, [chatRoomId]);

    // 메시지 전송
    const sendMessage = () => {

        if (message.trim() === '' || !memberId) {
            return;
        }

        const token = localStorage.getItem('token');

        const chatMessageDTO = {
            chatRoomId: Number(chatRoomId),
            nickName: '나',
            memberId: Number(memberId),
            message: message,
            type: type,
        };

        if (stompClientRef.current && isConnected) {
            try {
                stompClientRef.current.publish({
                    destination: '/pub/api/chat/message',
                    body: JSON.stringify(chatMessageDTO),
                    headers: {
                        Authorization: `${token}`,
                    },
                });
                onMessageSend(); // 메시지 전송 후 스크롤 설정
            } catch (error) {
            }
        } else {
        }

        setMessage('');
    };

    return (
        <div className="chat-input-container">
            <input
                type="text"
                value={message}
                onChange={(e) => setMessage(e.target.value)} // 메시지 입력 상태 업데이트
                onKeyPress={(e) => {
                    if (e.key === 'Enter') sendMessage(); // Enter 키로 메시지 전송
                }}
                placeholder="메시지를 입력하세요"
                className="chat-input"
            />
            <button onClick={sendMessage} className="chat-send-button">전송</button>
        </div>
    );
};

export default ChatComponent;
