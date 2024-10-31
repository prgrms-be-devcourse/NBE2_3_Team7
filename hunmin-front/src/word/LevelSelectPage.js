import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Box, Container, Typography, Button } from '@mui/material';

const LevelSelectPage = () => {
    const navigate = useNavigate();
    const location = useLocation();

    // URL에서 선택된 언어 가져오기
    const queryParams = new URLSearchParams(location.search);
    const selectedLanguage = queryParams.get('lang');

    const handleStartLearning = (level) => {
        // 레벨에 따라 학습 시작 페이지로 이동하는 로직 추가
        console.log(`Starting learning in ${selectedLanguage} at Level ${level}`);
        navigate(`/word-learning/start?lang=${selectedLanguage}&level=${level}`);
    };

    return (
        <Container>
            {/* 배너 영역 */}
            <Box sx={{
                backgroundColor: '#007bff',
                color: 'white',
                padding: 2,
                textAlign: 'center',
                marginBottom: 3,
                boxShadow: 3
            }}>
                {selectedLanguage} - 레벨 선택
            </Box>

            {/* 본문 컨테이너 */}
            <Box sx={{
                maxWidth: '500px',
                margin: '0 auto',
                padding: 3,
                backgroundColor: '#ffffff',
                borderRadius: 2,
                boxShadow: 3,
                textAlign: 'center'
            }}>
                <Typography variant="h5" sx={{ marginBottom: 3, fontWeight: 'bold', color: '#007bff' }}>
                    레벨을 선택해주세요
                </Typography>

                {/* 레벨 선택 버튼들 */}
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ marginBottom: 2 }}
                    onClick={() => handleStartLearning(1)}
                >
                    Level 1 Easy
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    sx={{ marginBottom: 2 }}
                    onClick={() => handleStartLearning(2)}
                >
                    Level 2 Normal
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={() => handleStartLearning(3)}
                >
                    Level 3 Hard
                </Button>
            </Box>
        </Container>
    );
};

export default LevelSelectPage;
