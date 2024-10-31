import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import KakaoMapSearch from '../board/map/KakaoMapSearch';
import BoardWrite from '../board/write/BoardWrite';
import { TextField, Button, Typography, Container, Box, Paper } from '@mui/material';
import api from '../axios';

const CreateBoardPage = () => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [location, setLocation] = useState(null);
    const [imageUrls, setImageUrls] = useState([]);
    const navigate = useNavigate();

    // Handle location selection from KakaoMapSearch component
    const handleLocationSelect = (selectedLocation) => {
        setLocation(selectedLocation);
    };

    // Upload image function
    const uploadImage = async (file) => {
        const formData = new FormData();
        formData.append('files', file);

        try {
            const response = await axios.post('http://localhost:8080/api/board/uploadImage', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            const imageUrl = response.data[0];
            setImageUrls((prev) => [...prev, imageUrl]);
            return imageUrl;
        } catch (error) {
            console.error('Image upload failed:', error);
            return null;
        }
    };

    // Handle form submission
    const handleSubmit = async () => {
        try {
            const boardData = {
                title,
                content,
                location: location ? location.name : '',
                latitude: location ? location.latitude : null,
                longitude: location ? location.longitude : null,
                imageUrls: imageUrls.length > 0 ? imageUrls : null,
                memberId: localStorage.getItem('memberId'), // 로컬 스토리지에서 memberId 가져오기
            };
            console.log('Sending board data:', boardData);
            const response = await api.post('/board', boardData);
            console.log(response.data);
            navigate('/');
        } catch (error) {
            console.error('Error creating board:', error);
        }
    };

    return (
        <Container maxWidth="lg"> {/* 화면 전체를 사용하기 위해 maxWidth="lg" 설정 */}
            <Paper elevation={3} style={{ padding: '20px', marginTop: '20px' }}>
                <Typography variant="h4" gutterBottom>
                    게시글 작성
                </Typography>
                <Box component="form" noValidate autoComplete="off">
                    <TextField
                        fullWidth
                        label="제목"
                        variant="outlined"
                        margin="normal"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                    <BoardWrite
                        value={content}
                        onChange={setContent}
                        uploadImage={uploadImage}
                        setImageUrls={setImageUrls}
                    />
                    <Box mt={3}></Box>
                    <Typography variant="subtitle1" gutterBottom>
                        장소 (선택 사항)
                    </Typography>
                    <KakaoMapSearch onLocationSelect={handleLocationSelect} />
                    <Button
                        variant="contained"
                        color="primary"
                        fullWidth
                        style={{ marginTop: '20px' }}
                        onClick={handleSubmit}
                    >
                        저장
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
};

export default CreateBoardPage;
