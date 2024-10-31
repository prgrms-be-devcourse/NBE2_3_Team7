import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Box, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const countries = [
    "대한민국", "미국", "영국", "일본", "중국",
    "프랑스", "독일", "스페인", "캐나다", "호주"
];

const levels = [
    { label: "상", value: "ADVANCED" },
    { label: "중", value: "INTERMEDIATE" },
    { label: "하", value: "BEGINNER" }
];

const RegistrationForm = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [country, setCountry] = useState('');
    const [level, setLevel] = useState('');
    const [image, setImage] = useState(null);  // 이미지 파일 상태
    const [imagePreview, setImagePreview] = useState(null);  // 이미지 미리보기 상태
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImage(file);  // 이미지 파일을 상태로 저장

            // FileReader를 사용하여 이미지 파일을 읽고 미리보기 설정
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result);  // base64 형식으로 미리보기 저장
            };
            reader.readAsDataURL(file);  // 이미지 파일을 base64로 읽음
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            let imageUrl = null;

            // 이미지가 있는 경우 이미지 업로드 요청
            if (image) {
                const imageData = new FormData();
                imageData.append('image', image);
                const imageResponse = await axios.post('http://localhost:8080/api/members/uploads', imageData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
                imageUrl = imageResponse.data;  // 업로드한 이미지 URL
            }

            // 회원가입 데이터 전송
            await axios.post('http://localhost:8080/api/members/register', {
                email,
                password,
                nickname,
                country,
                level,
                image: imageUrl  // 이미지 URL 포함
            });

            navigate('/login');  // 회원가입 성공 시 로그인 페이지로 이동
        } catch (error) {
            setError('회원가입 실패. 다시 시도해주세요.');
        }
    };

    return (
        <Container maxWidth="xs">
            <Box sx={{ mt: 8 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    회원가입
                </Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        label="이름"
                        fullWidth
                        margin="normal"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        required
                    />
                    <TextField
                        label="이메일"
                        fullWidth
                        margin="normal"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <TextField
                        label="비밀번호"
                        type="password"
                        fullWidth
                        margin="normal"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <FormControl fullWidth margin="normal" required>
                        <InputLabel>국가</InputLabel>
                        <Select
                            value={country}
                            onChange={(e) => setCountry(e.target.value)}
                        >
                            {countries.map((country, index) => (
                                <MenuItem key={index} value={country}>{country}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="normal" required>
                        <InputLabel>한국어 레벨</InputLabel>
                        <Select
                            value={level}
                            onChange={(e) => setLevel(e.target.value)}
                        >
                            {levels.map((levelObj, index) => (
                                <MenuItem key={index} value={levelObj.value}>{levelObj.label}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    {/* 이미지 파일 입력 필드 추가 */}
                    <Button
                        variant="contained"
                        component="label"
                        fullWidth
                        sx={{ mt: 2 }}
                    >
                        프로필 이미지 업로드
                        <input
                            type="file"
                            hidden
                            accept="image/*"
                            onChange={handleImageChange}
                        />
                    </Button>

                    {/* 이미지 미리보기 추가 */}
                    {imagePreview && (
                        <Box mt={2} sx={{ textAlign: 'center' }}>
                            <img src={imagePreview} alt="프로필 미리보기" style={{ width: '100%', maxHeight: '300px', objectFit: 'cover' }} />
                        </Box>
                    )}

                    {error && <Typography color="error">{error}</Typography>}
                    <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
                        회원가입
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default RegistrationForm;
