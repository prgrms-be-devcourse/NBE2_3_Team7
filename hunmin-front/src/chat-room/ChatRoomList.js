import React, {useState, useEffect} from 'react';
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
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import DeleteIcon from '@mui/icons-material/Delete';

const ChatRoomCard = ({room, onEnter, onRightClick}) => {
    return (
        <Card
            sx={{marginBottom: 2, cursor: 'pointer', width: '100%'}}
            onClick={() => onEnter(room.chatRoomId)}
            onContextMenu={(e) => onRightClick(e, room.chatRoomId)}
        >
            <CardHeader
                title={`채팅방 (${room.nickName || 'Unknown'}, ${room.partnerName || 'N/A'})`}
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
    const [chatRooms, setChatRooms] = useState([]);
    const [anchorEl, setAnchorEl] = useState(null);
    const [selectedChatRoom, setSelectedChatRoom] = useState(null);
    const [snackbar, setSnackbar] = useState({open: false, message: '', severity: 'success'});
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);  // 삭제 Dialog 상태 추가
    const [openCreateDialog, setOpenCreateDialog] = useState(false);  // 생성 Dialog 상태 추가
    const [newNickName, setNewNickName] = useState('');
    const [partnerName, setPartnerName] = useState('');  // 파트너 이름 상태 추가

    useEffect(() => {
        // 인증된 사용자인지 확인 후 요청
        api.get('/chat-room/list')
            .then(response => {
                console.log(response.data);
                setChatRooms(response.data);
            })
            .catch(error => {
                console.error("Error fetching chat rooms:", error);
                setSnackbar({
                    open: true,
                    message: '채팅방 목록을 불러오는데 실패했습니다.',
                    severity: 'error',
                });
            });
    }, []);

    const enterRoom = (chatRoomId) => {
        window.location.href = `/chat-room/${chatRoomId}`;
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

    // 삭제 버튼 클릭 시 파트너 이름 입력 Dialog 열기
    const openPartnerNameDialog = () => {
        setOpenDeleteDialog(true);
    };

    // 채팅방 삭제 요청
    const deleteChatRoom = () => {
        if (!partnerName) {
            setSnackbar({
                open: true,
                message: '채팅상대 닉네임을 입력해주세요.',
                severity: 'warning',
            });
            return;
        }
        if (selectedChatRoom) {
            api.delete(`/chat-room/${selectedChatRoom}/${partnerName}`)
                .then((response) => {
                    if (response.data === true) { // 서버에서 true를 반환하면 성공 처리
                        setChatRooms(chatRooms.filter(room => room.chatRoomId !== selectedChatRoom));
                        setSnackbar({
                            open: true,
                            message: '채팅방이 성공적으로 삭제되었습니다.',
                            severity: 'success',
                        });
                        handleCloseDeleteDialog();
                    } else {
                        setSnackbar({
                            open: true,
                            message: '닉네임이 일치하지 않습니다.',
                            severity: 'error',
                        });
                        handleCloseDeleteDialog();
                    }
                })
                .catch(error => {
                    console.error("Error deleting chat room:", error);
                    setSnackbar({
                        open: true,
                        message: '채팅방 삭제에 실패했습니다.',
                        severity: 'error',
                    });
                    handleCloseDeleteDialog();
                });
            handleMenuClose();
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbar({...snackbar, open: false});
    };

    // 삭제 다이얼로그 닫기
    const handleCloseDeleteDialog = () => {
        setOpenDeleteDialog(false);
        setPartnerName('');
    };

    // 생성 다이얼로그 닫기
    const handleCloseCreateDialog = () => {
        setOpenCreateDialog(false);
        setNewNickName('');
    };

    const handleCreateChatRoom = () => {
        if (newNickName.trim() === '') {
            setSnackbar({
                open: true,
                message: '닉네임을 입력해주세요.',
                severity: 'warning',
            });
            return;
        }

        api.post(`/chat-room/${newNickName}`)
            .then(response => {
                setChatRooms([...chatRooms, response.data]);
                setSnackbar({
                    open: true,
                    message: '채팅방이 성공적으로 생성되었습니다.',
                    severity: 'success',
                });
                handleCloseCreateDialog();
            })
            .catch(error => {
                console.error("Error creating chat room:", error);
                setSnackbar({
                    open: true,
                    message: '채팅방 생성에 실패했습니다.',
                    severity: 'error',
                });
                handleCloseDeleteDialog();
            });
    };

    return (
        <Box sx={{maxWidth: 700, margin: 'auto', padding: 2}}>
            <Typography variant="h4" gutterBottom>
                채팅방 목록
            </Typography>
            <Button variant="contained" color="primary" onClick={() => setOpenCreateDialog(true)}
                    sx={{marginBottom: 2}}>
                채팅방 생성
            </Button>
            <List sx={{width: '100%'}}>
                {chatRooms.map(room => (
                    <React.Fragment key={room.chatRoomId}>
                        <ListItem>
                            <ChatRoomCard
                                room={room}
                                onEnter={enterRoom}
                                onRightClick={handleMenuOpen}
                            />
                        </ListItem>
                        <Divider component="li"/>
                    </React.Fragment>
                ))}
            </List>
            <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                }}
                transformOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                }}
            >
                <MenuItem onClick={openPartnerNameDialog}>
                    <DeleteIcon fontSize="small" sx={{marginRight: 1}}/>
                    채팅방 삭제
                </MenuItem>
            </Menu>

            {/* Snackbar */}
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

            {/* 삭제 다이얼로그 */}
            <Dialog open={openDeleteDialog} onClose={handleCloseDeleteDialog}>
                <DialogTitle>채팅방 삭제하기</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        채팅방의 상대방 유저의 닉네임을 입력해주세요.
                    </DialogContentText>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="삭제"
                        placeholder="상대방 닉네임"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={partnerName}
                        onChange={(e) => setPartnerName(e.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDeleteDialog}>취소</Button>
                    <Button onClick={deleteChatRoom}>삭제</Button>
                </DialogActions>
            </Dialog>

            {/* 채팅방 생성 다이얼로그 */}
            <Dialog open={openCreateDialog} onClose={handleCloseCreateDialog}>
                <DialogTitle>채팅방 생성</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        채팅 상대 닉네임을 입력해주세요.
                    </DialogContentText>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="닉네임"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={newNickName}
                        onChange={(e) => setNewNickName(e.target.value)}
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
