import api from '../axios';

const createComment = (boardId, commentData) => {
    return api.post(`/board/${boardId}/comment`, commentData); // Create comment on a board
};

const createChildComment = (boardId, commentId, commentData) => {
    return api.post(`/board/${boardId}/comment/${commentId}`, commentData); // Create child comment (reply)
};

const updateComment = (boardId, commentId, commentData) => {
    return api.put(`/board/${boardId}/comment/${commentId}`, commentData); // Update comment
};

const deleteComment = (boardId, commentId) => {
    return api.delete(`/board/${boardId}/comment/${commentId}`); // Delete comment
};

const getCommentsByBoard = (boardId, params) => {
    return api.get(`/board/${boardId}/comment`, { params }); // Get comments by board
};

const likeComment = (commentId) => {
    return api.post(`/likeComment/${commentId}`); // 좋아요 추가
};

const unlikeComment = (commentId) => {
    return api.delete(`/likeComment/${commentId}`); // 좋아요 취소
};


const getCommentLikeMembers = (commentId) => {
    return api.get(`/likeComment/${commentId}/members`); // 좋아요 누른 회원 목록 조회
};

export {
    createComment,
    createChildComment,
    updateComment,
    deleteComment,
    getCommentsByBoard,
    likeComment,
    unlikeComment,
    getCommentLikeMembers,
};
