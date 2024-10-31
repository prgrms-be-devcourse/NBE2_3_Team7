// PasswordUpdate.jsx 컴포넌트
import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

const PasswordUpdate = () => {
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();
    const email = location.state?.email;

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (newPassword !== confirmPassword) {
            setError('비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            const response = await axios.post('http://localhost:8080/api/members/password/update', {
                email: location.state.email,
                nickname: location.state.nickname,  // 닉네임도 필요
                newPassword
            });

            if (response.data === "비밀번호가 성공적으로 변경되었습니다.") {
                alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.');
                navigate('/login');
            }
        } catch (error) {
            console.error('Password update failed:', error.response?.data);
            setError(error.response?.data || '비밀번호 변경 실패. 다시 시도해주세요.');
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ mt: 8 }}>
                <Typography component="h1" variant="h5">
                    새 비밀번호 설정
                </Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="새 비밀번호"
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        label="새 비밀번호 확인"
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                    />
                    {error && <Typography color="error">{error}</Typography>}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        비밀번호 변경
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default PasswordUpdate;