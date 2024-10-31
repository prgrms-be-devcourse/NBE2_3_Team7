import React, { useState } from 'react';
import { Box, Container, Typography, Button, TextField, Select, MenuItem, FormControl, InputLabel, Alert } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import api from '../axios';

const WordViewPage = () => {
    const [title, setTitle] = useState('');
    const [lang, setLang] = useState('');
    const [wordData, setWordData] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');

        try {
            // API 호출 시 URL을 `${title}/${lang}` 형식으로 변경
            const response = await api.get(`/words/join/${title}/${lang}`);
            setWordData(response.data);
        } catch (err) {
            setError('단어를 찾을 수 없습니다.');
            setWordData(null);
        }
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
                단어 검색
            </Box>

            <form onSubmit={handleSubmit}>
                <TextField
                    label="검색할 단어 입력"
                    variant="outlined"
                    fullWidth
                    required
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    sx={{ marginBottom: 2 }}
                />

                <FormControl fullWidth required sx={{ marginBottom: 2 }}>
                    <InputLabel>언어 선택</InputLabel>
                    <Select
                        value={lang}
                        onChange={(e) => setLang(e.target.value)}
                        label="언어 선택"
                    >
                        <MenuItem value="">
                            <em>언어 선택</em>
                        </MenuItem>
                        <MenuItem value="영어">영어</MenuItem>
                        <MenuItem value="일본어">일본어</MenuItem>
                        <MenuItem value="중국어">중국어</MenuItem>
                        <MenuItem value="베트남어">베트남어</MenuItem>
                        <MenuItem value="프랑스어">프랑스어</MenuItem>
                    </Select>
                </FormControl>

                <Button type="submit" variant="contained" color="primary">
                    조회
                </Button>
            </form>

            {error && <Alert severity="error" sx={{ marginTop: 2 }}>{error}</Alert>}

            {wordData && (
                <Box sx={{ marginTop: 3, padding: 2, border: '1px solid #ccc', borderRadius: 2 }}>
                    <Typography variant="h5">{wordData.title}</Typography>
                    <Typography variant="body1">뜻: {wordData.translation}</Typography>
                    <Typography variant="body1">정의: {wordData.definition}</Typography>
                    <Typography variant="body1">언어: {wordData.lang}</Typography>
                    <Typography variant="body1">작성일: {new Date(wordData.createdAt).toLocaleDateString()}</Typography>
                    <Typography variant="body1">수정일: {new Date(wordData.updatedAt).toLocaleDateString()}</Typography>

                    <Box sx={{ marginTop: 2 }}>
                        <Button component={Link} to="/word-management" variant="contained" color="primary" sx={{ marginRight: 1 }}>
                            사전으로 돌아가기
                        </Button>
                    </Box>
                </Box>
            )}
        </Container>
    );
};

export default WordViewPage;
