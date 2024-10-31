import React, { useState, useEffect } from 'react';
import { Box, Container, Typography, Button, List, ListItem, ListItemText, Pagination } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import api from '../axios';

const WordListPage = () => {
    const [lang, setLang] = useState('');
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [words, setWords] = useState([]);
    const [wordData, setWordData] = useState(null);
    const [selectedWord, setSelectedWord] = useState(null);

    const location = useLocation();

    const fetchWords = async (selectedLang, currentPage) => {
        try {
            const params = {
                page: currentPage,
                size: 10,
                lang: selectedLang,
            };

            const response = await api.get('/words', { params });
            setWords(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('단어를 가져오는 중 오류 발생:', error);
        }
    };

    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const language = queryParams.get('lang');
        if (language) {
            setLang(language);
            fetchWords(language, page);
        }
    }, [location.search, page]);

    const handleWordClick = async (word) => {
        // 같은 단어를 다시 클릭하면 정보를 숨김
        if (selectedWord === word.title) {
            setSelectedWord(null);
            setWordData(null);
        } else {
            setSelectedWord(word.title);
            try {
                const response = await api.get(`/words/join/${word.title}/${word.lang}`);
                setWordData(response.data);
            } catch (err) {
                console.error('단어 정보를 가져오는 중 오류 발생:', err);
            }
        }
    };

    const handlePageChange = (event, value) => {
        setPage(value);
        fetchWords(lang, value);
    };

    return (
        <Container maxWidth="md">
            <Box sx={{
                backgroundColor: '#007bff',
                color: 'white',
                padding: 2,
                textAlign: 'center',
                marginBottom: 3,
                boxShadow: 3
            }}>
                단어 목록
            </Box>

            {lang && (
                <Box sx={{ marginBottom: 3 }}>
                    <Typography variant="h6" color="textSecondary" align="center" sx={{ marginBottom: 2 }}>
                        언어: {lang}
                    </Typography>
                </Box>
            )}

            {lang && (
                <Box sx={{
                    backgroundColor: '#ffffff',
                    padding: 3,
                    borderRadius: 2,
                    boxShadow: 3,
                }}>
                    <Typography variant="h5" color="primary" fontWeight="bold" gutterBottom>
                        단어 목록
                    </Typography>

                    <List>
                        {words.length > 0 ? words.map((word) => (
                            <div key={word.title}>
                                <ListItem onClick={() => handleWordClick(word)} sx={{ cursor: 'pointer' }}>
                                    <ListItemText
                                        primary={word.title}
                                        secondary={word.translation}
                                    />
                                </ListItem>

                                {/* 클릭한 단어의 정보 표시 부분 */}
                                {selectedWord === word.title && wordData && (
                                    <Box sx={{ padding: 2, border: '1px solid #ccc', borderRadius: 2 }}>
                                        <Typography variant="h5">{wordData.title}</Typography>
                                        <Typography variant="body1">뜻: {wordData.translation}</Typography>
                                        <Typography variant="body1">정의: {wordData.definition}</Typography>
                                        <Typography variant="body1">언어: {wordData.lang}</Typography>
                                        <Typography variant="body1">작성일: {new Date(wordData.createdAt).toLocaleDateString()}</Typography>
                                        <Typography variant="body1">
                                            수정일: {wordData.updatedAt ? new Date(wordData.updatedAt).toLocaleDateString() : ''}
                                        </Typography>
                                    </Box>
                                )}
                            </div>
                        )) : (
                            <Typography variant="body1" color="textSecondary">
                                해당 언어에 대한 단어가 없습니다.
                            </Typography>
                        )}
                    </List>
                </Box>
            )}

            {lang && (
                <Box sx={{ display: 'flex', justifyContent: 'center', marginTop: 3 }}>
                    <Pagination
                        count={totalPages}
                        page={page}
                        onChange={handlePageChange}
                        color="primary"
                    />
                </Box>
            )}

            <Box sx={{ textAlign: 'center', marginTop: 3 }}>
                <Button component={Link} to="/word-management" variant="contained" color="primary" sx={{ marginRight: 1 }}>
                    사전으로 돌아가기
                </Button>
                <Button component={Link} to="/word-view" variant="outlined" sx={{ marginRight: 1 }}>
                    단어 검색
                </Button>
                <Button component={Link} to="/word-edit" variant="outlined" sx={{ marginLeft: 1, color: 'red' }}>
                    단어 관리 (관리자 전용)
                </Button>
            </Box>
        </Container>
    );
};

export default WordListPage;