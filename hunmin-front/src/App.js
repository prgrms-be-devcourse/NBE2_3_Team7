import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate, useLocation } from 'react-router-dom';
import Header from './header/Header';
import BoardListPage from './board/BoardListPage';
import BoardDetailPage from './board/BoardDetailPage';
import CreateBoardPage from './board/CreateBoardPage';
import RegistrationForm from './member/RegistrationForm';
import LoginForm from './member/LoginForm';
import UpdateMemberForm from "./member/UpdateMemberForm";
import NoticeListPage from './notice/NoticeListPage';
import NoticeDetailPage from "./notice/NoticeDetailPage";
import CreateNoticePage from './notice/CreateNoticePage';
import ChatRoomList  from "./chat-room/ChatRoomList";
import CreateChatRoom from "./chat-room/CreateChatRoom";
import ChatRoomDetail from "./chat-room/ChatRoomDetail";
import WordLearningPage from "./word/WordLearningPage";
import WordManagementPage from "./word/WordManagementPage";
import WordListPage from "./word/WordListPage";
import WordViewPage from "./word/WordViewPage";
import WordRegisterPage from "./word/WordRegisterPage";
import WordEditPage from "./word/WordEditPage";
import LevelSelectPage from "./word/LevelSelectPage";
import LanguageSelectPage from "./word/LanguageSelectPage";
import LearningPage from "./word/LearningPage";
import TestLanguageSelectPage from './word/TestLanguageSelectPage';
import TestLevelSelectPage from './word/TestLevelSelectPage';
import TestPage from './word/TestPage';
import TestRecords from './word/TestRecords';
import PasswordVerify from './member/PasswordVerify';
import PasswordUpdate from './member/PasswordUpdate';

import FollowForm from './follow/followForm'; // followForm 컴포넌트 임포트

import AdminMembersList from "./admin/AdminMemberList";
import AdminMemberPostsAndComments from "./admin/AdminMemberPostsAndComments";
import AdminMemberDetail from "./admin/AdminMemberDetail";

const App = () => {
    const [token, setToken] = useState(localStorage.getItem('token') || '');


    return (
        <Router>
            <AppContent token={token} setToken={setToken} /> {/* setToken 전달 */}
        </Router>
    );
};

const AppContent = ({ token, setToken }) => {
    const location = useLocation();
    const hideHeaderRoutes = ['/login', '/register', '/password/verify', '/password/update'];

    return (
        <>
            {!hideHeaderRoutes.includes(location.pathname) && token && <Header />}
            <Routes>
                <Route path="/login" element={<LoginForm setToken={setToken} />} />
                <Route path="/register" element={<RegistrationForm />} />
                <Route path="/password/verify" element={<PasswordVerify />} />
                <Route path="/password/update" element={<PasswordUpdate />} />
                {token ? (
                    <>
                        <Route path="/" element={<BoardListPage />} />
                        <Route path="/board/:boardId" element={<BoardDetailPage />} />
                        <Route path="/create-board" element={<CreateBoardPage />} />
                        <Route path="/update-member" element={<UpdateMemberForm />} />

                        {/* 공지사항 라우트 */}
                        <Route path="/notices" element={<NoticeListPage />} />
                        <Route path="/notices/:id" element={<NoticeDetailPage />} />
                        <Route path="/create-notice" element={<CreateNoticePage />} />
                        <Route path="/edit-notice/:id" element={<CreateNoticePage />} />

                        {/* 채팅 기능 라우트 */}
                        <Route path="/chat-rooms/list" element={<ChatRoomList />} />
                        <Route path="/chat-room/create" element={<CreateChatRoom />} />
                        <Route path="/chat-room/:chatRoomId" element={<ChatRoomDetail />} />

                        {/* 단어 관리 시스템 라우트 추가 */}
                        <Route path="/word-management" element={<WordManagementPage />} />
                        <Route path="/word-learning" element={<WordLearningPage />} />
                        <Route path="/word-list" element={<WordListPage />} />
                        <Route path="/word-view" element={<WordViewPage />} />
                        <Route path="/word-register" element={<WordRegisterPage />} />
                        <Route path="/word-edit" element={<WordEditPage />} />
                        <Route path="/word-learning/levelSelect" element={<LevelSelectPage />} />
                        <Route path="/word-learning/languageSelect" element={<LanguageSelectPage />} />
                        <Route path="/word-learning/start" element={<LearningPage />} />
                        <Route path="/word-test/languageSelect" element={<TestLanguageSelectPage />} />
                        <Route path="/word-test/levelSelect" element={<TestLevelSelectPage />} />
                        <Route path="/word-test/start" element={<TestPage />} />
                        <Route path="/word-test/records" element={<TestRecords />} />

                        {/* 팔로우 기능 시스템 라우트 추가 */}
                        <Route path="/followForm" element={<FollowForm />} />

                        {/* 관리자 라우트 추가 */}
                        <Route path="/admin/members" element={<AdminMembersList />} />
                        <Route path="/admin/member/:memberId" element={<AdminMemberDetail />} />
                        <Route path="/admin/member/:memberId/posts-comments" element={<AdminMemberPostsAndComments />} />

                        )
                    </>
                ) : (
                    <Route path="*" element={<Navigate to="/login" />} />
                )}
            </Routes>
        </>
    );
};

export default App;
