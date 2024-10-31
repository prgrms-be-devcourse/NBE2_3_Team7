import React, { useState } from 'react';
import { Box, Container, Typography, Button, TextField, Select, MenuItem, FormControl, InputLabel, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import api from '../axios';

const WordEditPage = () => {
    const [title, setTitle] = useState('');
    const [lang, setLang] = useState('');
    const [originalTitle, setOriginalTitle] = useState(''); // 수정 전 title 저장
    const [originalLang, setOriginalLang] = useState(''); // 수정 전 lang 저장
    const [wordData, setWordData] = useState(null);
    const [translation, setTranslation] = useState('');
    const [definition, setDefinition] = useState('');
    const [error, setError] = useState('');
    const [updatedWordData, setUpdatedWordData] = useState(null); // 업데이트된 단어 데이터를 저장할 상태
    const navigate = useNavigate();

    const handleSearch = async (event) => {
        event.preventDefault();
        setError('');

        try {
            const response = await api.get(`/words/join/${title}/${lang}`); // API 호출
            const fetchedWordData = response.data;

            // 검색된 단어 데이터로 상태 업데이트
            setWordData(fetchedWordData);
            setTranslation(fetchedWordData.translation);
            setDefinition(fetchedWordData.definition);
            // 수정 전의 title과 lang 값을 설정
            setOriginalTitle(fetchedWordData.title);
            setOriginalLang(fetchedWordData.lang);
        } catch (err) {
            setError('단어를 찾을 수 없습니다.');
            setWordData(null);
        }
    };

    const memberId = localStorage.getItem('memberId');

    const handleUpdate = async (event) => {
        event.preventDefault();
        setError('');

        if (!wordData) {
            setError('단어 데이터가 없습니다.');
            return;
        }

        try {
            const updatedWordData = {
                memberId: memberId,
                wordId: wordData.wordId, // wordData에서 wordId 가져오기
                title,
                lang,
                translation,
                definition,
            };

            // 수정 전 title과 lang을 URL 파라미터로 사용
            const response = await api.put(`/words/update/${originalTitle}/${originalLang}`, updatedWordData);
            setUpdatedWordData(response.data); // 업데이트된 단어 데이터를 상태에 저장
        } catch (err) {
            setError('단어 수정 중 오류가 발생했습니다.');
        }
    };

    const handleDelete = async () => {
        setError('');
        try {
            await api.delete(`/words/delete/${originalTitle}/${originalLang}`);
            setWordData(null); // 삭제 후 단어 데이터 초기화
            setUpdatedWordData(null); // 수정된 단어 데이터 초기화
            alert('단어가 삭제되었습니다.');
        } catch (err) {
            setError('단어 삭제 중 오류가 발생했습니다.');
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
                단어 검색 및 수정
            </Box>

            <form onSubmit={handleSearch}>
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
                    <Typography variant="h5">현재 단어: {wordData.title}</Typography>

                    <TextField
                        label="수정할 단어"
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

                    <TextField
                        label="번역"
                        variant="outlined"
                        fullWidth
                        required
                        multiline
                        rows={4}
                        value={translation}
                        onChange={(e) => setTranslation(e.target.value)}
                        sx={{ marginBottom: 2 }}
                    />

                    <TextField
                        label="정의"
                        variant="outlined"
                        fullWidth
                        required
                        multiline
                        rows={4}
                        value={definition}
                        onChange={(e) => setDefinition(e.target.value)}
                        sx={{ marginBottom: 2 }}
                    />

                    <Button onClick={handleUpdate} variant="contained" color="primary">
                        수정하기
                    </Button>
                    <Button onClick={handleDelete} variant="contained" color="secondary" sx={{ marginLeft: 2 }}>
                        삭제하기
                    </Button>
                </Box>
            )}

            {/* 수정된 단어가 있을 경우 보여주는 부분 */}
            {updatedWordData && (
                <Box sx={{ marginTop: 3, padding: 2, border: '1px solid #4caf50', borderRadius: 2 }}>
                    <Typography variant="h5" color="green">수정된 단어</Typography>
                    <Typography variant="body1">단어: {updatedWordData.title}</Typography>
                    <Typography variant="body1">언어: {updatedWordData.lang}</Typography>
                    <Typography variant="body1">번역: {updatedWordData.translation}</Typography>
                    <Typography variant="body1">정의: {updatedWordData.definition}</Typography>
                </Box>
            )}

            <Button
                href="/word-management"
                variant="text"
                fullWidth
                sx={{ marginTop: 2 }}
            >
                사전으로 돌아가기
            </Button>
        </Container>
    );
};

export default WordEditPage;
