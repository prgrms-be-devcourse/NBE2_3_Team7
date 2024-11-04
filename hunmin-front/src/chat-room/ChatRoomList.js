import React, {useState, useEffect, useRef} from 'react';
import api from '../axios';
import {
    Box,
    Card,
    CardHeader,
    CardContent,
    Typography,
    IconButton,
    Menu,
    MenuItem,
    List,
    ListItem,
    Divider,
    Snackbar,
    Alert,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    TextField,
    CircularProgress
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import DeleteIcon from '@mui/icons-material/Delete';
import ChatRoomInfo from './ChatRoomInfo';

const ChatRoomCard = ({room, onEnter, onRightClick}) => {
    const currentMemberId = localStorage.getItem('memberId');
    const displayName = room.memberId === Number(currentMemberId) ? room.partnerName : room.nickName;

    return (
        <Card
            sx={{marginBottom: 2, cursor: 'pointer', width: '100%'}}
            onClick={() => onEnter(room.chatRoomId)}
            onContextMenu={(e) => onRightClick(e, room.chatRoomId)}
        >
            <CardHeader
                title={`채팅방 : ${displayName}`}
                action={
                    <IconButton onClick={(e) => onRightClick(e, room.chatRoomId)}>
                        <MoreVertIcon/>
                    </IconButton>
                }
            />
            <CardContent>
                <Typography variant="body2" color="text.secondary">
                    생성일: {room.createdAt ? new Date(room.createdAt).toLocaleString() : 'N/A'}
                </Typography>
            </CardContent>
        </Card>
    );
};

const ChatRoomList = () => {
    const [selectedChatRoomId, setSelectedChatRoomId] = useState(null);
    const [chatRooms, setChatRooms] = useState([]);
    const [anchorEl, setAnchorEl] = useState(null);
    const [selectedChatRoom, setSelectedChatRoom] = useState(null);
    const [snackbar, setSnackbar] = useState({open: false, message: '', severity: 'success'});
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [openCreateDialog, setOpenCreateDialog] = useState(false);
    const [newNickName, setNewNickName] = useState('');
    const [partnerName, setPartnerName] = useState('');
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const listInnerRef = useRef();

    useEffect(() => {
        fetchChatRooms(page);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const fetchChatRooms = async (pageNumber) => {
        if (isLoading || !hasMore) return;

        setIsLoading(true);
        try {
            const response = await api.get('/chat-room/list', {
                params: {page: pageNumber, size: 10},
            });

            const newChatRooms = response.data.content;

            if (newChatRooms.length === 0) {
                setHasMore(false);
            } else {
                setChatRooms((prevChatRooms) => [...prevChatRooms, ...newChatRooms]);
                setPage((prevPage) => prevPage + 1);
            }
        } catch (error) {
            console.error('Error fetching chat rooms:', error);
            setSnackbar({
                open: true,
                message: '채팅방 목록을 불러오는데 실패했습니다.',
                severity: 'error',
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleScroll = () => {
        if (listInnerRef.current) {
            const {scrollTop, scrollHeight, clientHeight} = listInnerRef.current;
            if (scrollTop + clientHeight >= scrollHeight - 10) {
                fetchChatRooms(page);
            }
        }
    };

    useEffect(() => {
        if (listInnerRef.current) {
            listInnerRef.current.addEventListener('scroll', handleScroll);
        }
        return () => {
            if (listInnerRef.current) {
                listInnerRef.current.removeEventListener('scroll', handleScroll);
            }
        };
    }, [page]);

    const enterRoom = (chatRoomId) => {
        window.location.href = `/chat-room/${chatRoomId}`;
        setSelectedChatRoomId(chatRoomId);
    };

    const handleMenuOpen = (event, chatRoomId) => {
        event.stopPropagation();
        setAnchorEl(event.currentTarget);
        setSelectedChatRoom(chatRoomId);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
        setSelectedChatRoom(null);
    };

    const openPartnerNameDialog = () => {
        setOpenDeleteDialog(true);
    };

    const deleteChatRoom = async () => {
        if (!partnerName) {
            setSnackbar({
                open: true,
                message: '정말 삭제 하시겠습니까?',
                severity: 'warning',
            });
            return;
        }
        try {
            const response = await api.delete(`/chat-room/${selectedChatRoom}`);
            if (response.status === 200) {
                setChatRooms(chatRooms.filter(room => room.chatRoomId !== selectedChatRoom));
                setSnackbar({
                    open: true,
                    message: '채팅방이 성공적으로 삭제되었습니다.',
                    severity: 'success',
                });
            } else {
                throw new Error('채팅방 삭제 실패');
            }
        } catch (error) {
            console.error("Error deleting chat room:", error);
            setSnackbar({
                open: true,
                message: '채팅방 삭제에 실패했습니다.',
                severity: 'error',
            });
        } finally {
            handleCloseDeleteDialog();
            handleMenuClose();
        }
    };

    const handleCloseSnackbar = () => setSnackbar({...snackbar, open: false});
    const handleCloseDeleteDialog = () => {
        setOpenDeleteDialog(false);
        setPartnerName('');
    };
    const handleCloseCreateDialog = () => {
        setOpenCreateDialog(false);
        setNewNickName('');
    };

    const handleCreateChatRoom = async () => {
        if (newNickName.trim() === '') {
            setSnackbar({
                open: true,
                message: '닉네임을 입력해주세요.',
                severity: 'warning',
            });
            return;
        }
        try {
            const response = await api.post(`/chat-room/${newNickName}`);
            setChatRooms([...chatRooms, response.data]);
            setSnackbar({
                open: true,
                message: '채팅방이 성공적으로 생성되었습니다.',
                severity: 'success',
            });
        } catch (error) {
            console.error("Error creating chat room:", error);
            setSnackbar({
                open: true,
                message: '채팅방 생성에 실패했습니다.',
                severity: 'error',
            });
        } finally {
            handleCloseCreateDialog();
        }
    };

    return (
        <Box sx={{maxWidth: 700, margin: 'auto', padding: 2}}>
            {selectedChatRoomId ? (
                <ChatRoomInfo chatRoomId={selectedChatRoomId}/>
            ) : (
                <>
                    <Typography variant="h4" gutterBottom>
                        채팅방 목록
                    </Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => setOpenCreateDialog(true)}
                        sx={{marginBottom: 2}}
                    >
                        채팅방 생성
                    </Button>
                    <Box
                        ref={listInnerRef}
                        sx={{
                            height: '500px', // 원하는 높이로 설정
                            overflowY: 'auto',
                            border: '1px solid #ddd',
                            borderRadius: '4px',
                            padding: '8px',
                        }}
                    >
                        <List sx={{width: '100%'}}>
                            {chatRooms.map((room) => (
                                <React.Fragment key={room.chatRoomId}>
                                    <ListItem>
                                        <ChatRoomCard room={room} onEnter={enterRoom} onRightClick={handleMenuOpen}/>
                                    </ListItem>
                                    <Divider component="li"/>
                                </React.Fragment>
                            ))}
                            {isLoading && (
                                <Box sx={{display: 'flex', justifyContent: 'center', padding: 2}}>
                                    <CircularProgress/>
                                </Box>
                            )}
                        </List>
                    </Box>
                </>
            )}
            <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                anchorOrigin={{vertical: 'top', horizontal: 'right'}}
                transformOrigin={{vertical: 'top', horizontal: 'right'}}
            >
                <MenuItem onClick={openPartnerNameDialog}>
                    <DeleteIcon fontSize="small" sx={{marginRight: 1}}/>
                    채팅방 삭제
                </MenuItem>
            </Menu>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{vertical: 'bottom', horizontal: 'center'}}
            >
                <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{width: '100%'}}>
                    {snackbar.message}
                </Alert>
            </Snackbar>

            <Dialog open={openDeleteDialog} onClose={handleCloseDeleteDialog}>
                <DialogTitle>채팅방 삭제하기</DialogTitle>
                <DialogContent>
                    <DialogContentText>정말 삭제 하시겠습니까? "네"를 입력해주세요.</DialogContentText>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="삭제"
                        placeholder="네"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={partnerName}
                        onChange={(e) => setPartnerName(e.target.value)}
                        onKeyPress={(e) => {
                            if (e.key === 'Enter' && partnerName.trim() === '네') {
                                deleteChatRoom();
                            }
                        }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDeleteDialog}>취소</Button>
                    <Button
                        onClick={deleteChatRoom}
                        disabled={partnerName.trim() !== '네'}
                    >
                        삭제
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog open={openCreateDialog} onClose={handleCloseCreateDialog}>
                <DialogTitle>채팅방 생성</DialogTitle>
                <DialogContent>
                    <DialogContentText>채팅 상대 닉네임을 입력해주세요.</DialogContentText>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="닉네임"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={newNickName}
                        onChange={(e) => setNewNickName(e.target.value)}
                        onKeyPress={(e) => {
                            if (e.key === 'Enter') {
                                handleCreateChatRoom();
                            }
                        }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseCreateDialog}>취소</Button>
                    <Button onClick={handleCreateChatRoom}>생성</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default ChatRoomList;
