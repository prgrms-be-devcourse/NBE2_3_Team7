import React, { useEffect, useState } from 'react';
import { Box, Button, Container, Typography } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import api from '../axios';

const LearningPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const lang = queryParams.get('lang');
    const level = queryParams.get('level');

    const [words, setWords] = useState([]);
    const [timeLeft, setTimeLeft] = useState(0);
    const [showTranslation, setShowTranslation] = useState(false);
    const [showEndMessage, setShowEndMessage] = useState(false);
    const [fixedWordDisplays, setFixedWordDisplays] = useState([]);

    // 레벨에 따라 시간을 설정하는 함수
    const getTimeForLevel = (level) => {
        switch (level) {
            case '1':
                return 30; // 30초
            case '2':
                return 20; // 20초
            case '3':
                return 10; // 10초
            default:
                return 30; // 기본값 30초
        }
    };

    useEffect(() => {
        const fetchWords = async () => {
            try {
                console.log(`Fetching words for lang: ${lang}, level: ${level}`); // 로그 추가
                const response = await api.get(`/words/learning/start`, {
                    params: { lang, level },
                });
                console.log("Response data:", response.data); // 응답 로그 추가
                setWords(response.data.words);
                const initialTimeLeft = getTimeForLevel(level); // 레벨에 따른 초기화
                setTimeLeft(initialTimeLeft); // 초기 시간 설정
                setShowTranslation(false);
                setShowEndMessage(false);
            } catch (error) {
                console.error("Error fetching words:", error);
            }
        };

        fetchWords();
    }, [lang, level]);

    useEffect(() => {
        if (timeLeft > 0) {
            const countdown = setInterval(() => {
                setTimeLeft(prevTime => prevTime - 1);
            }, 1000);

            return () => clearInterval(countdown);
        } else if (timeLeft === 0 && !showEndMessage) {
            console.log("Time's up!");
            setShowTranslation(true);
            setShowEndMessage(true);
        }
    }, [timeLeft, showEndMessage]);

    const handleRetry = () => {
        window.location.reload();
    };

    const getRandomWordDisplay = (word) => {
        return Math.random() < 0.5 ? word.displayWord : word.displayTranslation;
    };

    useEffect(() => {
        if (words.length > 0) {
            const newFixedDisplays = words.map((word) => {
                return getRandomWordDisplay(word);
            });
            setFixedWordDisplays(newFixedDisplays);
        }
    }, [words]);

    return (
        <Container>
            <Box sx={{ backgroundColor: '#007bff', color: 'white', padding: 2, textAlign: 'center', marginBottom: 3, boxShadow: 3 }}>
                단어 학습
            </Box>
            <Button variant="contained" color="primary" onClick={() => navigate('/word-learning')} sx={{ marginBottom: 2 }}>돌아가기</Button>
            {!showEndMessage ? (
                <Typography variant="h6" align="center" color="#007bff">남은 시간: {timeLeft}초</Typography>
            ) : (
                <Typography variant="h6" align="center" color="#007bff" marginTop={3}>시간이 종료되었습니다!</Typography>
            )}
            <Button variant="contained" color="primary" onClick={handleRetry} sx={{ display: showEndMessage ? 'block' : 'none', margin: '20px auto' }}>다시하기</Button>
            <Box className="word-list" marginTop={2}>
                {words.map((word, index) => (
                    <Box key={index} sx={{ padding: 2, borderBottom: '1px solid #ccc', transition: 'background-color 0.3s ease', '&:hover': { backgroundColor: '#f1f1f1' } }}>
                        <Typography variant="body1" fontWeight="bold">{fixedWordDisplays[index]}</Typography>
                        {showTranslation && (
                            <Box>
                                <Typography variant="body2" color="textSecondary">
                                    {fixedWordDisplays[index] === word.displayWord ? word.displayTranslation : word.displayWord}
                                </Typography>
                                <Typography variant="body2" color="textSecondary">{word.definition}</Typography>
                            </Box>
                        )}
                    </Box>
                ))}
            </Box>
        </Container>
    );
};

export default LearningPage;
