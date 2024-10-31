import React, { useEffect, useState } from 'react';
import { Box, Button, Typography, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const TestRecords = () => {
    const [testRecords, setTestRecords] = useState([]);
    const [filteredRecords, setFilteredRecords] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedLang, setSelectedLang] = useState('전체'); // 현재 선택된 언어 상태
    const memberId = localStorage.getItem('memberId');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchTestScores = async () => {
            try {
                const response = await axios.get(`/api/words/test/records?memberId=${memberId}`);
                setTestRecords(response.data);
                setFilteredRecords(response.data); // 처음엔 전체 데이터로 설정
            } catch (error) {
                console.error("Error fetching test scores:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchTestScores();
    }, [memberId]);

    const filterRecords = (lang) => {
        setSelectedLang(lang); // 선택된 언어 업데이트
        if (lang === '전체') {
            setFilteredRecords(testRecords); // 전체 기록으로 설정
        } else {
            setFilteredRecords(testRecords.filter(record => record.testLang === lang));
        }
    };

    if (loading) {
        return <Typography variant="h6">Loading...</Typography>;
    }

    return (
        <Paper elevation={3} sx={{ maxWidth: '900px', margin: '20px auto', padding: '30px' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h5" sx={{ fontWeight: 'bold', marginBottom: 2 }}>
                    나의 시험 기록
                </Typography>
                <Button variant="outlined" onClick={() => navigate('/word-test/languageSelect')}>
                    뒤로가기
                </Button>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', marginBottom: 2 }}>
                {['전체', '영어', '일본어', '중국어', '베트남어', '프랑스어'].map((lang) => (
                    <Button
                        key={lang}
                        variant={selectedLang === lang ? 'contained' : 'outlined'}
                        color="primary"
                        onClick={() => filterRecords(lang)}
                        sx={{ flex: 1, margin: '0 5px', height: '48px' }}
                    >
                        {lang} 시험
                    </Button>
                ))}
            </Box>
            {filteredRecords.length === 0 ? (
                <Typography variant="body1" sx={{ textAlign: 'center', fontSize: '1.2rem' }}>
                    시험 기록이 없습니다.
                </Typography>
            ) : (
                filteredRecords.map(record => (
                    <Box key={record.wordScoreId} sx={{ marginBottom: 2, padding: 2, border: '1px solid #ccc', borderRadius: '4px', textAlign: 'left' }}>
                        <Typography variant="body1" sx={{ fontSize: '1.2rem', fontWeight: 'bold' }}>
                            {record.testLang} 시험 Level {record.testLevel}
                        </Typography>
                        <Typography variant="body1">응시 날짜 : {new Date(record.createdAt).toLocaleString()}</Typography>
                        <Typography variant="body1">시험 점수 : {record.testScore}점</Typography>
                        <Typography variant="body1">랭크 점수 : {record.testRankScore}점</Typography>
                    </Box>
                ))
            )}
        </Paper>
    );
};

export default TestRecords;