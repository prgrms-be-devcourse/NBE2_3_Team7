// PasswordVerify.jsx 컴포넌트
import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const PasswordVerify = () => {
    const [email, setEmail] = useState('');
    const [nickname, setNickname] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/members/password/verify', {
                email,
                nickname
            });

            // 응답 확인
            console.log('Response:', response.data);

            if (response.data === "사용자 확인 완료") {
                navigate('/password/update', {
                    state: { email,
                             nickname
                    }
                });
            }
        } catch (error) {
            console.error('Verification failed:', error);
            setError('인증 실패. 이메일과 닉네임을 확인하세요.');
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ mt: 8 }}>
                <Typography component="h1" variant="h5">
                    비밀번호 재설정 인증
                </Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="이메일"
                        autoComplete="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="닉네임"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                    />
                    {error && <Typography color="error">{error}</Typography>}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        인증하기
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default PasswordVerify;