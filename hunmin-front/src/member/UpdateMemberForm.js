import React, { useState, useEffect } from 'react';
import {
    TextField,
    Button,
    Container,
    Typography,
    Box,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from '@mui/material';
import { useNavigate } from 'react-router-dom'; // useNavigate 추가
import api from '../axios'; // axios 인스턴스 import
import axios from 'axios'; // 기본 axios import

const levels = [
    { label: "상", value: "ADVANCED" },
    { label: "중", value: "INTERMEDIATE" },
    { label: "하", value: "BEGINNER" }
];

const UpdateMemberForm = () => {
    const [memberId, setMemberId] = useState('');
    const [nickname, setNickname] = useState('');
    const [level, setLevel] = useState('');
    const [image, setImage] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate(); // navigate 훅 사용

    useEffect(() => {
        const storedEmail = localStorage.getItem('email');
        const storedNickname = localStorage.getItem('nickname');
        const storedLevel = localStorage.getItem('level');
        const storedMemberId = localStorage.getItem('memberId');
        const storedCountry = localStorage.getItem('country');

        if (storedEmail) {
            setMemberId(storedMemberId);
        }
        if (storedNickname) {
            setNickname(storedNickname);
        }
        if (storedLevel) {
            setLevel(storedLevel);
        }
        const storedImage = localStorage.getItem('image');
        if (storedImage) {
            setImagePreview(storedImage);
        }
    }, []);

    const handleNicknameChange = (e) => {
        setNickname(e.target.value);
    };

    const handleLevelChange = (e) => {
        setLevel(e.target.value);
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImage(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const updatedData = {};

        const storedNickname = localStorage.getItem('nickname');
        const storedLevel = localStorage.getItem('level');

        if (nickname && nickname !== storedNickname) {
            updatedData.nickname = nickname;
        }

        if (level && level !== storedLevel) {
            updatedData.level = level;
        }

        let imageUrl = null;

        if (image) {
            const imageData = new FormData();
            imageData.append('image', image);
            const imageResponse = await axios.post('http://localhost:8080/api/members/uploads', imageData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            imageUrl = imageResponse.data;
            updatedData.image = imageUrl;
            console.log(imageUrl);
        }

        if (Object.keys(updatedData).length > 0) {
            try {
                await api.put(`/members/${memberId}`, {
                    ...updatedData,
                });
                alert('회원정보가 수정되었습니다. 다시 로그인 해주세요.'); // 메시지 추가
                navigate('/login'); // 로그인 페이지로 리다이렉트
            } catch (error) {
                console.error(error);
                setError('회원정보 수정에 실패했습니다.');
            }
        } else {
            alert('변경된 정보가 없습니다.');
        }
    };

    const storedEmail = localStorage.getItem('email');
    const storedCountry = localStorage.getItem('country');

    return (
        <Container maxWidth="xs">
            <Box sx={{ mt: 8 }}>
                <Typography variant="body1" color="textSecondary">
                    이메일: {storedEmail}
                </Typography>
                <Typography variant="body1" color="textSecondary">
                    국적: {storedCountry}
                </Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        label="닉네임"
                        fullWidth
                        margin="normal"
                        value={nickname}
                        onChange={handleNicknameChange}
                    />
                    <FormControl fullWidth margin="normal">
                        <InputLabel>한국어 레벨</InputLabel>
                        <Select
                            value={level}
                            onChange={handleLevelChange}
                        >
                            {levels.map((levelObj, index) => (
                                <MenuItem key={index} value={levelObj.value}>{levelObj.label}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
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
                            onChange={handleFileChange}
                        />
                    </Button>

                    {imagePreview && (
                        <Box mt={2} sx={{ textAlign: 'center' }}>
                            <img src={imagePreview} alt="프로필 미리보기" style={{ width: '100%', maxHeight: '300px', objectFit: 'cover' }} />
                        </Box>
                    )}

                    {error && <Typography color="error">{error}</Typography>}
                    <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 2 }}>
                        수정하기
                    </Button>
                </form>
            </Box>
        </Container>
    );
};

export default UpdateMemberForm;
