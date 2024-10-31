import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Container, Typography, Button, Paper } from '@mui/material';

const WordLearningPage = () => {
    const navigate = useNavigate();

    const handleDictionaryPage = () => {
        navigate('/word-management'); // 단어사전 페이지로 이동
    };

    const handleLearningPage = () => {
        navigate('/word-learning/languageSelect'); // 단어학습 언어선택 페이지로 이동
    };

    const handleTestPage = () => {
        navigate('/word-test/languageSelect'); // 단어시험 언어선택 페이지로 이동
    };

    return (
        <Container>
            {/* 배너 영역 */}
            <Box sx={{
                backgroundColor: '#007bff',
                color: 'white',
                padding: 2,
                textAlign: 'center',
                marginBottom: 0,
                boxShadow: 3
            }}>
                훈민정음 2.0
            </Box>

            {/* 홈 버튼 */}
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 2 }}>
                <Button variant="outlined" color="primary" onClick={() => navigate('/')} sx={{ marginBottom: 2 }}>
                    홈
                </Button>
            </Box>

            {/* 단어사전, 단어 학습, 단어 시험 버튼을 포함한 페이퍼들 */}
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                {/* 단어사전 */}
                <Paper sx={{ width: '60%', padding: 4.0, margin: 4.0, textAlign: 'left', boxShadow: 3 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        fullWidth
                        onClick={handleDictionaryPage}
                        sx={{ marginBottom: 1, fontSize: '1.5rem' }}
                    >
                        단어사전
                    </Button>
                    <Typography variant="body1" sx={{ marginTop: 1, lineHeight: 1.6 }}>
                        영어, 일본어, 중국어, 베트남어, 프랑스어
                    </Typography>
                    <Typography variant="body1" sx={{ marginTop: 1, lineHeight: 1.6 }}>
                        5개의 언어로 제공되며, 한국어 단어의 뜻과 정의를 학습할 수 있습니다.
                    </Typography>
                </Paper>

                {/* 단어 학습 */}
                <Paper sx={{ width: '60%', padding: 4.0, margin: 4.0, textAlign: 'left', boxShadow: 3 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        fullWidth
                        onClick={handleLearningPage}
                        sx={{ marginBottom: 2, fontSize: '1.5rem' }}
                    >
                        단어 학습
                    </Button>
                    <Typography variant="body1" sx={{ marginTop: 1, lineHeight: 1.6 }}>
                        정해진 시간 동안 한국어 단어와 선택한 언어의 단어가 랜덤으로 표시됩니다.
                    </Typography>
                    <Typography variant="body1" sx={{ marginTop: 1, lineHeight: 1.6 }}>
                        각 단어의 의미를 생각해보고 일치했는지 확인해보세요.
                    </Typography>
                </Paper>

                {/* 단어 시험 */}
                <Paper sx={{ width: '60%', padding: 4, margin: 4, textAlign: 'left', boxShadow: 3 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        fullWidth
                        onClick={handleTestPage}
                        sx={{ marginBottom: 3, fontSize: '1.5rem' }}
                    >
                        단어 시험
                    </Button>
                    <Typography variant="body1" sx={{ marginTop: 1, lineHeight: 1.6 }}>
                        정해진 시간 동안 선택한 언어로 주어지는 단어를 한국어로 번역하세요.
                    </Typography>
                    <Typography variant="body1" sx={{ marginTop: 1, lineHeight: 1.6 }}>
                        자신의 점수를 확인하고 실력을 평가해 보세요.
                    </Typography>
                </Paper>
            </Box>
        </Container>
    );
};

export default WordLearningPage;