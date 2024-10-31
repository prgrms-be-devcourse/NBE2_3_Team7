import React, { useState, useEffect } from 'react';
//import axios from 'axios';
import api from '../axios';
import { useParams, useNavigate } from 'react-router-dom';
import './CreateNoticePage.css';
import Markdown from 'markdown-to-jsx';
import BoardWrite from '../board/write/BoardWrite'; // BoardWrite 컴포넌트를 임포트합니다.
import './ErrorMessage.css';

const CreateNoticePage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    //const memberId = localStorage.getItem('memberId'); // 로컬 저장소에서 멤버 ID를 가져옵니다.
    const [notice, setNotice] = useState({ title: '', content: '' });
    const [errorMessage, setErrorMessage] = useState(''); // 에러 메시지 상태 추가

    useEffect(() => {
        if (id) {
            api.get(`/notices/${id}`) // Axios 인스턴스 사용
            //axios.get(`/api/notices/${id}`)
                .then(response => {
                    setNotice(response.data);
                })
                .catch(error => {
                    if (error.response && error.response.data) {
                        setErrorMessage(`Error: ${error.response.data.error}`); // 에러 메시지를 팝업으로 표시
                        console.error(error)
                    } else {
                        setErrorMessage("There was an error creating/updating the notice!");
                    }

                });
        }
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setNotice(prevNotice => ({
            ...prevNotice,
            [name]: value
        }));
    };

    const handleContentChange = (content) => {
        setNotice(prevNotice => ({
            ...prevNotice,
            content: content
        }));
    };


    const handleSubmit = (e) => {
        e.preventDefault();

        if (id) {
            const { title, content } = notice; // 필요한 필드만 추출
            const noticeData = { title, content };
            api.put(`/notices/${id}`, noticeData)
            //axios.put(`/api/notices/${id}`, notice)
                .then(response => {
                    navigate(`/notices/${id}`);
                })
                .catch(error => {
                    if (error.response && error.response.data) {
                        setErrorMessage(`Error: ${error.response.data.error}`); // 에러 메시지를 팝업으로 표시
                        console.error(error)
                    } else {
                        setErrorMessage("There was an error creating/updating the notice!");
                    }
                });
        } else {
            api.post('/notices', notice)
            //axios.post('/api/notices', notice)
                .then(response => {
                    navigate(`/notices/${response.data.noticeId}`);
                })
                .catch(error => {
                    if (error.response && error.response.data) {
                        setErrorMessage(error.response.data.error);
                        console.error(error)
                    } else {
                        setErrorMessage("There was an error creating the notice!");
                    }
                });
        }
    };
    const handleGoHome = () => {
        navigate('/notices');
    };

    const uploadImage = async (file) => {
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await api.post('/api/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            return response.data.url; // 서버에서 반환된 이미지 URL
        } catch (error) {
            console.error('Image upload failed:', error);
            return null;
        }
    };

    return (
        <div className="form-container">
            {errorMessage && (
                <div className="error-message" style={{color: 'red', marginBottom: '10px'}}>
                    {errorMessage}
                </div>
            )}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label className="label">제목:</label>
                    <input type="text" name="title" value={notice.title} onChange={handleChange} required
                           className="input"/>
                </div>
                <div className="form-group">
                    <label className="label">내용:</label>
                    <BoardWrite
                        value={notice.content}
                        onChange={handleContentChange}
                        uploadImage={uploadImage}
                        setImageUrls={(urls) => setNotice(prevNotice => ({
                            ...prevNotice,
                            imageUrls: urls
                        }))}
                    />
                </div>
                <div className="preview">
                    <h3>미리보기:</h3>
                    <Markdown>{notice.content}</Markdown>
                </div>
                <button type="submit" className="submit-button">{id ? '수정' : '생성'}</button>
            </form>
            <button onClick={handleGoHome} className="home-button">공지사항 목록으로 돌아가기</button>
        </div>
    );

};

export default CreateNoticePage;