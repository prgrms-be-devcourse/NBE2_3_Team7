import React from 'react';
import { Box, Container, Typography, List, ListItem, Button, ListItemText } from '@mui/material';
import {Link, useNavigate} from 'react-router-dom';

const WordManagementPage = () => {
    const navigate = useNavigate();
    return (
        <Container maxWidth="md">
            <Box sx={{ textAlign: 'right', marginBottom: 2 }}>
                <Button variant="outlined" onClick={() => navigate('/word-learning')}>
                    뒤로가기
                </Button>
            </Box>

            {/* 배너 영역 */}
            <Box sx={{
                backgroundColor: '#007bff',
                color: 'white',
                padding: 2,
                textAlign: 'center',
                marginBottom: 3,
                boxShadow: 3
            }}>
                단어 사전
            </Box>

            {/* 단어 보기 목록 */}
            <Box
                sx={{
                    backgroundColor: '#ffffff',
                    padding: 3,
                    borderRadius: 2,
                    boxShadow: 3,
                }}
            >
                <Typography variant="h5" color="primary" fontWeight="bold" gutterBottom>
                    단어 목록 조회
                </Typography>
                <List>
                    <ListItem>
                        <ListItemText primary={<Link to="/word-list?lang=영어">영어 / English</Link>} />
                    </ListItem>
                    <ListItem>
                        <ListItemText primary={<Link to="/word-list?lang=일본어">일본어 / 日本語</Link>} />
                    </ListItem>
                    <ListItem>
                        <ListItemText primary={<Link to="/word-list?lang=중국어">중국어 / 中文</Link>} />
                    </ListItem>
                    <ListItem>
                        <ListItemText primary={<Link to="/word-list?lang=베트남어">베트남어 / Tiếng Việt</Link>} />
                    </ListItem>
                    <ListItem>
                        <ListItemText primary={<Link to="/word-list?lang=프랑스어">프랑스어 / Français</Link>} />
                    </ListItem>
                </List>
            </Box>

            {/* 단어 관리 목록 */}
            <Box
                sx={{
                    backgroundColor: '#ffffff',
                    padding: 3,
                    borderRadius: 2,
                    boxShadow: 3,
                    marginTop: 3,
                }}
            >
                <Typography variant="h5" color="primary" fontWeight="bold" gutterBottom>
                    단어 관리 - 관리자 전용
                </Typography>

                <List>
                    <ListItem>
                        <Link to="/word-register">
                            <ListItemText primary="단어 등록" />
                        </Link>
                    </ListItem>
                    <ListItem>
                        <Link to="/word-edit">
                            <ListItemText primary="단어 수정, 삭제" />
                        </Link>
                    </ListItem>
                </List>
            </Box>
        </Container>
    );
};

export default WordManagementPage;
