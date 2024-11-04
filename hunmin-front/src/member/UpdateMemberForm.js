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
    MenuItem,
} from '@mui/material';
import api from '../axios';

const UpdateMemberForm = () => {
    const [memberId, setMemberId] = useState('');
    const [nickname, setNickname] = useState('');
    const [level, setLevel] = useState('');
    const [image, setImage] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [currentImage, setCurrentImage] = useState(null); // 현재 프로필 이미지 상태 추가
    const [error, setError] = useState('');

    // 초기 데이터 로드
    useEffect(() => {
        const storedEmail = localStorage.getItem('email');
        const storedNickname = localStorage.getItem('nickname');
        const storedLevel = localStorage.getItem('level');
        const storedMemberId = localStorage.getItem('memberId');
        const storedImage = localStorage.getItem('image');

        console.log('=== 컴포넌트 마운트 시 저장된 이미지 정보 ===');
        console.log('localStorage에서 가져온 이미지:', storedImage);

        if (storedMemberId) setMemberId(storedMemberId);
        if (storedNickname) setNickname(storedNickname);
        if (storedLevel) setLevel(storedLevel);
        if (storedImage) {
            console.log('이미지 상태 업데이트 전:', currentImage);
            setCurrentImage(storedImage);
            setImagePreview(storedImage);
            console.log('이미지 상태 업데이트 후:', storedImage);
        }
    }, []);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImage(file);
            // 파일 미리보기 생성
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const formData = new FormData();
            const memberInfo = {
                nickname: nickname,
                level: level
            };
            formData.append('memberInfo', new Blob([JSON.stringify(memberInfo)], {
                type: 'application/json'
            }));

            if (image) {
                formData.append('profileImage', image);
            }

            // PUT 요청만 수행
            await api.put(`/members/${memberId}`, formData);

            // 로컬 상태 업데이트
            localStorage.setItem('nickname', nickname);
            localStorage.setItem('level', level);

            if (image) {
                // 이미지 미리보기를 현재 이미지로 설정
                setCurrentImage(imagePreview);
                localStorage.setItem('image', imagePreview);
            }

            setImage(null); // 파일 선택 초기화
            alert('회원정보가 수정되었습니다.');

        } catch (error) {
            console.error('수정 중 에러 발생:', error);
            console.error('=== 에러 상세:', error.response);
            setError('회원정보 수정에 실패했습니다.');
        }
    };

    return (
        <Container maxWidth="xs">
            <Box sx={{ mt: 8 }}>
                <Typography variant="body1" color="textSecondary">
                    이메일: {localStorage.getItem('email')}
                </Typography>
                <Typography variant="body1" color="textSecondary">
                    국적: {localStorage.getItem('country')}
                </Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        label="닉네임"
                        fullWidth
                        margin="normal"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                    />
                    <FormControl fullWidth margin="normal">
                        <InputLabel>한국어 레벨</InputLabel>
                        <Select
                            value={level}
                            onChange={(e) => setLevel(e.target.value)}
                        >
                            <MenuItem value="ADVANCED">상</MenuItem>
                            <MenuItem value="INTERMEDIATE">중</MenuItem>
                            <MenuItem value="BEGINNER">하</MenuItem>
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

                    {/* 이미지 표시 부분 */}
                    <Box mt={2} sx={{ textAlign: 'center' }}>
                        <img
                            src={imagePreview || currentImage}
                            alt="프로필"
                            style={{
                                width: '200px',
                                height: '200px',
                                objectFit: 'cover',
                                borderRadius: '50%',
                                border: '1px solid #ccc',
                                display: imagePreview || currentImage ? 'block' : 'none',
                                margin: '0 auto'
                            }}
                            onError={(e) => {
                                console.error('이미지 로드 실패');
                                console.error('시도한 이미지 URL:', imagePreview || currentImage);
                                console.error('현재 이미지 상태:', {
                                    imagePreview,
                                    currentImage,
                                    localStorage: localStorage.getItem('image')
                                });
                                e.target.onerror = null;
                            }}
                        />
                    </Box>

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