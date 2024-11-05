import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Box, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
const apiUrl = process.env.REACT_APP_API_URL;
// const apiUrl = 'http://localhost:8080'; // 임시로 변경해서 테스트

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
            // FormData 객체 생성
            const formData = new FormData();

            // memberInfo 객체 생성
            const memberInfo = {
                email,
                password,
                nickname,
                country,
                level,
                image: null
            };

            // Blob으로 변환하여 추가
            const memberInfoBlob = new Blob(
                [JSON.stringify(memberInfo)],
                { type: 'application/json' }
            );

            formData.append('memberInfo', memberInfoBlob);

            // 이미지가 있는 경우에만 추가
            if (image) {
                formData.append('profileImage', image);
            }

            // axios 설정
            const config = {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Accept': 'application/json',
                    // CORS 관련 헤더 추가
                    'Access-Control-Allow-Origin': '*'
                }
            };

            console.log('전송 시도:', {
                memberInfo: memberInfo,
                hasImage: !!image
            });

            const response = await axios.post(
                `${apiUrl}/members/register`,
                formData,
                config
            );

            console.log('회원가입 성공:', response.data);
            navigate('/login');
        } catch (error) {
            console.error('=== 로그인 요청 실패 ===', {
                message: error.message,
                status: error.response?.status,
                statusText: error.response?.statusText,
                data: error.response?.data,
                config: {
                    url: error.config?.url,
                    method: error.config?.method,
                    headers: error.config?.headers,
                    data: error.config?.data
                }
            });

            // 네트워크 오류인지 확인
            if (!error.response) {
                console.error('=== 네트워크 오류 ===');
                setError('서버에 연결할 수 없습니다.');
                return;
            }

            // 401 에러 구체적 처리
            if (error.response.status === 401) {
                setError('이메일 또는 비밀번호가 올바르지 않습니다.');
                return;
            }
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
