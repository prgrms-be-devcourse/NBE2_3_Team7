// src/chat-room/ChatRoomDetail.jsx

import React, { useState, useEffect, useRef, useLayoutEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ChatComponent from './ChatComponent';
import ChatRoomInfo from './ChatRoomInfo';
import ChatEditComponent from './ChatEditComponent';
import ChatDeleteComponent from './ChatDeleteComponent';
import api from '../axios';
import './ChatRoomDetail.css';
import moment from 'moment';
import { v4 as uuidv4 } from 'uuid';

const ChatRoomDetail = () => {
    const { chatRoomId } = useParams();
    const navigate = useNavigate();

    // 상태 변수 선언
    const [messages, setMessages] = useState([]);
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [isFetching, setIsFetching] = useState(false);
    const [nickname, setNickname] = useState('');
    const [currentUserId, setCurrentUserId] = useState('');
    const [currentUserName, setCurrentUserName] = useState('');
    const [editingMessageId, setEditingMessageId] = useState(null);
    const [deletingMessageId, setDeletingMessageId] = useState(null);
    const [shouldScrollToBottom, setShouldScrollToBottom] = useState(true);
    const [isPaginating, setIsPaginating] = useState(false);

    // 참조 변수 선언
    const messagesContainerRef = useRef(null);
    const messagesEndRef = useRef(null);
    const previousScrollHeight = useRef(0);

    // 사용자 정보 가져오기
    const fetchCurrentUserInfo = async () => {
        try {
            const response = await api.get('/chat/user-info');
            setCurrentUserId(response.data.memberId);
            setCurrentUserName(response.data.nickname);
        } catch (error) {
            console.error('현재 사용자 정보를 가져오는 데 실패했습니다:', error);
        }
    };

    // 메시지 페이징 불러오기
    const fetchPastMessages = async (pageNumber) => {
        if (isFetching || !hasMore) return;
        setIsFetching(true);
        setIsPaginating(true);
        try {
            if (messagesContainerRef.current) {
                previousScrollHeight.current = messagesContainerRef.current.scrollHeight;
            }

            const response = await api.get(`/chat/messages/${chatRoomId}`, {
                params: {
                    page: pageNumber,
                    size: 10,
                },
            });

            const newMessages = response.data.content;
            const lastPage = response.data.last;

            // 중복 메시지 제거
            const uniqueNewMessages = newMessages.filter(msg =>
                !messages.some(existingMsg => existingMsg.chatMessageId === msg.chatMessageId)
            );

            if (uniqueNewMessages.length === 0) {
                setHasMore(false);
            } else {
                setMessages((prevMessages) => [...uniqueNewMessages.reverse(), ...prevMessages]);
                setPage(pageNumber + 1);
                setHasMore(!lastPage);
            }

            // 페이지 번호에 따라 setShouldScrollToBottom 설정
            if (pageNumber === 1) {
                setShouldScrollToBottom(true);
            } else {
                setShouldScrollToBottom(false);
            }
        } catch (error) {
            console.error('과거 메시지 불러오기에 실패했습니다:', error);
        } finally {
            setIsFetching(false);
            setIsPaginating(false);
        }
    };


    // 초기 마운트 시 사용자 정보와 메시지 페이징 불러오기
    useEffect(() => {
        fetchCurrentUserInfo();
        fetchPastMessages(page);
        setIsPaginating(false);

    }, [chatRoomId]);

    // 스크롤을 끝까지 내리는 함수
    const scrollToBottom = () => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    };

    // 과거 메시지 로드 후 스크롤 위치를 약간 아래로 이동시키는 함수
    const scrollToPreviousPosition = () => {
        if (messagesContainerRef.current) {
            const newScrollHeight = messagesContainerRef.current.scrollHeight;
            const scrollDifference = newScrollHeight - previousScrollHeight.current;
            messagesContainerRef.current.scrollTop = scrollDifference + 5;
        }
    };


    // 메시지가 로드되었을 때의 스크롤 동작 설정
    useLayoutEffect(() => {
        if (isPaginating) {
            scrollToPreviousPosition();

            // scrollToPreviousPosition 이후의 스크롤 위치 설정
            if (messagesContainerRef.current) {
                const currentScrollTop = messagesContainerRef.current.scrollTop;
            }
        } else if (shouldScrollToBottom) {
            scrollToBottom();
        }
    }, [messages, isPaginating, shouldScrollToBottom]);

    // 스크롤 이벤트 핸들러
    const handleScroll = (e) => {
        const { scrollTop } = e.target;
        if (scrollTop === 0 && hasMore && !isFetching) {
            fetchPastMessages(page);
        }
    };

    // 메시지 작성 시간을 포맷하는 함수
    const formatDateTime = (dateTime) => {
        return moment(dateTime).format('YYYY-MM-DD HH:mm');
    };

    // 메시지 업데이트 처리 함수
    const handleUpdateMessage = (updatedMessage) => {
        setMessages((prevMessages) =>
            prevMessages.map((msg) =>
                msg.chatMessageId === updatedMessage.chatMessageId ? updatedMessage : msg
            )
        );
        setEditingMessageId(null);
    };

    // 메시지 삭제 처리 함수
    const handleDeleteMessage = (deletedMessageId) => {
        setMessages((prevMessages) =>
            prevMessages.filter((msg) => msg.chatMessageId !== deletedMessageId)
        );
        setDeletingMessageId(null);
    };

    // 컴포넌트 마운트 시 스크롤을 가장 아래로 설정
    useEffect(() => {
        setShouldScrollToBottom(true);
    }, []);

    return (
        <div className="chat-room-container">
            {/* 채팅방 헤더 */}
            <div className="chat-room-header">
                <h1 className="chat-room-title">
                    {currentUserName}과 {nickname}의 대화
                </h1>
                <button className="leave-button" onClick={() => navigate(-1)}>
                    채팅방 나가기
                </button>
            </div>

            {/* 채팅방 정보 */}
            <ChatRoomInfo chatRoomId={chatRoomId} setNickname={setNickname} />

            {/* 채팅 메시지 목록 */}
            <div className="chat-messages-container" onScroll={handleScroll} ref={messagesContainerRef}>
                <ul className="chat-messages-list">
                    {messages.map((msg) => (
                        <li
                            key={msg.chatMessageId || uuidv4()} // 고유 키 사용
                            className={msg.memberId === currentUserId ? 'my-message' : 'other-message'}
                        >
                            <div className="message-content">
                                <strong className="sender-name">
                                    {msg.memberId === currentUserId ? '나' : msg.nickName || '상대방'}
                                </strong>

                                {/* 메시지 수정 또는 일반 메시지 표시 */}
                                {editingMessageId === msg.chatMessageId ? (
                                    <ChatEditComponent
                                        chatMessageId={msg.chatMessageId}
                                        originalMessage={msg.message}
                                        onUpdateSuccess={handleUpdateMessage}
                                        onCancel={() => setEditingMessageId(null)}
                                    />
                                ) : (
                                    <>
                                        <div className="message-text">{msg.message}</div>

                                        {/* 메시지 시간 및 수정/삭제 버튼 */}
                                        <div className="message-meta">
                                            <span className="message-time">{formatDateTime(msg.createdAt)}</span>
                                            {msg.memberId === currentUserId && (
                                                <div className="message-actions">
                                                    <button
                                                        onClick={() => setEditingMessageId(msg.chatMessageId)}
                                                        className="message-edit-button"
                                                    >
                                                        수정
                                                    </button>
                                                    <button
                                                        onClick={() => setDeletingMessageId(msg.chatMessageId)}
                                                        className="message-delete-button"
                                                    >
                                                        삭제
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    </>
                                )}

                                {/* 삭제 확인 모달 */}
                                {deletingMessageId === msg.chatMessageId && (
                                    <ChatDeleteComponent
                                        chatMessageId={msg.chatMessageId}
                                        onDeleteSuccess={handleDeleteMessage}
                                        onCancel={() => setDeletingMessageId(null)}
                                    />
                                )}
                            </div>
                        </li>
                    ))}
                </ul>
                <div ref={messagesEndRef} />
            </div>

            {/* 채팅 입력창 */}
            <ChatComponent
                chatRoomId={chatRoomId}
                setMessages={setMessages}
                memberId={currentUserId}
                onMessageSend={() => setShouldScrollToBottom(true)}
            />
        </div>
    );

};

export default ChatRoomDetail;
