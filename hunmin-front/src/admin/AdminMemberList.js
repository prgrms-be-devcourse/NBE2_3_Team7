import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../axios';
import { Box, Typography, List, ListItem, ListItemText, Pagination, Paper, TextField, Button, CircularProgress } from '@mui/material';

const AdminMembersList = () => {
    const [members, setMembers] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResult, setSearchResult] = useState(null);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!searchQuery) {
            const fetchMembers = async () => {
                setLoading(true);
                try {
                    const response = await api.get('/admin/members', {
                        params: { page, size: 10 },
                    });
                    setMembers(response.data.content);
                    setTotalPages(response.data.totalPages);
                } catch (error) {
                    console.error('Error fetching members:', error);
                } finally {
                    setLoading(false);
                }
            };
            fetchMembers();
        }
    }, [page, searchQuery]);

    const handleSearchChange = (event) => {
        setSearchQuery(event.target.value);
    };

    const handleSearchSubmit = async () => {
        if (!searchQuery) return;
        setLoading(true);
        try {
            const response = await api.get(`/admin/member/nickname/${searchQuery}`);
            setSearchResult(response.data);
        } catch (error) {
            console.error('Error fetching member:', error);
            setSearchResult(null);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
            <Paper sx={{ p: 3, mb: 3 }}>
                <Typography variant="h4" sx={{ mb: 3 }}>회원 목록 및 검색</Typography>
                <Box sx={{ display: 'flex', mb: 3 }}>
                    <TextField
                        label="닉네임으로 검색"
                        variant="outlined"
                        value={searchQuery}
                        onChange={handleSearchChange}
                        sx={{ flexGrow: 1, mr: 2 }}
                    />
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSearchSubmit}
                        disabled={loading}
                    >
                        검색
                    </Button>
                </Box>
                {loading ? (
                    <CircularProgress />
                ) : (
                    <>
                        {searchQuery && searchResult ? (
                            <List>
                                <ListItem
                                    key={searchResult.memberId}
                                    component={Link}
                                    to={`/admin/member/${searchResult.memberId}`}
                                    sx={{
                                        '&:hover': {
                                            backgroundColor: 'rgba(0, 0, 0, 0.04)',
                                        },
                                        transition: 'background-color 0.3s',
                                        borderRadius: '8px',
                                        mb: 1,
                                        padding: '10px',
                                        textDecoration: 'none',
                                        color: 'inherit'
                                    }}
                                >
                                    <ListItemText
                                        primary={
                                            <Typography variant="h6" component="span">
                                                {searchResult.nickname}
                                            </Typography>
                                        }
                                        secondary={
                                            <Typography variant="body2" color="textSecondary">
                                               이메일: {searchResult.email} — 게시글: {searchResult.boardCount} | 댓글: {searchResult.commentCount}
                                            </Typography>
                                        }
                                    />
                                </ListItem>
                            </List>
                        ) : (
                            <List>
                                {members.map(member => (
                                    <ListItem
                                        key={member.memberId}
                                        component={Link}
                                        to={`/admin/member/${member.memberId}`}
                                        sx={{
                                            '&:hover': {
                                                backgroundColor: 'rgba(0, 0, 0, 0.04)',
                                            },
                                            transition: 'background-color 0.3s',
                                            borderRadius: '8px',
                                            mb: 1,
                                            padding: '10px',
                                            textDecoration: 'none',
                                            color: 'inherit'
                                        }}
                                    >
                                        <ListItemText
                                            primary={
                                                <Typography variant="h6" component="span">
                                                    {member.nickname}
                                                </Typography>
                                            }
                                            secondary={
                                                <Typography variant="body2" color="textSecondary">
                                                    이메일: {member.email} — 게시글: {member.boardCount} | 댓글: {member.commentCount}
                                                </Typography>
                                            }
                                        />
                                    </ListItem>
                                ))}
                            </List>
                        )}
                        {totalPages > 1 && !searchQuery && (
                            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                                <Pagination
                                    count={totalPages}
                                    page={page}
                                    onChange={(event, value) => setPage(value)}
                                    color="primary"
                                    variant="outlined"
                                    shape="rounded"
                                />
                            </Box>
                        )}
                    </>
                )}
            </Paper>
        </Box>
    );
};

export default AdminMembersList;


