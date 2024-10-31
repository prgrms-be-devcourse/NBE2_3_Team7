import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CommentPage from '../comment/CommentPage';
import KakaoMapSearch from './map/KakaoMapSearch';

/* Popover 임포트 추가 */
import { Typography, Button, TextField, Grid, Paper, Popover } from '@mui/material'; // Popover 추가

import LocationOnIcon from "@mui/icons-material/LocationOn";
import BoardWrite from '../board/write/BoardWrite';
import api from '../axios';
import IconButton from '@mui/material/IconButton';
import BookmarkIcon from '@mui/icons-material/Bookmark';
import BookmarkBorderIcon from '@mui/icons-material/BookmarkBorder';
import {FaUserCircle} from "react-icons/fa";

const BoardDetailPage = () => {
    const { boardId } = useParams();
    const navigate = useNavigate();

    /* 기존 상태들 */
    const [board, setBoard] = useState(null);
    const [isEditMode, setIsEditMode] = useState(false);
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [location, setLocation] = useState(null);
    const [originalLocation, setOriginalLocation] = useState(null);
    const [imageUrls, setImageUrls] = useState([]);
    const [memberId, setMemberId] = useState('');
    const [originalMemberId, setOriginalMemberId] = useState('');
    const [isBookmarked, setIsBookmarked] = useState(false);

    /* 추가된 상태들 */
    const [anchorEl, setAnchorEl] = useState(null); // Popover 위치 지정
    const [isFollowing, setIsFollowing] = useState(false); // 팔로우 여부

    /* useEffect 분리 및 수정 */
    useEffect(() => {
        // 로컬 스토리지에서 memberId 가져오기
        const storedMemberId = localStorage.getItem('memberId');
        if (storedMemberId) {
            setMemberId(storedMemberId);
        }
    }, []);

    useEffect(() => {
        if (memberId && boardId) {
            fetchBoard();
            checkIfBookmarked();
        }
    }, [memberId, boardId]);

    const fetchBoard = async () => {
        try {
            const response = await api.get(`/board/${boardId}`);
            setBoard(response.data);
            setTitle(response.data.title);
            setContent(response.data.content);
            setOriginalLocation({
                name: response.data.location,
                latitude: response.data.latitude,
                longitude: response.data.longitude,
            });
            setLocation({
                name: response.data.location,
                latitude: response.data.latitude,
                longitude: response.data.longitude,
            });
            setImageUrls(response.data.imageUrls || []);
            setOriginalMemberId(response.data.memberId);

            /* 팔로우 여부 확인 함수 호출 추가 */
            checkIfFollowing(response.data.memberId);
        } catch (error) {
            console.error('Error fetching board:', error);
        }
    };

    /* 팔로우 여부 확인 함수 추가 */
    const checkIfFollowing = async (targetMemberId) => {
        try {
            const response = await api.get(`/follow/check/${targetMemberId}`, {
                params: { memberId },
            });
            setIsFollowing(response.data.isFollowing);
        } catch (error) {
            console.error('Error checking follow status:', error);
        }
    };

    /* 팔로우 요청 함수 추가 */
    const handleFollowRequest = async () => {
        try {
            if (memberId === originalMemberId) {
                alert('자기 자신을 팔로우할 수 없습니다.');
                return;
            }
            await api.post(`/follow/${originalMemberId}`, null, {
                params: { memberId },
            });
            alert('팔로우 요청을 보냈습니다.');
            setIsFollowing(true);
            handlePopoverClose();
        } catch (error) {
            console.error('Error sending follow request:', error);
            alert('팔로우 요청을 보내는 데 실패했습니다.');
        }
    };

    /* Popover 열기 및 닫기 함수 추가 */
    const handlePopoverOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

    const open = Boolean(anchorEl);

    /* 나머지 기존 함수들 */
    const handleDelete = async () => {
        try {
            await api.delete(`/board/${boardId}`);
            navigate('/');
        } catch (error) {
            console.error('Error deleting board:', error);
        }
    };

    const handleUpdate = async () => {
        if (!location) {
            alert('위치를 선택해 주세요!');
            return;
        }
        try {
            const updatedBoard = {
                boardId,
                title,
                content,
                memberId: originalMemberId,
                location: location.name,
                latitude: location.latitude,
                longitude: location.longitude,
                imageUrls: imageUrls.length > 0 ? imageUrls : null,
            };
            await api.put(`/board/${boardId}`, updatedBoard);
            setIsEditMode(false);
            fetchBoard();
        } catch (error) {
            console.error('Error updating board:', error);
        }
    };

    const handleLocationSelect = (selectedLocation) => {
        setLocation(selectedLocation);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    };

    const uploadImage = async (file) => {
        const formData = new FormData();
        formData.append('files', file);
        try {
            const response = await api.post('/board/uploadImage', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            return response.data[0];
        } catch (error) {
            console.error('Image upload failed:', error);
            return null;
        }
    };

    const checkIfBookmarked = async () => {
        try {
            const storedMemberId = localStorage.getItem('memberId');
            const response = await api.get(`/bookmark/${boardId}/member/${storedMemberId}`);
            setIsBookmarked(response.data);
        } catch (error) {
            console.error('Error checking bookmark:', error);
        }
    };

    const toggleBookmark = async () => {
        try {
            if (isBookmarked) {
                await api.delete(`/bookmark/${boardId}`, { params: { memberId } });
            } else {
                await api.post(`/bookmark/${boardId}`, null, { params: { memberId } });
            }
            setIsBookmarked(!isBookmarked);
        } catch (error) {
            console.error('Error toggling bookmark:', error);
        }
    };

    const isValidProfileImage = (image) => {
        return image && !image.includes('null');
    };

    if (!board) {
        return <div>Loading...</div>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <Paper elevation={3} style={{ padding: '20px' }}>
                {isEditMode ? (
                    /* 수정 모드 UI */
                    <div>
                        <Typography variant="h5">게시글 수정</Typography>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <TextField
                                    label="제목"
                                    variant="outlined"
                                    fullWidth
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <BoardWrite
                                    value={content}
                                    onChange={setContent}
                                    uploadImage={uploadImage} // 이미지 업로드 함수 전달
                                    setImageUrls={setImageUrls} // 이미지 URL 상태 변경 함수 전달
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <KakaoMapSearch onLocationSelect={handleLocationSelect} />
                                <Typography variant="subtitle1">수정 전 장소: {originalLocation?.name || '없음'}</Typography>
                                <Typography variant="subtitle1">수정 후 장소: {location?.name || '선택된 장소 없음'}</Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <Button variant="contained" color="primary" onClick={handleUpdate}>저장</Button>
                                <Button variant="outlined" color="secondary" onClick={() => setIsEditMode(false)}>취소</Button>
                            </Grid>
                        </Grid>
                    </div>
                ) : (
                    <div>
                        <Typography variant="h5">{board.title}</Typography>
                        <Typography variant="body1">
                            {isValidProfileImage(board.profileImage) ? (
                                <img
                                    src={board.profileImage}
                                    alt="프로필"
                                    style={{
                                        width: '30px',
                                        height: '30px',
                                        borderRadius: '50%',
                                        objectFit: 'cover',
                                    }}
                                />
                            ) : (
                                <FaUserCircle size={30} style={{color: '#fff'}}/> // 프로필 아이콘 표시
                            )} <span
                            onClick={handlePopoverOpen}
                            style={{cursor: 'pointer', color: '#007bff', textDecoration: 'underline'}}
                        >
                                {board.nickname}
                            </span>
                            <Popover
                                open={open}
                                anchorEl={anchorEl}
                                onClose={handlePopoverClose}
                                anchorOrigin={{
                                    vertical: 'bottom',
                                    horizontal: 'left',
                                }}
                                transformOrigin={{
                                    vertical: 'top',
                                    horizontal: 'left',
                                }}
                            >
                                <div style={{padding: '10px'}}>
                                    <Typography variant="subtitle1">{board.nickname}</Typography>
                                    {memberId != originalMemberId && (
                                        <Button
                                            variant="contained"
                                            color="primary"
                                            onClick={handleFollowRequest}
                                            disabled={isFollowing}
                                        >
                                            {isFollowing ? '팔로잉' : '팔로우'}
                                        </Button>
                                    )}
                                </div>
                            </Popover>
                            <span style={{margin: '0 8px'}}/> {/* 공백 추가 */}
                            <strong>{board.updatedAt ? '수정일 :' : '작성일 :'}</strong> {formatDate(board.updatedAt || board.createdAt)}
                            <span style={{margin: '0 8px'}}/>
                            {board.location && (
                                <>
                                    <LocationOnIcon style={{marginRight: '4px'}}/>
                                    {board.location}
                                </>
                            )}
                            <IconButton onClick={toggleBookmark}>
                                {isBookmarked ? (
                                    <BookmarkIcon style={{color: 'inherit'}}/>
                                ) : (
                                    <BookmarkBorderIcon style={{color: 'inherit'}}/>
                                )}
                            </IconButton>
                        </Typography>
                        <hr/>
                        <Typography variant="body1" dangerouslySetInnerHTML={{__html: board.content}}/>
                        <Grid item xs={12}>
                            <div style={{display: 'flex', justifyContent: 'flex-end', gap: '8px'}}>
                            {/* 수정 및 삭제 버튼 표시 조건 추가 */}
                                {memberId === originalMemberId && (
                                    <>
                                        <Button variant="contained" color="primary" onClick={() => setIsEditMode(true)}>수정</Button>
                                        <Button variant="outlined" color="secondary" onClick={handleDelete}>삭제</Button>
                                    </>
                                )}
                            </div>
                        </Grid>
                    </div>
                )}
            </Paper>
            <CommentPage boardId={boardId} />
        </div>
    );
};

export default BoardDetailPage;
