import React, { useState, useEffect, useContext, useCallback } from 'react';
import { 
  Container, 
  Typography, 
  Box, 
  CircularProgress, 
  Paper, 
  Button, 
  TextField, 
  Divider, 
  Pagination,
  Alert,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton
} from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import { KeycloakContext } from '../context/KeycloakContext';
import { TopicResponseDto } from '../lib/topicService';
import { ParentCommentResponseDto, CommentPage } from '../lib/commentService';
import apiProxy from '../lib/apiProxy';
import Comment from '../components/Comment';
import { AccountCircle, AttachFile, Delete } from '@mui/icons-material';

const topicAsciiArt = `
  ______                               
 |  ____|                              
 | |__    ___   _ __  _   _  _ __ ___  
 |  __|  / _ \\ | '__|| | | || '_ \` _ \\ 
 | |    | (_) || |   | |_| || | | | | |
 |_|     \\___/ |_|    \\__,_||_| |_| |_|
`;

const TopicPage = () => {
  const { topicId } = useParams<{ topicId: string }>();
  const navigate = useNavigate();
  const { authenticated, login, userDetails } = useContext(KeycloakContext);
  
  const [topic, setTopic] = useState<TopicResponseDto | null>(null);
  const [comments, setComments] = useState<ParentCommentResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [commentContent, setCommentContent] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [commentsPerPage] = useState(10);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [deletingTopic, setDeletingTopic] = useState(false);
  
  const fetchTopic = useCallback(async () => {
    if (!topicId) return;
    
    try {
      const response = await apiProxy.topics.getById(topicId);
      setTopic(response.data);
      setError('');
    } catch (err: any) {
      if (err.response?.status === 404) {
        setError('Topic not found');
      } else {
        setError(`Failed to load topic: ${err.message}`);
      }
      setTopic(null);
    }
  }, [topicId]);
  
  const fetchComments = useCallback(async (page: number) => {
    if (!topicId) return;
    
    try {
      const response = await apiProxy.comments.getForTopic(topicId, page - 1, commentsPerPage);
      const data = response.data as CommentPage;
      
      setComments(data.content || []);
      setTotalPages(data.totalPages || 1);
      setCurrentPage(page);
      setError('');
    } catch (err: any) {
      setError(`Failed to load comments: ${err.message}`);
      setComments([]);
    }
  }, [topicId, commentsPerPage]);
  
  useEffect(() => {
    setLoading(true);
    
    const loadData = async () => {
      await fetchTopic();
      await fetchComments(1);
      setLoading(false);
    };
    
    loadData();
  }, [fetchTopic, fetchComments]);
  
  const handleCommentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentContent.trim() || !topicId) return;
    
    if (!authenticated) {
      setError('Please login to comment');
      setTimeout(() => {
        login();
      }, 1500);
      return;
    }
    
    setSubmittingComment(true);
    
    try {
      await apiProxy.comments.create({
        text: commentContent,
        topicId,
        parentCommentId: null
      });
      
      setCommentContent('');
      fetchComments(currentPage);
      setError('');
    } catch (err: any) {
      setError(`Failed to post comment: ${err.message}`);
    } finally {
      setSubmittingComment(false);
    }
  };
  
  const handlePageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    fetchComments(page);
  };
  
  const handleCommentDelete = (commentId: string) => {
    fetchComments(currentPage);
  };
  
  const formatDate = (dateValue: string | number[]) => {
    if (Array.isArray(dateValue)) {
      const [year, month, day, hour, minute] = dateValue;
      return new Date(year, month - 1, day, hour, minute).toLocaleString();
    } else {
      return new Date(dateValue).toLocaleString();
    }
  };

  const handleDeleteClick = () => {
    setOpenDeleteDialog(true);
  };

  const handleCloseDeleteDialog = () => {
    if (deletingTopic) return;
    setOpenDeleteDialog(false);
  };

  const handleConfirmDelete = async () => {
    if (!topicId) return;
    
    setDeletingTopic(true);
    try {
      await apiProxy.topics.delete(topicId);
      setOpenDeleteDialog(false);
      navigate('/home');
    } catch (err: any) {
      setError(`Failed to delete topic: ${err.message}`);
    } finally {
      setDeletingTopic(false);
      setOpenDeleteDialog(false);
    }
  };

  const isTopicAuthor = userDetails?.id === topic?.authorId;

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      
      <Box sx={{ textAlign: 'center', mb: 4 }}>
        <Typography
          component="pre"
          sx={{
            fontFamily: "'VT323', monospace",
            fontSize: { xs: '0.8rem', sm: '1rem', md: '1.2rem' },
            lineHeight: 1.2,
            color: 'var(--success-color)',
            textAlign: 'center',
            margin: '0 auto',
            whiteSpace: 'pre',
            overflow: 'hidden'
          }}
        >
          {topicAsciiArt}
        </Typography>
      </Box>
      
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress sx={{ color: 'var(--accent-color)' }} />
        </Box>
      ) : topic ? (
        <>
          <Paper 
            elevation={0} 
            sx={{ 
              p: 3, 
              mb: 4, 
              bgcolor: 'var(--bg-color)', 
              border: '1px solid var(--border-color)',
              position: 'relative'
            }}
          >
            {isTopicAuthor && (
              <IconButton
                size="small"
                color="error"
                onClick={handleDeleteClick}
                sx={{
                  position: 'absolute',
                  right: 16,
                  top: 16,
                  '&:hover': {
                    backgroundColor: 'rgba(255, 0, 0, 0.1)'
                  }
                }}
                aria-label="delete topic"
              >
                <Delete />
              </IconButton>
            )}
            <Typography 
              variant="h5" 
              sx={{ 
                mb: 2, 
                fontWeight: 'bold',
                fontFamily: "'VT323', monospace",
                fontSize: "1.8rem",
                color: "var(--accent-color)"
              }}
            >
              {topic.title}
            </Typography>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
              <Typography variant="body2" color="textSecondary" sx={{ 
                display: "flex", 
                alignItems: "center", 
                gap: 0.5,
                fontFamily: "'Courier Prime', monospace"
              }}>
                <AccountCircle fontSize="small" />
                Posted by: {topic.authorName || (topic.author && topic.author.name) || 'Unknown'}
              </Typography>
              <Typography variant="body2" color="textSecondary" sx={{ fontFamily: "'Courier Prime', monospace" }}>
                {topic.createdAt ? formatDate(topic.createdAt) : ''}
              </Typography>
            </Box>
            
            <Divider sx={{ mb: 3 }} />
            
            <Typography variant="body1" sx={{ 
              whiteSpace: 'pre-wrap',
              fontFamily: "monospace",
              mb: 2
            }}>
              {topic.content}
            </Typography>
            
            {topic.files && topic.files.length > 0 && (
              <Box sx={{ 
                mt: 3,
                p: 2,
                borderTop: "1px dashed var(--border-color)"
              }}>
                <Typography variant="subtitle2" sx={{ 
                  fontFamily: "'VT323', monospace",
                  color: "var(--accent-color)",
                  mb: 1
                }}>
                  Attachments:
                </Typography>
                <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
                  {topic.files.map((file, index) => {
                    const fileName = file.minioObjectName.split('/').pop() || '';
                    const fileExtension = fileName.split('.').pop() || '';
                    const fileUrl = `http://localhost:9000/forum/${fileName}`;
                    
                    return (
                      <Box
                        key={index}
                        sx={{
                          display: "flex",
                          alignItems: "center", 
                          gap: 0.5,
                          border: "1px solid var(--border-color)",
                          borderRadius: "4px",
                          p: 1,
                          cursor: "pointer",
                          '&:hover': {
                            backgroundColor: 'rgba(255, 255, 255, 0.05)'
                          }
                        }}
                        component="a"
                        href={fileUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <AttachFile fontSize="small" />
                        <Typography variant="body2" sx={{ fontFamily: "'Courier Prime', monospace" }}>
                          .{fileExtension}
                        </Typography>
                      </Box>
                    );
                  })}
                </Box>
              </Box>
            )}
          </Paper>
          
          <Box sx={{ mb: 4 }}>
            <Typography 
              variant="h6" 
              sx={{ 
                mb: 3,
                fontFamily: "'VT323', monospace",
                fontSize: "1.5rem"
              }}
            >
              Leave a Comment
            </Typography>
            
            <form onSubmit={handleCommentSubmit}>
              <TextField
                multiline
                rows={4}
                fullWidth
                placeholder="Write your comment..."
                value={commentContent}
                onChange={(e) => setCommentContent(e.target.value)}
                sx={{ mb: 2 }}
                disabled={submittingComment}
              />
              
              <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Button 
                  variant="contained" 
                  type="submit" 
                  disabled={!commentContent.trim() || submittingComment}
                  sx={{
                    fontFamily: "'VT323', monospace",
                    bgcolor: "var(--accent-color)",
                    color: "var(--bg-color)",
                    '&:hover': {
                      bgcolor: "var(--accent-color-dark)",
                      color: "var(--bg-color)"
                    }
                  }}
                >
                  {submittingComment ? 'Posting...' : 'Post Comment'}
                </Button>
              </Box>
            </form>
          </Box>
          
          <Box sx={{ mb: 4 }}>
            <Typography 
              variant="h6" 
              sx={{ 
                mb: 3,
                fontFamily: "'VT323', monospace",
                fontSize: "1.5rem"
              }}
            >
              Comments ({comments.length})
            </Typography>
            
            {comments.length > 0 ? (
              <>
                {comments.map((comment) => (
                  <Comment 
                    key={comment.id} 
                    comment={comment} 
                    onDelete={handleCommentDelete} 
                  />
                ))}
                
                {totalPages > 1 && (
                  <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                    <Pagination 
                      count={totalPages} 
                      page={currentPage} 
                      onChange={handlePageChange}
                      sx={{
                        '& .MuiPaginationItem-root': {
                          fontFamily: "'VT323', monospace",
                          fontSize: "1.2rem",
                          color: "var(--text-color)",
                          border: "1px solid var(--border-color)",
                        },
                        '& .MuiPaginationItem-root.Mui-selected': {
                          backgroundColor: "var(--secondary-color)",
                          color: "var(--bg-color)",
                        }
                      }}
                    />
                  </Box>
                )}
              </>
            ) : (
              <Typography 
                variant="body1" 
                sx={{ 
                  textAlign: 'center', 
                  color: 'text.secondary', 
                  my: 4,
                  fontFamily: "'Courier Prime', monospace"
                }}
              >
                No comments yet. Be the first to comment!
              </Typography>
            )}
          </Box>
        </>
      ) : (
        <Typography 
          variant="h6" 
          sx={{ 
            textAlign: 'center', 
            my: 4,
            fontFamily: "'VT323', monospace" 
          }}
        >
          Topic not found or has been removed.
        </Typography>
      )}

      <Dialog
        open={openDeleteDialog}
        onClose={handleCloseDeleteDialog}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
        PaperProps={{
          sx: {
            backgroundColor: 'var(--card-bg, #fff)',
            color: 'var(--text-color, #000)',
            border: '2px solid var(--danger-color, red)',
            boxShadow: '0 4px 20px rgba(0, 0, 0, 0.2)',
            padding: '8px'
          }
        }}
      >
        <DialogTitle id="alert-dialog-title" sx={{ fontFamily: "'VT323', monospace", fontSize: '1.5rem' }}>
          Confirm Delete Topic
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description" sx={{ color: 'var(--text-color, #000)' }}>
            <strong>Are you sure you want to delete this topic?</strong><br/>
            <Box sx={{ 
              mt: 1, 
              p: 1, 
              backgroundColor: 'rgba(0,0,0,0.05)',
              borderLeft: '3px solid var(--accent-color, #1976d2)',
              fontStyle: 'italic'
            }}>
              "{topic?.title}"
            </Box>
            <Box sx={{ mt: 1 }}>
              This action cannot be undone. All comments and replies will also be deleted.
            </Box>
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button 
            onClick={handleCloseDeleteDialog} 
            disabled={deletingTopic}
            sx={{ 
              color: 'var(--text-color, #000)',
              fontFamily: "'VT323', monospace",
              fontWeight: 'bold'
            }}
          >
            Cancel
          </Button>
          <Button 
            onClick={handleConfirmDelete} 
            disabled={deletingTopic}
            autoFocus
            variant="contained"
            sx={{ 
              backgroundColor: 'var(--danger-color, red)',
              fontFamily: "'VT323', monospace",
              fontWeight: 'bold',
              color: 'white',
              '&:hover': {
                backgroundColor: 'darkred'
              }
            }}
          >
            {deletingTopic ? (
              <CircularProgress size={20} sx={{ color: 'white' }} />
            ) : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default TopicPage; 