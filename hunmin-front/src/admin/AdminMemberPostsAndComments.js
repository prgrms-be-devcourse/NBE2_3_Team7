import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../axios';
import {
    Box,
    Typography,
    List,
    ListItem,
    ListItemText,
    Pagination,
    Paper,
    Divider,
    CircularProgress,
    useTheme
} from '@mui/material';

const AdminMemberPostsAndComments = () => {
    const theme = useTheme();
    const { memberId } = useParams();
    const [posts, setPosts] = useState({ content: [], totalPages: 0 });
    const [comments, setComments] = useState({ content: [], totalPages: 0 });
    const [postsPage, setPostsPage] = useState(1);
    const [commentsPage, setCommentsPage] = useState(1);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let isMounted = true;

        const fetchData = async () => {
            setLoading(true);
            try {
                const [postsResponse, commentsResponse] = await Promise.all([
                    api.get(`/admin/member/${memberId}/boards`, { params: { page: postsPage, size: 10 } }),
                    api.get(`/admin/member/${memberId}/comments`, { params: { page: commentsPage, size: 10 } })
                ]);

                console.log('Posts Response:', postsResponse.data);
                console.log('Comments Response:', commentsResponse.data);

                if (isMounted) {
                    setPosts({
                        content: postsResponse.data.content || [],
                        totalPages: postsResponse.data.totalPages || 0
                    });
                    setComments({
                        content: commentsResponse.data.content || [],
                        totalPages: commentsResponse.data.totalPages || 0
                    });
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        };

        fetchData().catch(error => {
            console.error('Error in fetchData:', error);
            if (isMounted) {
                setLoading(false);
            }
        });

        return () => {
            isMounted = false;
        };
    }, [memberId, postsPage, commentsPage]);

    const formatDate = (dateString) => {
        if (!dateString) return '';
        try {
            return new Date(dateString).toLocaleString();
        } catch (error) {
            console.error('Date formatting error:', error);
            return '';
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ maxWidth: 800, margin: '0 auto', p: 2 }}>
            <Paper sx={{ mb: 2, p: 2 }}>
                <Typography variant="h5" sx={{ mb: 2 }}>게시글 목록</Typography>
                <Divider sx={{ mb: 2 }} />
                <List>
                    {posts.content && posts.content.length > 0 ? (
                        posts.content.map((post) => (
                            <ListItem key={post?.boardId || Math.random()} sx={{ display: 'block', mb: 2 }}>
                                <Typography variant="h6">{post?.title || '제목 없음'}</Typography>
                                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                    작성일: {formatDate(post?.createdAt)} |
                                    작성자: {post?.nickname || '알 수 없음'}
                                </Typography>
                                <Typography variant="body1" sx={{ mb: 1 }}>
                                    {post?.content?.substring(0, 100) || '내용 없음'}
                                    {post?.content?.length > 100 ? '...' : ''}
                                </Typography>
                                <Divider />
                            </ListItem>
                        ))
                    ) : (
                        <ListItem>
                            <ListItemText primary="작성한 게시글이 없습니다." />
                        </ListItem>
                    )}
                </List>
                {posts.totalPages > 1 && (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                        <Pagination
                            count={posts.totalPages}
                            page={postsPage}
                            onChange={(event, value) => setPostsPage(value)}
                            color="primary"
                        />
                    </Box>
                )}
            </Paper>

            <Paper sx={{ p: 2 }}>
                <Typography variant="h5" sx={{ mb: 2 }}>댓글 목록</Typography>
                <Divider sx={{ mb: 2 }} />
                <List>
                    {comments.content && comments.content.length > 0 ? (
                        comments.content.map((comment) => (
                            <ListItem key={comment?.commentId || Math.random()} sx={{ display: 'block', mb: 2 }}>
                                <Typography variant="body1" sx={{ mb: 1 }}>
                                    {comment?.content || '내용 없음'}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                    작성일: {formatDate(comment?.createdAt)} |
                                    원글 ID: {comment?.boardId || '알 수 없음'}
                                </Typography>
                                <Divider sx={{ mt: 1 }} />
                            </ListItem>
                        ))
                    ) : (
                        <ListItem>
                            <ListItemText primary="작성한 댓글이 없습니다." />
                        </ListItem>
                    )}
                </List>
                {comments.totalPages > 1 && (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                        <Pagination
                            count={comments.totalPages}
                            page={commentsPage}
                            onChange={(event, value) => setCommentsPage(value)}
                            color="primary"
                        />
                    </Box>
                )}
            </Paper>
        </Box>
    );
};

export default AdminMemberPostsAndComments;
