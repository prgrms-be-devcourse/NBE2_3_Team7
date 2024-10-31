import React, { useEffect, useState } from 'react';
import { Box, Button, Container, Typography, Divider } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import api from '../axios';

const TestPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const selectedLanguage = queryParams.get('lang');
    const selectedLevel = queryParams.get('level');
    const memberId = localStorage.getItem('memberId');

    const [words, setWords] = useState([]);
    const [userAnswers, setUserAnswers] = useState([]);
    const [timer, setTimer] = useState(600);
    const [isTimeUp, setIsTimeUp] = useState(false);
    const [finalScore, setFinalScore] = useState(0);
    const [penaltyScore, setPenaltyScore] = useState(0);
    const [showResults, setShowResults] = useState(false);
    const [correctAnswers, setCorrectAnswers] = useState([]);
    const [correctCount, setCorrectCount] = useState(0);
    const [incorrectCount, setIncorrectCount] = useState(0);
    const [timerId, setTimerId] = useState(null);
    const [hasSubmitted, setHasSubmitted] = useState(false);

    useEffect(() => {
        fetchWords();
        startTimer();

        return () => clearInterval(timerId); // Clean up timer
    }, [selectedLanguage, selectedLevel]);

    const startTimer = () => {
        setTimer(600); // Reset timer to 10 minutes
        setIsTimeUp(false);

        if (timerId) {
            clearInterval(timerId);
        }

        const timerIntervalId = setInterval(() => {
            setTimer(prev => {
                if (prev <= 1) {
                    clearInterval(timerIntervalId);
                    setIsTimeUp(true);
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);

        setTimerId(timerIntervalId);
    };

    const fetchWords = async () => {
        try {
            const response = await api.get(`/words/test/start`, {
                params: { lang: selectedLanguage, level: selectedLevel }
            });

            console.log("Fetched words:", response.data); // 응답 로그 찍기

            setWords(response.data.words);
            setUserAnswers(new Array(response.data.words.length).fill(''));
            setShowResults(false);
        } catch (error) {
            console.error("Error fetching words:", error);
        }
    };

    const handleAnswerChange = (index, value) => {
        const newAnswers = [...userAnswers];
        newAnswers[index] = value;
        setUserAnswers(newAnswers);
    };

    const handleSubmit = async () => {
        if (hasSubmitted) return; // 한 번 제출된 경우 return

        let correctCountTemp = 0;
        const correctAnswers = [];

        for (let i = 0; i < words.length; i++) {
            const correctAnswer = words[i].displayTranslation;
            const userAnswer = userAnswers[i].trim();

            if (correctAnswer.toLowerCase() === userAnswer.toLowerCase()) {
                correctCountTemp++;
            }

            correctAnswers.push({ question: words[i].displayWord, userAnswer, correctAnswer });
        }

        try {
            const response = await api.post('/words/test/submit', {
                memberId,
                correctCount: correctCountTemp,
                testLang: selectedLanguage,
                testLevel: selectedLevel,
            });

            console.log("Submitting answers:", {
                memberId,
                correctCount: correctCountTemp,
                testLang: selectedLanguage,
                testLevel: selectedLevel,
            });

            setFinalScore(response.data.finalScore);
            setPenaltyScore(response.data.penaltyScore);

            console.log("Response data:", response.data);

            setCorrectAnswers(correctAnswers);
            setCorrectCount(correctCountTemp);
            setIncorrectCount(words.length - correctCountTemp);

            setShowResults(true);
            setHasSubmitted(true);
            setIsTimeUp(true); // 타이머 멈추기
            setTimer(0); // 타이머를 0으로 설정
            clearInterval(timerId); // 타이머 정지
        } catch (error) {
            console.error('Error submitting answers:', error);
        }
    };

    const handleRetry = () => {
        fetchWords(); // 단어를 다시 가져오기
        setUserAnswers(new Array(words.length).fill('')); // 사용자 답변 초기화
        setFinalScore(0); // 점수 초기화
        setPenaltyScore(0); // 랭킹 점수 초기화
        setCorrectAnswers([]); // 정답 초기화
        setCorrectCount(0); // 정답 개수 초기화
        setIncorrectCount(0); // 오답 개수 초기화
        startTimer(); // 타이머 시작
        setShowResults(false); // 결과 숨기기
        setHasSubmitted(false); // 제출 상태 초기화
    };

    return (
        <Container sx={{ paddingBottom: '80px' }}> {/* 하단 고정 바의 높이만큼 여백 추가 */}
            <Box sx={{ textAlign: 'center', marginBottom: 3, marginTop: 10 }}>
                <Typography variant="h5">{selectedLanguage} - Level {selectedLevel} Test</Typography>
            </Box>

            {showResults ? ( // 결과를 보여주기 위한 조건부 렌더링
                <Box sx={{ marginTop: 3, textAlign: 'center' }}>
                    <Typography variant="h5" sx={{ marginBottom: 1, fontSize: '2.0rem' }}>시험 결과</Typography>
                    <Typography variant="h6" sx={{ fontSize: '1.6rem' }}>시험 점수: {finalScore}점</Typography>
                    <Typography variant="h6" sx={{ fontSize: '1.6rem' }}>랭킹 점수: {penaltyScore}점</Typography>
                    <Typography variant="h6" sx={{ fontSize: '1.6rem' }}>정답 개수: {correctCount}개</Typography>
                    <Typography variant="h6" sx={{ fontSize: '1.6rem' }}>오답 개수: {incorrectCount}개</Typography>

                    <Box sx={{ marginTop: 3 }}>
                        <Typography variant="h6" align="left" sx={{ fontSize: '1.6rem' }}>정답 확인</Typography>
                        {correctAnswers.map((answer, index) => (
                            <Box key={index} sx={{ marginBottom: 4 }}>
                                <Divider sx={{ margin: '10px 0' }} />
                                <Typography variant="body1" align="left" sx={{ fontSize: '1.4rem' }}>문제 {index + 1}: {answer.question}</Typography>
                                <Typography variant="body2" align="left" sx={{ fontSize: '1.4rem' }}>입력한 답: {answer.userAnswer}</Typography>
                                <Typography variant="body2" align="left" sx={{ fontSize: '1.4rem' }}>정답: {answer.correctAnswer}</Typography>
                                <Typography variant="body2" align="left" sx={{ fontSize: '1.4rem' }} color={answer.userAnswer === answer.correctAnswer ? 'green' : 'red'}>
                                    {answer.userAnswer === answer.correctAnswer ? '정답입니다!' : '오답입니다!'}
                                </Typography>
                            </Box>
                        ))}
                    </Box>
                </Box>
            ) : ( // 문제 출력
                isTimeUp && !hasSubmitted ? (
                    <Typography variant="h4" align="center" sx={{ marginTop: '20%', fontWeight: 'bold' }}>시간 종료! 제출해주세요!</Typography>
                ) : (
                    words.length > 0 ? (
                        words.map((word, index) => (
                            <Box key={index} sx={{ marginBottom: 4 }}>
                                <Typography variant="body1" sx={{ fontSize: '1.2rem' }}>{index + 1}. {word.displayWord}</Typography>
                                <input
                                    type="text"
                                    value={userAnswers[index]}
                                    onChange={(e) => handleAnswerChange(index, e.target.value)}
                                    disabled={isTimeUp} // 타이머가 종료되면 입력 비활성화
                                    style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                                />
                            </Box>
                        ))
                    ) : (
                        <Typography>문제가 없습니다. 다시 시도해주세요.</Typography>
                    )
                )
            )}

            {/* 타이머 및 버튼 하단 고정 */}
            <Box sx={{ position: 'fixed', bottom: 0, left: 0, right: 0, backgroundColor: 'white', zIndex: 1000, padding: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <Button variant="contained" color="primary" onClick={() => navigate('/')}>
                        홈
                    </Button>
                    <Button variant="outlined" color="primary" onClick={() => navigate('/word-test/languageSelect')} sx={{ marginLeft: 1 }}>
                        목록
                    </Button>
                </Box>
                <Typography variant="h6" align="center" sx={{ margin: '0 20px' }}>
                    {isTimeUp ? '시간 종료' : `남은 시간: ${Math.floor(timer / 60)}:${timer % 60 < 10 ? `0${timer % 60}` : timer % 60}`}
                </Typography>
                <Box>
                    {!hasSubmitted && !isTimeUp && (
                        <Button variant="contained" color="secondary" onClick={handleSubmit} sx={{ marginRight: 2 }}>
                            제출
                        </Button>
                    )}
                    {isTimeUp && (
                        <Button variant="contained" color="secondary" onClick={handleSubmit} sx={{ marginRight: 2 }}>
                            제출
                        </Button>
                    )}
                    <Button variant="contained" color="primary" onClick={handleRetry}>
                        다시하기
                    </Button>
                </Box>
            </Box>
        </Container>
    );
};

export default TestPage;