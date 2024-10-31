import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Container, Typography, Button } from '@mui/material';

const LanguageSelectPage = () => {
    const navigate = useNavigate();

    const handleLanguageSelect = (language) => {
        navigate(`/word-learning/levelSelect?lang=${language}`); // 레벨 선택 페이지로 이동
    };

    return (
        <Container>
            {/* 본문 컨테이너 */}
            <Box sx={{
                maxWidth: '500px',
                margin: '0 auto',
                padding: 3,
                backgroundColor: '#ffffff',
                borderRadius: 2,
                boxShadow: 3,
                textAlign: 'center',
                marginTop: '50px' // 위쪽 공간 추가
            }}>
                <Typography variant="h5" sx={{ marginBottom: 4, fontWeight: 'bold', color: '#007bff' }}>
                    언어를 선택하세요
                </Typography>

                {/* 언어 선택 버튼들 */}
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ marginBottom: 2 }}
                    onClick={() => handleLanguageSelect('영어')}
                >
                    영어 (English)
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ marginBottom: 2 }}
                    onClick={() => handleLanguageSelect('일본어')}
                >
                    일본어 (日本語)
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ marginBottom: 2 }}
                    onClick={() => handleLanguageSelect('중국어')}
                >
                    중국어 (中文)
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ marginBottom: 2 }}
                    onClick={() => handleLanguageSelect('베트남어')}
                >
                    베트남어 (Tiếng Việt)
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={() => handleLanguageSelect('프랑스어')}
                >
                    프랑스어 (Français)
                </Button>

                {/* 뒤로가기 버튼 */}
                <Button variant="outlined" color="primary" onClick={() => navigate('/word-learning')} sx={{ marginTop: 3 }}>
                    뒤로가기
                </Button>
            </Box>
        </Container>
    );
};

export default LanguageSelectPage;