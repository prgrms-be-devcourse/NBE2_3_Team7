import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Container, Typography, Button, Table, TableHead, TableBody, TableRow, TableCell, Paper, Avatar } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';

const TestLanguageSelectPage = () => {
    const navigate = useNavigate();
    const [rankings, setRankings] = useState([]);
    const [filteredRankings, setFilteredRankings] = useState([]);
    const [selectedLang, setSelectedLang] = useState('전체');
    const [userScore, setUserScore] = useState(null);
    const [userInfo, setUserInfo] = useState(null);
    const memberId = localStorage.getItem('memberId');

    const handleTestLanguageSelect = (language) => {
        navigate(`/word-test/levelSelect?lang=${language}`);
    };

    useEffect(() => {
        const fetchRankings = async () => {
            try {
                const response = await fetch('/api/words/test/rankings');
                const data = await response.json();
                setRankings(data);
                setFilteredRankings(data);

                const userScores = data.filter(score => score.memberId === Number(memberId));
                if (userScores.length > 0) {
                    const highestScoreEntry = userScores.reduce((prev, current) =>
                        (prev.testScore > current.testScore) ? prev : current
                    );
                    setUserScore(highestScoreEntry.testScore);
                    setUserInfo(highestScoreEntry);
                }
                else {
                    setUserScore(null);
                    setUserInfo(null);
                }
            } catch (error) {
                console.error('Error fetching rankings:', error);
            }
        };

        fetchRankings();
    }, [memberId]);

    const filterRankings = (lang) => {
        setSelectedLang(lang);
        let userScores = [];

        if (lang === '전체') {
            userScores = rankings.filter(score => score.memberId === Number(memberId));
        } else {
            userScores = rankings.filter(score => score.testLang === lang && score.memberId === Number(memberId));
        }

        const highestScoreEntry = userScores.length > 0
            ? userScores.reduce((prev, current) => (prev.testScore > current.testScore ? prev : current))
            : null;

        setUserInfo(highestScoreEntry);

        if (lang === '전체') {
            setFilteredRankings(rankings);
        } else {
            setFilteredRankings(rankings.filter(score => score.testLang === lang));
        }
    };

    const topThreeRankings = filteredRankings.slice(0, 3);

    return (
        <Container>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', marginBottom: 2 }}>
                <Button color="inherit" onClick={() => navigate('/word-learning')} startIcon={<EditIcon />}>
                    단어 학습
                </Button>
                <Button variant="outlined" color="primary" onClick={() => navigate('/word-learning')}>
                    뒤로가기
                </Button>
            </Box>

            <Box sx={{
                maxWidth: '1200px',
                margin: '0 auto',
                padding: 3,
                backgroundColor: '#ffffff',
                borderRadius: 2,
                boxShadow: 3,
                textAlign: 'center'
            }}>
                <Typography variant="h5" sx={{ marginBottom: 3, fontWeight: 'bold', color: '#007bff' }}>
                    언어를 선택하세요
                </Typography>

                <Box sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', marginBottom: 2 }}>
                    {['영어', '일본어', '중국어', '베트남어', '프랑스어'].map((lang) => (
                        <Button
                            key={lang}
                            variant="contained"
                            color="primary"
                            sx={{ flex: '1 1 150px', margin: 1, display: 'flex', flexDirection: 'column' }}
                            onClick={() => handleTestLanguageSelect(lang)}
                        >
                            <Typography variant="body1">{lang}</Typography>
                            <Typography variant="body2">({getLanguageName(lang)})</Typography>
                        </Button>
                    ))}
                </Box>

                <Box sx={{ display: 'flex', justifyContent: 'flex-end', marginTop: 2 }}>
                    <Button variant="outlined" color="primary" onClick={() => navigate('/word-test/records')}>
                        시험 기록
                    </Button>
                </Box>
            </Box>

            <Paper elevation={3} sx={{ maxWidth: '1200px', margin: '20px auto', padding: '20px', position: 'relative', paddingBottom: '80px' }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold', marginBottom: 2, fontSize: '1.5rem', color: '#333', textAlign: 'center' }}>
                    단어시험 TOP100
                </Typography>

                <Box sx={{ display: 'flex', justifyContent: 'space-between', marginBottom: 2 }}>
                    {['전체', '영어', '일본어', '중국어', '베트남어', '프랑스어'].map((lang) => (
                        <Button
                            key={lang}
                            variant={selectedLang === lang ? 'contained' : 'outlined'}
                            color="primary"
                            onClick={() => filterRankings(lang)}
                            sx={{ flex: 1, margin: '0 5px', height: '48px' }}
                        >
                            {lang} 랭킹
                        </Button>
                    ))}
                </Box>

                <Box sx={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
                    {/* 2등 왼쪽 */}
                    {topThreeRankings[1] ? (
                        <Box key={topThreeRankings[1].wordScoreId} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginRight: '80px' }}>
                            <Box sx={{ borderRadius: '50%', border: '15px solid silver', width: '120px', height: '120px', display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 1 }}>
                                <Avatar src={topThreeRankings[1].profileImage} sx={{ width: 100, height: 100 }} />
                            </Box>
                            <Typography sx={{ fontSize: '1.5rem' }}>{topThreeRankings[1].nickName}</Typography>
                        </Box>
                    ) : (
                        <Box sx={{ width: '120px', height: '120px' }} />
                    )}

                    {/* 1등 중앙 */}
                    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', margin: '0 40px' }}>
                        {topThreeRankings[0] ? (
                            <>
                                <Box sx={{ borderRadius: '50%', border: '15px solid gold', width: '120px', height: '120px', display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 1 }}>
                                    <Avatar src={topThreeRankings[0].profileImage} sx={{ width: 100, height: 100 }} />
                                </Box>
                                <Typography sx={{ fontSize: '1.5rem' }}>{topThreeRankings[0].nickName}</Typography>
                            </>
                        ) : (
                            <Box sx={{ width: '120px', height: '120px' }} />
                        )}
                    </Box>

                    {/* 3등 오른쪽 */}
                    {topThreeRankings[2] ? (
                        <Box key={topThreeRankings[2].wordScoreId} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginLeft: '80px' }}>
                            <Box sx={{ borderRadius: '50%', border: '15px solid #cd7f32', width: '120px', height: '120px', display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: 1 }}>
                                <Avatar src={topThreeRankings[2].profileImage} sx={{ width: 100, height: 100 }} />
                            </Box>
                            <Typography sx={{ fontSize: '1.5rem' }}>{topThreeRankings[2].nickName}</Typography>
                        </Box>
                    ) : (
                        <Box sx={{ width: '120px', height: '120px' }} />
                    )}
                </Box>

                <Table sx={{ minWidth: 650 }}>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center" sx={{ fontSize: '1.1rem', fontWeight: 'bold' }}>순위</TableCell>
                            <TableCell align="center" sx={{ fontSize: '1.1rem', fontWeight: 'bold' }}>닉네임</TableCell>
                            <TableCell align="center" sx={{ fontSize: '1.1rem', fontWeight: 'bold' }}>언어</TableCell>
                            <TableCell align="center" sx={{ fontSize: '1.1rem', fontWeight: 'bold' }}>레벨</TableCell>
                            <TableCell align="center" sx={{ fontSize: '1.1rem', fontWeight: 'bold' }}>점수</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredRankings.length > 0 ? (
                            filteredRankings.map((wordScore, index) => (
                                <TableRow key={wordScore.wordScoreId}>
                                    <TableCell align="center" sx={{ fontSize: '1rem' }}>{index + 1}위</TableCell>
                                    <TableCell align="center" sx={{ fontSize: '1rem', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                        <Avatar src={wordScore.profileImage} sx={{ width: 30, height: 30, marginRight: '8px' }} />
                                        {wordScore.nickName}
                                    </TableCell>
                                    <TableCell align="center" sx={{ fontSize: '1rem' }}>{wordScore.testLang}</TableCell>
                                    <TableCell align="center" sx={{ fontSize: '1rem' }}>{wordScore.testLevel}</TableCell>
                                    <TableCell align="center" sx={{ fontSize: '1rem' }}>{wordScore.testRankScore}</TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={5} align="center">
                                    랭킹 정보가 없습니다.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>

                <Box sx={{
                    position: 'fixed',
                    bottom: 0,
                    left: 0,
                    right: 0,
                    backgroundColor: '#f5f5f5',
                    padding: 2,
                    display: 'flex',
                    justifyContent: 'space-between',
                    borderTop: '1px solid #ccc',
                    height: '80px',
                    marginLeft: '20px',
                    marginRight: '20px',
                }}>
                    <Typography variant="subtitle2" sx={{
                        position: 'absolute',
                        left: 20,
                        top: '50%',
                        transform: 'translateY(-50%)',
                        fontSize: '1.1rem',
                        fontWeight: 'bold',
                        backgroundColor: '#f5f5f5',
                        padding: '2px 5px',
                        borderRadius: '4px',
                    }}>
                        나의 순위
                    </Typography>
                    <Box sx={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center', // 중앙 정렬
                        width: '100%',
                        paddingLeft: '20px', // 좌우 패딩 조정
                        paddingRight: '20px', // 좌우 패딩 조정
                    }}>
                        {userInfo ? (
                            <>
                                <Box sx={{ display: 'flex', alignItems: 'center', marginRight: '10px' }}> {/* 오른쪽 여백 줄이기 */}
                                    <Avatar src={userInfo.profileImage} sx={{ width: 30, height: 30, marginRight: '10px' }} />
                                    <Typography variant="body1" sx={{ fontSize: '1.2rem' }}>{userInfo.nickName}</Typography>
                                </Box>
                                <Typography variant="body1" sx={{ fontSize: '1.2rem', width: '50px', textAlign: 'center', marginLeft: '140px' }}>
                                    {filteredRankings.findIndex(score => score.wordScoreId === userInfo.wordScoreId) + 1 || '-'}위
                                </Typography>
                                <Typography variant="body1" sx={{ fontSize: '1.2rem', width: '70px', textAlign: 'center', marginLeft: '140px', marginRight: '140px' }}>{userInfo.testLang}</Typography>
                                <Typography variant="body1" sx={{ fontSize: '1.2rem', width: '70px', textAlign: 'center', marginLeft: '20px', marginRight: '140px' }}>{userInfo.testLevel}</Typography>
                                <Typography variant="body1" sx={{ fontSize: '1.2rem', width: '70px', textAlign: 'center' }}>{userInfo.testRankScore}</Typography>
                            </>
                        ) : (
                            <Typography variant="body1" sx={{ fontSize: '1.2rem', textAlign: 'center', width: '100%' }}>
                                시험 정보가 없습니다.
                            </Typography>
                        )}
                    </Box>
                </Box>
            </Paper>
        </Container>
    );
};

// 언어 이름을 반환하는 헬퍼 함수
const getLanguageName = (lang) => {
    const languageNames = {
        '영어': 'English',
        '일본어': '日本語',
        '중국어': '中文',
        '베트남어': 'Tiếng Việt',
        '프랑스어': 'Français'
    };
    return languageNames[lang] || lang;
};

export default TestLanguageSelectPage;