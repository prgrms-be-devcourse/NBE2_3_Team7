import React, { useState, useEffect, useCallback } from 'react';
import api from '../axios';
import {
    Container,
    Typography,
    List,
    ListItem,
    ListItemAvatar,
    Avatar,
    ListItemText,
    Pagination,
    Button,
    Chip,
    Stack,
    Grid,
    CircularProgress,
} from '@mui/material';
import { makeStyles } from '@mui/styles';

const useStyles = makeStyles({
    container: {
        marginTop: '50px',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    list: {
        width: '100%',
        maxWidth: 700,
        backgroundColor: '#f9f9f9',
    },
    pagination: {
        marginTop: '20px',
    },
    // 스타일 클래스들은 필요에 따라 유지 또는 제거
});

function FollowForm() {
    const classes = useStyles();
    const [followList, setFollowList] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [error, setError] = useState(null);
    const [loadingId, setLoadingId] = useState(null);

    const fetchFollowList = useCallback(async (pageNumber) => {
        try {
            const response = await api.get('/follow/list', {
                params: {
                    page: pageNumber,
                    size: 10,
                },
                headers: {
                    Authorization: `${localStorage.getItem('token')}`,
                },
            });

            const followContent = response.data.content.map((follow) => ({
                followId: follow.followId,
                followerId: follow.followerId,
                followeeId: follow.followeeId,
                followerNickname: follow.followerName,
                followerEmail: follow.followerEmail,
                followerProfileImageUrl:
                    follow.followerImage || 'https://via.placeholder.com/150?text=Default+Profile',
                followeeNickname: follow.followeeName,
                followeeEmail: follow.followeeEmail,
                followeeProfileImageUrl:
                    follow.followeeImage || 'https://via.placeholder.com/150?text=Default+Profile',
                isNotificationEnabled: follow.notification,
                isBlocked: follow.isBlock,
                status: follow.status,
            }));
            setFollowList(followContent);
            setTotalPages(response.data.totalPages);
            setError(null);
            console.log('followContent {}',followContent)
        } catch (error) {
            console.error('팔로우 목록을 불러오는 중 오류 발생:', error);
            setError('팔로우 목록을 불러오는 중 오류가 발생했습니다.');
        }
    }, []);

    useEffect(() => {
        fetchFollowList(page);
    }, [page, fetchFollowList]);

    const handlePageChange = (event, value) => {
        setPage(value);
    };

    // 알림 토글 함수
    const toggleNotification = async (followerId, isNotificationEnabled) => {
        setLoadingId(followerId); // 로딩 상태 설정
        try {
            await api.post(
                `/follow/notification/${followerId}`,
                {},
                {
                    headers: {
                        Authorization: `${localStorage.getItem('token')}`,
                    },
                }
            );
            setFollowList((prevList) =>
                prevList.map((follow) =>
                    follow.followerId === followerId
                        ? { ...follow, isNotificationEnabled: !isNotificationEnabled }
                        : follow
                )
            );
        } catch (error) {
            console.error('알림 설정을 변경하는 중 오류 발생:', error);
            setError('알림 설정을 변경하는 중 오류가 발생했습니다.');
        } finally {
            setLoadingId(null); // 로딩 상태 해제
        }
    };

    // 차단 토글 함수
    const toggleBlock = async (followerId, isBlocked) => {
        setLoadingId(followerId); // 로딩 상태 설정
        try {
            await api.post(
                `/follow/block/${followerId}`,
                {},
                {
                    headers: {
                        Authorization: `${localStorage.getItem('token')}`,
                    },
                }
            );
            setFollowList((prevList) =>
                prevList.map((follow) =>
                    follow.followerId === followerId
                        ? {
                            ...follow,
                            isBlocked: !isBlocked,
                            isNotificationEnabled: isBlocked,
                        }
                        : follow
                )
            );
        } catch (error) {
            console.error('차단 설정을 변경하는 중 오류 발생:', error);
            setError('차단 설정을 변경하는 중 오류가 발생했습니다.');
        } finally {
            setLoadingId(null); // 로딩 상태 해제
        }
    };

    // 팔로우 요청 수락
    const acceptFollowRequest = async (followerId) => {
        setLoadingId(followerId); // 로딩 상태 설정
        try {
            await api.get(
                `/follow/${followerId}`,
                {},
                {
                    headers: {
                        Authorization: `${localStorage.getItem('token')}`,
                    },
                }
            );
            setFollowList((prevList) =>
                prevList.map((follow) =>
                    follow.followerId === followerId ? { ...follow, status: 'ACCEPTED' } : follow
                )
            );
            setError(null); // 에러 초기화
        } catch (error) {
            console.error('팔로우 요청을 수락하는 중 오류 발생:', error);
            setError('팔로우 요청을 수락하는 중 오류가 발생했습니다.');
        } finally {
            setLoadingId(null); // 로딩 상태 해제
        }
    };

    return (
        <Container className={classes.container}>
            <Typography variant="h4" gutterBottom>
                FOLLOW LIST
            </Typography>
            {error && <Typography color="error">{error}</Typography>}
            <List className={classes.list}>
                {followList.map((follow) => (
                    <ListItem key={follow.followId} disableGutters divider>
                        <Grid container alignItems="center" spacing={2}>
                            <Grid item xs={2}>
                                <ListItemAvatar>
                                    <Avatar
                                        src={follow.followerProfileImageUrl}
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = 'https://via.placeholder.com/150?text=Default+Profile';
                                        }}
                                    />
                                </ListItemAvatar>
                            </Grid>
                            <Grid item xs={4}>
                                <ListItemText
                                    primary={follow.followerNickname}
                                    secondary={follow.followerEmail}
                                />
                                <Chip
                                    label={
                                        follow.status === 'ACCEPTED'
                                            ? '수락됨'
                                            : follow.status === 'PENDING'
                                                ? '대기 중'
                                                : '팔로우하지 않음'
                                    }
                                    color={
                                        follow.status === 'ACCEPTED'
                                            ? 'success'
                                            : follow.status === 'PENDING'
                                                ? 'warning'
                                                : 'default'
                                    }
                                    size="small"
                                    className={classes.statusChip}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <Stack direction="row" spacing={1}>
                                    {/* 상태에 따른 버튼 및 알림/차단 버튼 표시 */}
                                    {follow.status === 'PENDING' ? (
                                        <Button
                                            variant="contained"
                                            color="primary"
                                            onClick={() => acceptFollowRequest(follow.followerId)}
                                            disabled={loadingId === follow.followId}
                                            endIcon={
                                                loadingId === follow.followId ? (
                                                    <CircularProgress size={20} color="inherit" />
                                                ) : null
                                            }
                                        >
                                            {loadingId === follow.followId ? '처리 중...' : '수락'}
                                        </Button>
                                    ) : (
                                        <>
                                            <Button
                                                variant="contained"
                                                color={follow.isNotificationEnabled ? 'primary' : 'default'}
                                            onClick={() =>
                                                    toggleNotification(follow.followerId, follow.isNotificationEnabled)
                                                }
                                                disabled={loadingId === follow.followId || follow.isBlocked}
                                                endIcon={
                                                    loadingId === follow.followId ? (
                                                        <CircularProgress size={20} color="inherit" />
                                                    ) : null
                                                }
                                            >
                                                {follow.isNotificationEnabled ? '알림 끄기' : '알림 켜기'}
                                            </Button>

                                            <Button
                                                variant={follow.isBlocked ? 'contained' : 'outlined'}
                                                color={follow.isBlocked ? 'secondary' : 'default'}
                                                onClick={() => toggleBlock(follow.followerId, follow.isBlocked)}
                                                disabled={loadingId === follow.followId}
                                                endIcon={
                                                    loadingId === follow.followId ? (
                                                        <CircularProgress size={20} color="inherit" />
                                                    ) : null
                                                }
                                            >
                                                {follow.isBlocked ? '차단 해제' : '차단'}
                                            </Button>
                                        </>
                                    )}
                                </Stack>
                            </Grid>
                        </Grid>
                    </ListItem>
                ))}
            </List>
            <Pagination
                className={classes.pagination}
                count={totalPages}
                page={page}
                onChange={handlePageChange}
                color="primary"
            />
        </Container>
    );
}

export default FollowForm;
