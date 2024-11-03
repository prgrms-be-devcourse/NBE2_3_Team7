// import React, { useState } from 'react';
// import { Box, Container, Typography, Button, TextField, Select, MenuItem, FormControl, InputLabel, Alert } from '@mui/material';
// import { useNavigate } from 'react-router-dom';
// import api from '../axios';
//
// const WordEditPage = () => {
//     const [title, setTitle] = useState('');
//     const [lang, setLang] = useState('');
//     const [originalTitle, setOriginalTitle] = useState(''); // 수정 전 title 저장
//     const [originalLang, setOriginalLang] = useState(''); // 수정 전 lang 저장
//     const [wordData, setWordData] = useState(null);
//     const [translation, setTranslation] = useState('');
//     const [definition, setDefinition] = useState('');
//     const [error, setError] = useState('');
//     const [updatedWordData, setUpdatedWordData] = useState(null); // 업데이트된 단어 데이터를 저장할 상태
//     const navigate = useNavigate();
//
//     const handleSearch = async (event) => {
//         event.preventDefault();
//         setError('');
//
//         try {
//             const response = await api.get(`/words/join/${title}/${lang}`); // API 호출
//             const fetchedWordData = response.data;
//
//             // 검색된 단어 데이터로 상태 업데이트
//             setWordData(fetchedWordData);
//             setTranslation(fetchedWordData.translation);
//             setDefinition(fetchedWordData.definition);
//
//             setOriginalTitle(fetchedWordData.title);
//             setOriginalLang(fetchedWordData.lang);
//
//             // 수정 전의 title과 lang 값을 설정
//             setOriginalTitle(fetchedWordData.title);
//             setOriginalLang(fetchedWordData.lang);
//         } catch (err) {
//             setError('단어를 찾을 수 없습니다.');
//             setWordData(null);
//         }
//     };
//
//
//
//     const memberId = localStorage.getItem('memberId');
//
//     // const handleUpdate = async (event) => {
//     //     event.preventDefault();
//     //     setError('');
//     //
//     //     if (!wordData) {
//     //         setError('단어 데이터가 없습니다.');
//     //         return;
//     //     }
//     //
//     //     try {
//     //         const updatedWordData = {
//     //             memberId: memberId,
//     //             wordId: wordData.wordId, // wordData에서 wordId 가져오기
//     //             title,
//     //             lang,
//     //             translation,
//     //             definition,
//     //         };
//     //
//     //         console.log('업데이트할 데이터:', updatedWordData);
//     //         console.log('originalTitle:', originalTitle); // 로그 추가
//     //         console.log('originalLang:', originalLang);   // 로그 추가
//     //
//     //         console.log('Encoded Title:', encodeURIComponent(originalTitle));
//     //         console.log('Encoded Lang:', encodeURIComponent(originalLang));
//     //
//     //         // 수정 전 title과 lang을 URL 파라미터로 사용
//     //         const response = await api.put(`/words/update/${originalTitle}/${originalLang}`, updatedWordData);
//     //         setUpdatedWordData(response.data); // 업데이트된 단어 데이터를 상태에 저장
//     //     } catch (err) {
//     //         setError('단어 수정 중 오류가 발생했습니다.');
//     //     }
//     // };
//
//     const handleUpdate = async (event) => {
//         event.preventDefault();
//         setError('');
//
//         if (!wordData) {
//             setError('단어 데이터가 없습니다.');
//             return;
//         }
//
//         try {
//             const updatedWordData = {
//                 memberId: memberId,
//                 wordId: wordData.wordId, // wordData에서 wordId 가져오기
//                 title,
//                 lang,
//                 translation,
//                 definition,
//             };
//
//             console.log('업데이트할 데이터:', updatedWordData);
//             console.log('originalTitle:', originalTitle);
//             console.log('originalLang:', originalLang);
//
//             console.log('Encoded Title:', encodeURIComponent(originalTitle));
//             console.log('Encoded Lang:', encodeURIComponent(originalLang));
//
//             // // 인코딩 적용
//             // const encodedTitle = encodeURIComponent(originalTitle);
//             // const encodedLang = encodeURIComponent(originalLang);
//
//
//
//             // 수정 전 title과 lang을 URL 파라미터로 사용
//             const response = await api.put(`/words/update/${originalTitle}/${originalLang}`, updatedWordData);
//             // const response = await api.put(`/words/update/${encodedTitle}/${encodedLang}`, updatedWordData);
//             setUpdatedWordData(response.data); // 업데이트된 단어 데이터를 상태에 저장
//         } catch (err) {
//             setError('단어 수정 중 오류가 발생했습니다.');
//         }
//     };
//
//     const handleDelete = async () => {
//         setError('');
//         try {
//             await api.delete(`/words/delete/${originalTitle}/${originalLang}`);
//             setWordData(null); // 삭제 후 단어 데이터 초기화
//             setUpdatedWordData(null); // 수정된 단어 데이터 초기화
//             alert('단어가 삭제되었습니다.');
//         } catch (err) {
//             setError('단어 삭제 중 오류가 발생했습니다.');
//         }
//     };
//
//     return (
//         <Container maxWidth="md">
//             <Box sx={{
//                 backgroundColor: '#007bff',
//                 color: 'white',
//                 padding: 2,
//                 textAlign: 'center',
//                 marginBottom: 3,
//                 boxShadow: 3
//             }}>
//                 단어 검색 및 수정
//             </Box>
//
//             <form onSubmit={handleSearch}>
//                 <TextField
//                     label="검색할 단어 입력"
//                     variant="outlined"
//                     fullWidth
//                     required
//                     value={title}
//                     onChange={(e) => setTitle(e.target.value)}
//                     sx={{ marginBottom: 2 }}
//                 />
//
//                 <FormControl fullWidth required sx={{ marginBottom: 2 }}>
//                     <InputLabel>언어 선택</InputLabel>
//                     <Select
//                         value={lang}
//                         onChange={(e) => setLang(e.target.value)}
//                         label="언어 선택"
//                     >
//                         <MenuItem value="">
//                             <em>언어 선택</em>
//                         </MenuItem>
//                         <MenuItem value="영어">영어</MenuItem>
//                         <MenuItem value="일본어">일본어</MenuItem>
//                         <MenuItem value="중국어">중국어</MenuItem>
//                         <MenuItem value="베트남어">베트남어</MenuItem>
//                         <MenuItem value="프랑스어">프랑스어</MenuItem>
//                     </Select>
//                 </FormControl>
//
//                 <Button type="submit" variant="contained" color="primary">
//                     조회
//                 </Button>
//             </form>
//
//             {error && <Alert severity="error" sx={{ marginTop: 2 }}>{error}</Alert>}
//
//             {wordData && (
//                 <Box sx={{ marginTop: 3, padding: 2, border: '1px solid #ccc', borderRadius: 2 }}>
//                     <Typography variant="h5">현재 단어: {wordData.title}</Typography>
//
//                     <TextField
//                         label="수정할 단어"
//                         variant="outlined"
//                         fullWidth
//                         required
//                         value={title}
//                         onChange={(e) => setTitle(e.target.value)}
//                         sx={{ marginBottom: 2 }}
//                     />
//
//                     <FormControl fullWidth required sx={{ marginBottom: 2 }}>
//                         <InputLabel>언어 선택</InputLabel>
//                         <Select
//                             value={lang}
//                             onChange={(e) => setLang(e.target.value)}
//                             label="언어 선택"
//                         >
//                             <MenuItem value="">
//                                 <em>언어 선택</em>
//                             </MenuItem>
//                             <MenuItem value="영어">영어</MenuItem>
//                             <MenuItem value="일본어">일본어</MenuItem>
//                             <MenuItem value="중국어">중국어</MenuItem>
//                             <MenuItem value="베트남어">베트남어</MenuItem>
//                             <MenuItem value="프랑스어">프랑스어</MenuItem>
//                         </Select>
//                     </FormControl>
//
//                     <TextField
//                         label="번역"
//                         variant="outlined"
//                         fullWidth
//                         required
//                         multiline
//                         rows={4}
//                         value={translation}
//                         onChange={(e) => setTranslation(e.target.value)}
//                         sx={{ marginBottom: 2 }}
//                     />
//
//                     <TextField
//                         label="정의"
//                         variant="outlined"
//                         fullWidth
//                         required
//                         multiline
//                         rows={4}
//                         value={definition}
//                         onChange={(e) => setDefinition(e.target.value)}
//                         sx={{ marginBottom: 2 }}
//                     />
//
//                     <Button onClick={handleUpdate} variant="contained" color="primary">
//                         수정하기
//                     </Button>
//                     <Button onClick={handleDelete} variant="contained" color="secondary" sx={{ marginLeft: 2 }}>
//                         삭제하기
//                     </Button>
//                 </Box>
//             )}
//
//             {/* 수정된 단어가 있을 경우 보여주는 부분 */}
//             {updatedWordData && (
//                 <Box sx={{ marginTop: 3, padding: 2, border: '1px solid #4caf50', borderRadius: 2 }}>
//                     <Typography variant="h5" color="green">수정된 단어</Typography>
//                     <Typography variant="body1">단어: {updatedWordData.title}</Typography>
//                     <Typography variant="body1">언어: {updatedWordData.lang}</Typography>
//                     <Typography variant="body1">번역: {updatedWordData.translation}</Typography>
//                     <Typography variant="body1">정의: {updatedWordData.definition}</Typography>
//                 </Box>
//             )}
//
//             <Button
//                 href="/word-management"
//                 variant="text"
//                 fullWidth
//                 sx={{ marginTop: 2 }}
//             >
//                 사전으로 돌아가기
//             </Button>
//         </Container>
//     );
// };
//
// export default WordEditPage;

