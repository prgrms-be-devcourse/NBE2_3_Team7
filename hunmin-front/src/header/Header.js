import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../axios';
import { AppBar, Toolbar, Badge, Typography, Grid, Button } from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';

const Header = () => {
    const [notifications, setNotifications] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [showPopup, setShowPopup] = useState(false);
    const [popupMessage, setPopupMessage] = useState(null);

    const displayPopup = (message) => {
        if (message) {
            setPopupMessage(message);
            setShowPopup(true);
            setTimeout(() => setShowPopup(false), 5000);
        }
    };

    const fetchNotifications = async () => {
        const memberId = localStorage.getItem('memberId'); // 로컬 스토리지에서 memberId 가져오기
        console.log("Fetching notifications for memberId:", memberId); // 디버깅 로그 추가
        try {
            const response = await api.get(`/notification/${memberId}`); // memberId를 URL에 포함
            const validNotifications = response.data.filter(notification => notification.message);
            setNotifications(validNotifications);
            console.log("Fetched notifications:", validNotifications); // 가져온 알림 로그
        } catch (error) {
            console.error('Error fetching notifications:', error);
        }
    };

    useEffect(() => {
        const memberId = localStorage.getItem('memberId');
        if (!memberId) return; // memberId가 없으면 SSE 구독하지 않음

        console.log("Subscribing to notifications for memberId:", memberId); // 디버깅 로그 추가
        const eventSource = new EventSource(`http://localhost:8080/api/notification/subscribe/${memberId}`);

        // SSE 이벤트 핸들링
        eventSource.onmessage = (event) => {
            const newNotification = JSON.parse(event.data);
            console.log("New notification received:", newNotification); // 수신한 알림 로그
            if (newNotification && newNotification.message) {
                setNotifications(prev => [newNotification, ...prev]);
                // 최신 알림 메시지로 팝업 표시
                displayPopup(newNotification.message);
            }
        };

        // 에러 처리
        eventSource.onerror = (event) => {
            console.error('EventSource failed:', event);
            eventSource.close(); // SSE 연결을 닫습니다.
        };

        // 컴포넌트 언마운트 시 연결 종료
        return () => {
            eventSource.close();
        };
    }, []);

    const toggleDropdown = () => {
        if (!showDropdown) {
            fetchNotifications();
        }
        setShowDropdown(!showDropdown);
    };

    const markAsRead = async (notificationId) => {
        try {
            await api.put(`/notification/${notificationId}`);
            setNotifications(prevNotifications =>
                prevNotifications.map(notification =>
                    notification.notificationId === notificationId
                        ? { ...notification, isRead: true }
                        : notification
                )
            );
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    const handleNotificationClick = (notification) => {
        markAsRead(notification.notificationId);

        if (notification.url.includes('/chat-room')) {
            window.location.href = notification.url;
        }else if(notification.url.includes('/follow')){
            window.location.href = "http://localhost:3000/followForm";
        }else {
            window.location.href = notification.url;
        }
    };

    return (
        <AppBar position="static">
            <Toolbar style={{ display: 'flex', justifyContent: 'space-between' }}>
                <Grid container justifyContent="space-between" alignItems="center">
                    <Grid item>
                        <Link to="/" style={{ marginRight: '20px', textDecoration: 'none', color: 'white' }}>
                            <Typography variant="h6" style={{ fontSize: '1.25rem' }}>훈민정음 2.0</Typography>
                        </Link>
                    </Grid>
                    <Grid item>
                        <Link to="/notices" style={{ marginRight: '20px', textDecoration: 'none', color: 'white' }}>
                            <Button color="inherit" style={{ fontSize: '1.25rem' }}>공지사항</Button>
                        </Link>
                    </Grid>
                    <Grid item style={{ position: 'relative' }}>
                        <Badge badgeContent={notifications.filter(n => !n.isRead).length} color="secondary">
                            <NotificationsIcon onClick={toggleDropdown} style={{ cursor: 'pointer' }} />
                        </Badge>
                        {showDropdown && (
                            <div style={{
                                position: 'absolute',
                                top: '30px',
                                right: '0',
                                backgroundColor: '#fff',
                                border: '1px solid #ccc',
                                padding: '10px',
                                width: '300px',
                                zIndex: 9999
                            }}>
                                {notifications.length > 0 ? (
                                    <ul style={{ listStyle: 'none', padding: 0 }}>
                                        {notifications.map((notification) => (
                                            <li key={notification.notificationId} style={{
                                                marginBottom: '10px',
                                                padding: '10px',
                                                border: '1px solid #ccc',
                                                borderRadius: '5px',
                                                backgroundColor: notification.isRead ? '#e0e0e0' : '#fff',
                                                cursor: 'pointer'
                                            }} onClick={() => handleNotificationClick(notification)}>
                                                <div>
                                                    <strong style={{ color: 'black' }}>{notification.message}</strong>
                                                    <div style={{ marginTop: '5px' }}>
                                                        {notification.isRead ? (
                                                            <span style={{ color: 'black' }}>읽음</span>
                                                        ) : (
                                                            <Button
                                                                variant="outlined"
                                                                color="primary"
                                                                size="small"
                                                                onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    markAsRead(notification.notificationId);
                                                                }}
                                                            >
                                                                확인
                                                            </Button>
                                                        )}
                                                    </div>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p>새로운 알림이 없습니다.</p>
                                )}
                            </div>
                        )}
                    </Grid>
                </Grid>
            </Toolbar>
            {showPopup && popupMessage && (
                <div style={{
                    position: 'fixed',
                    top: '20px',
                    right: '50px',
                    backgroundColor: 'rgba(0, 0, 0, 0.7)',
                    color: 'white',
                    border: '1px solid black',
                    padding: '10px',
                    zIndex: 1000,
                    borderRadius: '5px'
                }}>
                    {popupMessage}
                </div>
            )}
        </AppBar>
    );
};

export default Header;
