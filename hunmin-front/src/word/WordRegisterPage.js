import React, { useState } from 'react';
import {
    Box,
    Container,
    Typography,
    Button,
    TextField,
    Alert,
    InputLabel,
    Select,
    MenuItem,
    FormControl
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import api from '../axios'; // axios 인스턴스 import

const WordRegisterPage = () => {
    const [title, setTitle] = useState('');
    const [lang, setLang] = useState('');
    const [translation, setTranslation] = useState('');
    const [definition, setDefinition] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [registeredWord, setRegisteredWord] = useState(null); // 등록된 단어 상태 추가
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setErrorMessage('');

        // localStorage에서 memberId 가져오기
        const memberId = localStorage.getItem('memberId');

        if (!memberId) {
            setErrorMessage('사용자 정보가 없습니다. 다시 로그인해 주세요.');
            return;
        }

        try {
            // wordRequestDTO 객체 생성
            const wordRequestDTO = {
                title: title,
                lang: lang,
                translation: translation,
                definition: definition,
                memberId: memberId, // memberId를 Number 타입으로 변환하여 추가
            };

            // 단어 등록 API 호출
            const response = await api.post('/words', wordRequestDTO);
            setRegisteredWord(response.data); // 등록된 단어 상태 업데이트
            // 성공 시 홈으로 리다이렉트
            // navigate('/word-management'); // 필요 시 주석 해제
        } catch (error) {
            setErrorMessage('등록 중 오류가 발생했습니다. 다시 시도해 주세요.');
        }
    };

    return (
        <Container maxWidth="sm" sx={{ marginTop: 4 }}>
            <Typography variant="h4" gutterBottom>
                단어 등록
            </Typography>

            {errorMessage && <Alert severity="error">{errorMessage}</Alert>}

            <form onSubmit={handleSubmit}>
                <Box sx={{ marginBottom: 2 }}>
                    <TextField
                        fullWidth
                        required
                        label="단어"
                        variant="outlined"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                </Box>

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

                <Box sx={{ marginBottom: 2 }}>
                    <TextField
                        fullWidth
                        label="번역"
                        variant="outlined"
                        value={translation}
                        onChange={(e) => setTranslation(e.target.value)}
                    />
                </Box>

                <Box sx={{ marginBottom: 2 }}>
                    <TextField
                        fullWidth
                        label="정의"
                        variant="outlined"
                        value={definition}
                        onChange={(e) => setDefinition(e.target.value)}
                    />
                </Box>

                <Button type="submit" variant="contained" color="primary" fullWidth>
                    등록
                </Button>
            </form>

            {/* 등록된 단어 표시 */}
            {registeredWord && (
                <Box sx={{ marginTop: 4, padding: 2, border: '1px solid #ccc', borderRadius: '4px' }}>
                    <Typography variant="h6">등록된 단어:</Typography>
                    <Typography variant="body1"><strong>단어:</strong> {registeredWord.title}</Typography>
                    <Typography variant="body1"><strong>언어:</strong> {registeredWord.lang}</Typography>
                    <Typography variant="body1"><strong>번역:</strong> {registeredWord.translation}</Typography>
                    <Typography variant="body1"><strong>정의:</strong> {registeredWord.definition}</Typography>
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

export default WordRegisterPage;
