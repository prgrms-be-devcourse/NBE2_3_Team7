import axios from 'axios';
const apiUrl = process.env.REACT_APP_API_URL;

// Axios 인스턴스 생성
const api = axios.create({
    baseURL: `${apiUrl}/api`, // 기본 URL 설정
});

// 요청 인터셉터 추가 (토큰이 있을 때만 헤더에 추가)
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

export default api;