import React, {useState} from 'react';
import {
    Box,
    Container,
    Typography,
    Button,
    TextField,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Alert
} from '@mui/material';
import {useNavigate} from 'react-router-dom';
import api from '../axios';

const WordEditPage = () => {
    const [title, setTitle] = useState(''); // 검색할 단어
    const [lang, setLang] = useState(''); // 검색할 언어
    const [wordData, setWordData] = useState(null); // 조회된 단어 데이터
    const [originalTitle, setOriginalTitle] = useState(''); // 원래의 title
    const [originalLang, setOriginalLang] = useState(''); // 원래의 lang
    const [newTitle, setNewTitle] = useState(''); // 수정할 title
    const [newLang, setNewLang] = useState(''); // 수정할 lang
    const [newTranslation, setNewTranslation] = useState(''); // 수정할 translation
    const [newDefinition, setNewDefinition] = useState(''); // 수정할 definition
    const [error, setError] = useState('');
    const [updatedWordData, setUpdatedWordData] = useState(null);
    const navigate = useNavigate();

    const handleSearch = async (event) => {
        event.preventDefault();
        setError('');

        try {
            const response = await api.get(`/words/join/${title}/${lang}`);
            const fetchedWordData = response.data;

            setWordData(fetchedWordData);
            setNewTranslation(fetchedWordData.translation);
            setNewDefinition(fetchedWordData.definition);
            setNewTitle(fetchedWordData.title); // 기존 title로 초기화
            setNewLang(fetchedWordData.lang); // 기존 lang으로 초기화
            // originalTitle과 originalLang 설정
            setOriginalTitle(fetchedWordData.title);
            setOriginalLang(fetchedWordData.lang);
            console.log('originalTitle:', fetchedWordData.title);
            console.log('originalLang:', fetchedWordData.lang);

        } catch (err) {
            setError('단어를 찾을 수 없습니다.');
            setWordData(null);
        }
    };

    const handleUpdate = async (event) => {
        event.preventDefault();
        setError('');

        if (!wordData) {
            setError('단어 데이터가 없습니다.');
            return;
        }

        const memberId = localStorage.getItem('memberId'); // memberId 가져오기

        try {
            const updatedWordDataPayload = {
                memberId: memberId,
                wordId: wordData.wordId,
                title: newTitle,
                lang: newLang,
                translation: newTranslation,
                definition: newDefinition,
                originalTitle: originalTitle,
                originalLang: originalLang
            };

            console.log('수정 요청 데이터:', updatedWordDataPayload);
            console.log('수정 요청 단어', originalTitle, originalLang)

            // 원래 제목과 언어로 요청 보내기
            const response = await api.put(`/words/update/${originalTitle}/${originalLang}`, updatedWordDataPayload);

            console.log('수정된 단어 데이터:', response.data);
            setUpdatedWordData(response.data);

        } catch (err) {
            console.error(err);
            setError('단어 수정 중 오류가 발생했습니다.');
        }
    };

    const handleDelete = async () => {
        setError('');
        try {
            await api.delete(`/words/delete/${wordData.title}/${wordData.lang}`);
            setWordData(null);
            setUpdatedWordData(null);
            alert('단어가 삭제되었습니다.');
        } catch (err) {
            setError('단어 삭제 중 오류가 발생했습니다.');
        }
    };

    return (
        <Container maxWidth="md">
            <Box sx={{
                backgroundColor: '#007bff',
                color: 'white',
                padding: 2,
                textAlign: 'center',
                marginBottom: 3,
                boxShadow: 3
            }}>
                단어 검색 및 수정
            </Box>

            <form onSubmit={handleSearch}>
                <TextField
                    label="검색할 단어 입력"
                    variant="outlined"
                    fullWidth
                    required
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    sx={{marginBottom: 2}}
                />
                <FormControl fullWidth required sx={{marginBottom: 2}}>
                    <InputLabel>언어 선택</InputLabel>
                    <Select
                        value={lang}
                        onChange={(e) => setLang(e.target.value)}
                        label="언어 선택"
                    >
                        <MenuItem value="">
                            <em>언어 선택</em>
                        </MenuItem>
                        <MenuItem value="영어">영어</MenuItem>
                        <MenuItem value="일본어">일본어</MenuItem>
                        <MenuItem value="중국어">중국어</MenuItem>
                        <MenuItem value="베트남어">베트남어</MenuItem>
                        <MenuItem value="프랑스어">프랑스어</MenuItem>
                    </Select>
                </FormControl>

                <Button type="submit" variant="contained" color="primary">
                    조회
                </Button>
            </form>

            {error && <Alert severity="error" sx={{marginTop: 2}}>{error}</Alert>}

            {wordData && (
                <Box sx={{marginTop: 3, padding: 2, border: '1px solid #ccc', borderRadius: 2}}>
                    <Typography variant="h5">현재 단어: {wordData.title}</Typography>

                    <TextField
                        label="수정할 단어"
                        variant="outlined"
                        fullWidth
                        required
                        value={newTitle}
                        onChange={(e) => setNewTitle(e.target.value)}
                        sx={{marginBottom: 2}}
                    />

                    <FormControl fullWidth required sx={{marginBottom: 2}}>
                        <InputLabel>언어 선택</InputLabel>
                        <Select
                            value={newLang}
                            onChange={(e) => setNewLang(e.target.value)}
                            label="언어 선택"
                        >
                            <MenuItem value="">
                                <em>언어 선택</em>
                            </MenuItem>
                            <MenuItem value="영어">영어</MenuItem>
                            <MenuItem value="일본어">일본어</MenuItem>
                            <MenuItem value="중국어">중국어</MenuItem>
                            <MenuItem value="베트남어">베트남어</MenuItem>
                            <MenuItem value="프랑스어">프랑스어</MenuItem>
                        </Select>
                    </FormControl>

                    <TextField
                        label="번역"
                        variant="outlined"
                        fullWidth
                        required
                        multiline
                        rows={4}
                        value={newTranslation}
                        onChange={(e) => setNewTranslation(e.target.value)}
                        sx={{marginBottom: 2}}
                    />

                    <TextField
                        label="정의"
                        variant="outlined"
                        fullWidth
                        required
                        multiline
                        rows={4}
                        value={newDefinition}
                        onChange={(e) => setNewDefinition(e.target.value)}
                        sx={{marginBottom: 2}}
                    />

                    <Button onClick={handleUpdate} variant="contained" color="primary">
                        수정하기
                    </Button>
                    <Button onClick={handleDelete} variant="contained" color="secondary" sx={{marginLeft: 2}}>
                        삭제하기
                    </Button>
                </Box>
            )}

            {updatedWordData && (
                <Box sx={{marginTop: 3, padding: 2, border: '1px solid #4caf50', borderRadius: 2}}>
                    <Typography variant="h5" color="green">수정된 단어</Typography>
                    <Typography variant="body1">단어: {updatedWordData.title}</Typography>
                    <Typography variant="body1">언어: {updatedWordData.lang}</Typography>
                    <Typography variant="body1">번역: {updatedWordData.translation}</Typography>
                    <Typography variant="body1">정의: {updatedWordData.definition}</Typography>
                </Box>
            )}

            <Button
                href="/word-management"
                variant="text"
                fullWidth
                sx={{marginTop: 2}}
            >
                사전으로 돌아가기
            </Button>
        </Container>
    );
};

export default WordEditPage;
