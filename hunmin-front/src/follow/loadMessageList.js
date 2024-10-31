// src/components/FollowList.js
import React, { useState, useEffect } from 'react';
import api from '../axios';

function FollowList() {
    const [memberId, setMemberId] = useState('');
    const [follows, setFollows] = useState([]);
    const [page, setPage] = useState(1);

    const fetchFollows = async () => {
        try {
            const response = await api.get(`/follow/list/${memberId}`, {
                params: {
                    page: page,
                    size: 10,
                },
            });
            setFollows(response.data.content);
        } catch (error) {
            console.error(error);
            alert('팔로우 목록을 가져오는데 실패했습니다.');
        }
    };

    useEffect(() => {
        if (memberId) {
            fetchFollows();
        }
    }, [memberId, page]);

    return (
        <div>
            <h2>팔로우 리스트 조회</h2>
            <input
                type="text"
                placeholder="멤버 ID"
                value={memberId}
                onChange={(e) => setMemberId(e.target.value)}
            />
            <button onClick={fetchFollows}>조회</button>
            <ul>
                {follows.map((follow) => (
                    <li key={follow.followId}>
                        팔로우 ID: {follow.followId}, 팔로워 ID: {follow.followerId}, 팔로이 ID: {follow.followeeId}
                    </li>
                ))}
            </ul>
            <button disabled={page === 1} onClick={() => setPage(page - 1)}>
                이전 페이지
            </button>
            <button onClick={() => setPage(page + 1)}>다음 페이지</button>
        </div>
    );
}

export default FollowList;
