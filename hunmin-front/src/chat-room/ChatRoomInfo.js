import React, { useEffect, useState } from 'react';
import api from '../axios';

const ChatRoomInfo = ({ chatRoomId, setPartnerName, page, size }) => { // page, size props 추가
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchRoomDetails = async () => {
            try {
                const response = await api.get(`/chat-room/list`, {
                    params: {
                        page: 1,
                        size: 10
                    } // 페이지와 사이즈 파라미터 추가
                });
                console.log("API Response:", response.data);
                // content 배열에서 첫 번째 항목의 partnerName 가져오기 (필요에 따라 조건 변경 가능)
                const partnerData = response.data.content.find(item => item.chatRoomId == chatRoomId);
                const partnerName = partnerData?.partnerName || '알 수 없는 사용자';

                console.log(`partnerName ${partnerName}`)
                setPartnerName(partnerName);  // partnerName을 부모 컴포넌트로 전달
            } catch (error) {
                console.error('채팅방 정보를 불러오는 데 실패했습니다.', error);
            } finally {
                setLoading(false);
            }
        };

        fetchRoomDetails();
    }, [chatRoomId, setPartnerName, page, size]); // page와 size를 dependency에 추가

    if (loading) return <div>로딩 중...</div>;

    return null;
};

export default ChatRoomInfo;
