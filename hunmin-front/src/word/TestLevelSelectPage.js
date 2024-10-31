import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Box, Container, Typography, Button } from '@mui/material';

const TestLevelSelectPage = () => {
    const navigate = useNavigate();
    const location = useLocation();

    // URL에서 선택된 언어 가져오기
    const queryParams = new URLSearchParams(location.search);
    const selectedLanguage = queryParams.get('lang');

    const handleStartTest = (level) => {
        console.log(`Starting test in ${selectedLanguage} at Level ${level}`);
        navigate(`/word-test/start?lang=${selectedLanguage}&level=${level}`);
    };

    const handleTestPage = () => {
        navigate('/word-test/languageSelect'); // 단어시험 언어선택 페이지로 이동
    };

    return (
        <Container>
            {/* 배너 영역 */}
            <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                backgroundColor: '#007bff',
                color: 'white',
                padding: 3,
                marginBottom: 4,
                boxShadow: 3,
            }}>
                <Box sx={{ flex: 1, textAlign: 'center' }}>
                    <Typography sx={{ fontWeight: 'bold', fontSize: '1.8rem' }}>
                        {selectedLanguage} - 레벨 선택
                    </Typography>
                </Box>
                <Button
                    variant="outlined"
                    color="default" // 버튼 색상 변경
                    onClick={handleTestPage}
                    sx={{ marginLeft: 2, color: 'white', borderColor: 'white' }} // 글씨와 테두리 색상 변경
                >
                    돌아가기
                </Button>
            </Box>

            {/* 본문 컨테이너 */}
            <Box sx={{
                maxWidth: '600px',
                margin: '0 auto',
                padding: 4,
                backgroundColor: '#ffffff',
                borderRadius: 2,
                boxShadow: 3,
                textAlign: 'center'
            }}>
                <Typography variant="h4" sx={{ marginBottom: 4, fontWeight: 'bold', color: '#007bff', fontSize: '1.5rem' }}>
                    레벨을 선택해주세요
                </Typography>

                {/* 레벨 선택 버튼들을 가로로 배치 */}
                <Box sx={{ display: 'flex', justifyContent: 'space-between', marginBottom: 3 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleStartTest(1)}
                        sx={{ flex: 1, marginRight: 1, fontSize: '1.2rem' }}
                    >
                        Level 1
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleStartTest(2)}
                        sx={{ flex: 1, marginRight: 1, fontSize: '1.2rem' }}
                    >
                        Level 2
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleStartTest(3)}
                        sx={{ flex: 1, fontSize: '1.2rem' }}
                    >
                        Level 3
                    </Button>
                </Box>

                {/* 설명란 */}
                <Typography variant="body1" sx={{ marginTop: 4, textAlign: 'left', fontSize: '1.2rem' }}>
                    정답은 해당 서비스의 단어사전을 기준으로 채점합니다.<br />
                    시간은 10분이 주어집니다.<br /><br />
                    <strong>Level 1:</strong> 25문제 <span style={{ color: 'red' }}>(랭킹 점수에 패널티 20% 반영)</span><br />
                    <strong>Level 2:</strong> 50문제 <span style={{ color: 'red' }}>(랭킹 점수에 패널티 10% 반영)</span><br />
                    <strong>Level 3:</strong> 100문제 <span style={{ color: 'red' }}>(랭킹 점수에 패널티 없음)</span><br /><br />
                    종료 후에는 제출을 하셔야 결과를 확인하실 수 있습니다.
                </Typography>
            </Box>
        </Container>
    );
};

export default TestLevelSelectPage;