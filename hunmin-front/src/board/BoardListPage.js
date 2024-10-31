import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../axios';
import Map from './map/KakaoMap'; // Map 컴포넌트 임포트
import { FaUserCircle } from 'react-icons/fa'; // 프로필 아이콘 임포트
import {
    Box,
    Button,
    Typography,
    List,
    ListItem,
    ListItemText,
    TextField,
    AppBar,
    Toolbar,
    IconButton,
    Container,
    Pagination,
    Grid, // Grid 컴포넌트 임포트
} from '@mui/material';
import ChatIcon from '@mui/icons-material/Chat'; // 채팅 아이콘 임포트
import EditIcon from '@mui/icons-material/Edit'; // 연필 아이콘 임포트
import PeopleIcon from '@mui/icons-material/People';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings'; //관리자 페이지 이동 버튼
const BoardListPage = () => {
    const navigate = useNavigate(); // navigate 함수 추가
    const memberId = localStorage.getItem('memberId');
    const nickname = localStorage.getItem('nickname');
    const profileImage = localStorage.getItem('image');
    const role = localStorage.getItem('role'); // 추가 (관리자 확인용)
    const [boards, setBoards] = useState([]);
    const [filteredBoards, setFilteredBoards] = useState([]);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);
    const [totalPages, setTotalPages] = useState(0);
    const [searchLocation, setSearchLocation] = useState('');
    const [mapCenter, setMapCenter] = useState({ lat: 37.5665, lng: 126.978 }); // 초기 지도 중심 설정
    const [mapLevel, setMapLevel] = useState(9); // 지도 레벨 상태 추가
    const [showMyBoards, setShowMyBoards] = useState(false); // 내 작성글 보기 상태
    const [kakaoLoaded, setKakaoLoaded] = useState(false); // Kakao Maps SDK 로드 상태
    const [bookmarkedBoards, setBookmarkedBoards] = useState([]); // 북마크한 게시글 목록 상태
    const [showBookmarkedBoards, setShowBookmarkedBoards] = useState(false); // 북마크한 게시글 보기 상태
    const [searchTitle, setSearchTitle] = useState('');

    useEffect(() => {
        if (showMyBoards) {
            fetchMyBoards();
        } else {
            fetchBoards();
        }
    }, [page, size, showMyBoards]);

    useEffect(() => {
        setFilteredBoards(boards);
    }, [boards]);

    const fetchBoards = async () => {
        try {
            const response = await api.get('/board', {
                params: { page, size },
            });
            setBoards(response.data.content);
            setTotalPages(response.data.totalPages);
            console.log(response.data)
        } catch (error) {
            console.error('Error fetching boards:', error);
        }
    };

    const fetchMyBoards = async () => {
        try {
            const response = await api.get(`/board/member/${memberId}`, {
                params: { page, size },
            });
            setBoards(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching my boards:', error);
        }
    };
    const fetchSearchedBoard = async () => {
        try {
            const response = await api.get('/board/search', {
                params: {title: searchTitle, page, size},
            });
            setBoards(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching searched boards:', error);
        }
    };

    const handleSearch = async () => {
        const kakao = window.kakao;
        const ps = new kakao.maps.services.Places();

        ps.keywordSearch(searchLocation, (data, status) => {
            if (status === kakao.maps.services.Status.OK) {
                const firstPlace = data[0];
                const center = { lat: firstPlace.y, lng: firstPlace.x };

                const nearbyBoards = boards.filter(board => {
                    const distance = getDistance(board.latitude, board.longitude, center.lat, center.lng);
                    return distance <= 5000;
                });

                setFilteredBoards(nearbyBoards);
                setMapCenter(center);
                setMapLevel(5);
            } else {
                setFilteredBoards([]);
                setMapLevel(9);
            }
        });
    };

    const getDistance = (lat1, lon1, lat2, lon2) => {
        const R = 6371e3;
        const φ1 = lat1 * Math.PI / 180;
        const φ2 = lat2 * Math.PI / 180;
        const Δφ = (lat2 - lat1) * Math.PI / 180;
        const Δλ = (lon2 - lon1) * Math.PI / 180;

        const a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
            Math.cos(φ1) * Math.cos(φ2) *
            Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    };

    const isValidProfileImage = (image) => {
        return image && !image.includes('null');
    };

    // 로그아웃 처리 함수
    const handleLogout = async () => {
        try {
            // 현재 쿠키 상태 로그
            console.log('Current cookies:', document.cookie);

            // 로그아웃 요청
            const response = await api.post('/members/logout', {}, { withCredentials: true });
            console.log('Logout response:', response); // 응답 확인

            // 로컬 스토리지 초기화
            localStorage.clear();
            // 로그인 페이지로 이동
            navigate('/login');
        } catch (error) {
            console.error('Logout failed:', error); // 오류 출력
        }
    }

    // 채팅하기 버튼 클릭 핸들러
    const handleChatClick = () => {
        navigate('/chat-rooms/list'); // 채팅 목록 페이지로 이동
    };

    // 팔로우 버튼 클릭 핸들러
    const handleFollowClick = () => {
        navigate('/followForm'); // 채팅 목록 페이지로 이동
    };

    const fetchBookmarkedBoards = async () => {
        try {
            const response = await api.get(`/bookmark/member/${memberId}`);
            setBookmarkedBoards(response.data);
        } catch (error) {
            console.error('Error fetching bookmarked boards:', error);
        }
    };

    const handleInputChange = (e) => {
        setSearchTitle(e.target.value);
    };

    const handleSearchSubmit = () => {
        setPage(1); // 검색 시 페이지를 초기화
        fetchSearchedBoard();
    };

    return (
        <Container>
            <AppBar position="static">
                <Toolbar style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    {/* 왼쪽: 프로필 이미지 및 닉네임 */}
                    <Box display="flex" alignItems="center">
                        <Link to="/update-member" style={{ textDecoration: 'none', color: 'inherit' }}>
                            {isValidProfileImage(profileImage) ? (
                                <img
                                    src={profileImage}
                                    alt="프로필"
                                    style={{
                                        width: '40px',
                                        height: '40px',
                                        borderRadius: '50%',
                                        objectFit: 'cover',
                                        border: '2px solid #fff', // 이미지에 테두리 추가 (선택적)
                                    }}
                                />
                            ) : (
                                <FaUserCircle size={30} style={{ color: '#fff' }} /> // 프로필 아이콘 표시
                            )}
                        </Link>
                        <Typography variant="h6" style={{ marginLeft: '20px' }}>
                            {nickname} {/* 로컬 스토리지에서 가져온 닉네임 표시 */}
                        </Typography>
                    </Box>
                    <Button color="inherit" onClick={() => navigate('/word-learning')} startIcon={<EditIcon />}>
                        단어 학습
                    </Button>
                    {/* 오른쪽: 채팅하기 버튼 */}
                    <Button color="inherit" startIcon={<ChatIcon />} onClick={handleChatClick}>
                        채팅하기
                    </Button>
                    <Button color="inherit" startIcon={<PeopleIcon />} onClick={handleFollowClick}>
                        FOLLOW 화면
                    </Button>
                    {/* 관리자 권한이 있는 경우에만 관리자 페이지 아이콘 표시 */}
                    {role === 'ADMIN' && (
                        <IconButton
                            color="inherit"
                            onClick={() => navigate('/admin/members')}
                            style={{ marginLeft: '10px' }}
                            title="관리자 페이지"
                        >
                            <AdminPanelSettingsIcon />
                        </IconButton>
                    )}
                    <Button color="inherit" onClick={handleLogout}>
                        로그아웃
                    </Button>
                </Toolbar>
            </AppBar>

            <Box mt={2}>
                <Typography variant="h4">
                    {showMyBoards
                        ? '내 글'
                        : showBookmarkedBoards
                            ? '북마크한 글'
                            : '전체 글'}
                </Typography>
                <Link to="/create-board">
                    <Button variant="contained" color="primary">게시글 작성</Button>
                </Link>
                <Button
                    variant="contained"
                    color="inherit"
                    onClick={() => setShowMyBoards(!showMyBoards)}
                    style={{ marginLeft: '10px' }}
                >
                    {showMyBoards ? '전체 글 보기' : '내 글 보기'}
                </Button>
                <Button
                    variant="contained"
                    color="inherit"
                    onClick={() => {
                        setShowBookmarkedBoards(!showBookmarkedBoards);
                        fetchBookmarkedBoards(); // 북마크한 게시글을 조회하는 함수 호출
                    }}
                    style={{ marginLeft: '10px' }}
                >
                    {showBookmarkedBoards ? '전체 글 보기' : '북마크한 글 보기'}
                </Button>
                {/* 검색 입력 필드와 버튼 */}
                <TextField
                    label="제목 검색"
                    variant="outlined"
                    value={searchTitle}
                    onChange={handleInputChange}
                    size="small"
                    style={{marginLeft: '10px'}}
                    onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                            handleSearchSubmit(); // 엔터 키 입력 시 검색 함수 호출
                        }
                    }}
                />
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSearchSubmit}
                    style={{marginLeft: '10px', height: '40px'}}
                >
                    검색
                </Button>
            </Box>

            <Grid container spacing={2} mt={2}>
                <Grid item xs={12} md={6}>
                    <List>
                        {(showBookmarkedBoards ? bookmarkedBoards : filteredBoards).map((board) => (
                            <ListItem key={board.boardId}>
                                <Grid container alignItems="center">
                                    <Grid item xs={10}>
                                        <ListItemText
                                            primary={
                                                <Link to={`/board/${board.boardId}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                                                    <strong>{board.title}</strong> -
                                                    {isValidProfileImage(board.profileImage) ? (
                                                        <img
                                                            src={board.profileImage} // Remove curly braces around board.profileImage
                                                            alt="프로필"
                                                            style={{
                                                                width: '30px',
                                                                height: '30px',
                                                                borderRadius: '50%',
                                                                objectFit: 'cover',
                                                            }}
                                                        />
                                                    ) : (
                                                        <FaUserCircle size={30} style={{ color: '#fff' }} /> // 프로필 아이콘 표시
                                                    )} {board.nickname}
                                                </Link>
                                            }
                                            secondary={
                                                <>
                                                    {board.updatedAt ? (
                                                        <span>수정일: {formatDate(board.updatedAt)}</span>
                                                    ) : (
                                                        <span>작성일: {formatDate(board.createdAt)}</span>
                                                    )}
                                                    {board.location && (
                                                        <span> 장소: {board.location}</span>
                                                    )}
                                                </>
                                            }
                                        />
                                    </Grid>
                                    {board.imageUrls && board.imageUrls.length > 0 ? (
                                        <Grid item xs={2}>
                                            <img
                                                src={board.imageUrls[0]}
                                                alt={board.title}
                                                style={{ width: '100%', height: 'auto', borderRadius: '8px' }}
                                            />
                                        </Grid>
                                    ) : (
                                        <Grid item xs={2}>
                                            {/* 이미지가 없는 경우 빈 공간으로 유지 */}
                                            <div style={{
                                                width: '100%',
                                                height: '100%',
                                                backgroundColor: '#f0f0f0',
                                                borderRadius: '8px',
                                            }}>
                                            </div>
                                        </Grid>
                                    )}
                                </Grid>
                            </ListItem>
                        ))}
                    </List>
                    <Pagination
                        count={totalPages}
                        page={page}
                        onChange={(event, value) => setPage(value)}
                        color="primary"
                    />
                </Grid>

                <Grid item xs={12} md={6}>
                    <TextField
                        label="위치를 입력하세요"
                        variant="outlined"
                        value={searchLocation}
                        onChange={(e) => setSearchLocation(e.target.value)}
                        fullWidth
                    />
                    <Button variant="contained" color="primary" onClick={handleSearch} style={{marginTop: '10px'}}>
                        검색
                    </Button>
                    <Map boards={filteredBoards} mapLevel={mapLevel} mapCenter={mapCenter}/>
                </Grid>
            </Grid>
        </Container>
    );
};

export default BoardListPage;