import React, { useEffect, useState } from 'react';
import { Box, Button, Container, Typography, Card, CardContent } from '@mui/material';
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

    const getTimeForLevel = (level) => {
        switch (level) {
            case '1': return 30;
            case '2': return 20;
            case '3': return 10;
            default: return 30;
        }
    };

    useEffect(() => {
        const fetchWords = async () => {
            try {
                const response = await api.get(`/words/learning/start`, {
                    params: { lang, level },
                });
                setWords(response.data.words);
                const initialTimeLeft = getTimeForLevel(level);
                setTimeLeft(initialTimeLeft);
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
            setShowTranslation(true);
            setShowEndMessage(true);
        }
    }, [timeLeft, showEndMessage]);

    const handleRetry = () => {
        window.location.reload();
    };

    const getRandomWordDisplay = (word) => {
        if (word.displayWord && word.displayTranslation) {
            return Math.random() < 0.5 ? word.displayWord : word.displayTranslation;
        }
        return word.displayWord || word.displayTranslation || "";
    };

    useEffect(() => {
        if (words.length > 0) {
            const newFixedDisplays = words.map((word) => getRandomWordDisplay(word));
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
                    <Card key={index} sx={{ marginBottom: 2, boxShadow: 3 }}>
                        <CardContent>
                            <Typography variant="h5" fontWeight="bold">{fixedWordDisplays[index]}</Typography>
                            {showTranslation && (
                                <Box sx={{ marginTop: 1 }}>
                                    <Typography variant="h6" color="textSecondary">번역: {word.displayTitle}</Typography>
                                    <Typography variant="h6" color="textSecondary">정의: {word.definition}</Typography>
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                ))}
            </Box>
        </Container>
    );
};

export default LearningPage;