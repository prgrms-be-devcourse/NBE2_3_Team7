import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Box, Link } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
import Header from '../header/Header';
const apiUrl = process.env.REACT_APP_API_URL;

const LoginForm = ({ setToken }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(`${apiUrl}/api/members/login`, { email, password }, {
                headers: {
                    'Content-Type': 'application/json',
                },
                withCredentials: true
            });

            console.log('=== 로그인 응답 전체 데이터:', response.data); // 전체 응답 데이터 확인

            const token = response.data.token;
            const memberId = response.data.memberId;
            const memberEmail = response.data.email;
            const role = response.data.role;
            const nickname = response.data.nickname;
            const image = response.data.image;
            const level = response.data.level;
            const country = response.data.country;
            const refreshToken = response.data.refreshToken;

            console.log('=== 순수 이미지 URL:', image);
            console.log('=== URL에 포함된 localhost 여부:', image.includes('localhost'));

            // localStorage 저장 전 데이터 확인
            console.log('=== localStorage 저장 전 데이터:', {
                token,
                memberId,
                email: memberEmail,
                role,
                nickname,
                image,
                level,
                country
            });

            // localStorage에 데이터 저장
            localStorage.setItem('token', token);
            localStorage.setItem('memberId', memberId);
            localStorage.setItem('email', memberEmail);
            localStorage.setItem('role', role);
            localStorage.setItem('nickname', nickname);
            localStorage.setItem('image', image);
            localStorage.setItem('level', level);
            localStorage.setItem('country', country);

            // localStorage 저장 후 확인
            console.log('=== localStorage 저장 후 이미지 URL:', localStorage.getItem('image'));

            // 쿠키 설정
            Cookies.set('refresh', refreshToken, {
                expires: 1,
                path: '/'
            });

            setToken(token);

            <Header />;

            navigate('/');
        } catch (error) {
            console.error('=== 로그인 실패 상세:', error.response); // 에러 상세 정보
            console.error('=== 로그인 실패 데이터:', error.response?.data); // 에러 응답 데이터
            setError('로그인 실패. 이메일과 비밀번호를 확인하세요.');
        }
    };

    // 비밀번호 재설정 페이지로 이동
    const handlePasswordReset = () => {
        navigate('/password/verify');
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ mt: 8 }}>
                <Typography component="h1" variant="h5">
                    로그인
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
                        label="비밀번호"
                        type="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    {error && <Typography color="error">{error}</Typography>}
                    <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
                        로그인
                    </Button>

                    <Button
                        variant="text"
                        color="primary"
                        onClick={() => navigate('/register')}
                        sx={{ mt: 2 }}
                        fullWidth
                    >
                        회원가입
                    </Button>

                    {/* 비밀번호 재설정 버튼 */}
                    <Button
                        onClick={handlePasswordReset}
                        fullWidth
                        variant="text"
                        sx={{ mt: 1, mb: 2 }}
                    >
                        비밀번호 재설정
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default LoginForm;
