import React, { useState, useContext, useEffect } from 'react';
import { 
  Card, 
  CardContent, 
  Typography, 
  Box, 
  Avatar, 
  Button, 
  TextField, 
  IconButton,
  Collapse,
  Divider,
  Pagination,
  CircularProgress
} from '@mui/material';
import { Reply, Delete, ExpandMore, ExpandLess } from '@mui/icons-material';
import { ParentCommentResponseDto, ReplyResponseDto } from '../lib/commentService';
import { KeycloakContext } from '../context/KeycloakContext';
import apiProxy from '../lib/apiProxy';
import ReplyItem from './ReplyItem';

interface CommentProps {
  comment: ParentCommentResponseDto;
  onDelete: (commentId: string) => void;
}

const Comment: React.FC<CommentProps> = ({ comment, onDelete }) => {
  const { authenticated, userDetails, login } = useContext(KeycloakContext);
  const [showReplyForm, setShowReplyForm] = useState(false);
  const [replyContent, setReplyContent] = useState('');
  const [showReplies, setShowReplies] = useState(false);
  const [replies, setReplies] = useState<ReplyResponseDto[]>([]);
  const [repliesPage, setRepliesPage] = useState(1);
  const [repliesPageCount, setRepliesPageCount] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [localReplyCount, setLocalReplyCount] = useState(comment.replyCount);

  const isCommentAuthor = userDetails?.id === comment.authorId;
  const hasReplies = localReplyCount > 0;
  const MAX_REPLIES_PER_PAGE = 5;

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  const toggleReplyForm = () => {
    if (!authenticated) {
      setError('Please login to reply');
      setTimeout(() => {
        login();
      }, 1500);
      return;
    }
    setShowReplyForm(!showReplyForm);
  };

  const handleReplySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!replyContent.trim()) return;

    try {
      const response = await apiProxy.comments.create({
        text: replyContent,
        topicId: comment.topicId,
        parentCommentId: comment.id
      });
      
      setReplyContent('');
      setShowReplyForm(false);
      
      setLocalReplyCount(prev => prev + 1);
      
      setShowReplies(true);
      
      await loadReplies(1);
      
    } catch (err: any) {
      setError(`Failed to post reply: ${err.message}`);
    }
  };

  const loadReplies = async (page: number) => {
    if (loading) return;
    
    setLoading(true);
    setError('');
    
    try {
      const response = await apiProxy.comments.getReplies(comment.id, page - 1, MAX_REPLIES_PER_PAGE);
      
      if (response.data && response.data.content) {
        setReplies(response.data.content);
        setRepliesPageCount(response.data.totalPages || 1);
        setRepliesPage(page);
      } else {
        setReplies([]);
        setRepliesPageCount(1);
      }
    } catch (err: any) {
      setError(`Failed to load replies: ${err.message}`);
      console.error("Error loading replies:", err);
    } finally {
      setLoading(false);
    }
  };

  const toggleReplies = () => {
    const newShowReplies = !showReplies;
    setShowReplies(newShowReplies);
    
    if (newShowReplies) {
      loadReplies(1);
    }
  };

  const handlePageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    loadReplies(page);
  };

  const handleDeleteComment = async () => {
    try {
      await apiProxy.comments.delete(comment.id);
      onDelete(comment.id);
    } catch (err: any) {
      setError(`Failed to delete comment: ${err.message}`);
    }
  };

  useEffect(() => {
    if (comment.replyCount > localReplyCount) {
      setLocalReplyCount(comment.replyCount);
      
      if (showReplies) {
        loadReplies(1);
      }
    }
  }, [comment.replyCount]);

  return (
    <Card sx={{ 
      mb: 2, 
      bgcolor: 'var(--bg-color)', 
      border: '1px solid var(--border-color)',
      '&:hover': {
        borderColor: 'var(--accent-color)',
      } 
    }}>
      <CardContent>
        {error && (
          <Typography color="error" sx={{ mb: 2, fontFamily: "'Courier Prime', monospace" }}>
            {error}
          </Typography>
        )}
        
        <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 1 }}>
          <Avatar sx={{ mr: 2, bgcolor: 'var(--accent-color)' }}>
            {comment.authorName ? comment.authorName.charAt(0).toUpperCase() : 'U'}
          </Avatar>
          
          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="subtitle1" sx={{ 
                fontWeight: 'bold',
                fontFamily: "'VT323', monospace"
              }}>
                {comment.authorName || 'Unknown User'}
              </Typography>
              <Typography variant="caption" color="textSecondary" sx={{ 
                fontFamily: "'Courier Prime', monospace" 
              }}>
                {formatDate(comment.createdAt)}
              </Typography>
            </Box>
            
            <Typography variant="body1" sx={{ 
              mt: 1, 
              mb: 2,
              whiteSpace: 'pre-wrap',
              fontFamily: "monospace" 
            }}>
              {comment.deleted ? <em>This comment has been deleted</em> : (comment.text || comment.content)}
            </Typography>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Button 
                  startIcon={<Reply />} 
                  size="small" 
                  onClick={toggleReplyForm}
                  disabled={comment.deleted}
                  sx={{
                    fontFamily: "'VT323', monospace",
                    color: "var(--text-color)",
                    '&:hover': {
                      color: "var(--accent-color)",
                    }
                  }}
                >
                  Reply
                </Button>
              </Box>
              
              {isCommentAuthor && !comment.deleted && (
                <IconButton 
                  size="small" 
                  color="error" 
                  onClick={handleDeleteComment}
                  aria-label="delete comment"
                >
                  <Delete fontSize="small" />
                </IconButton>
              )}
            </Box>
            
            <Box sx={{ mt: 2 }}>
              <Button
                onClick={toggleReplies}
                size="medium"
                variant="contained"
                fullWidth
                startIcon={showReplies ? <ExpandLess /> : <ExpandMore />}
                sx={{ 
                  fontFamily: "'VT323', monospace",
                  bgcolor: "var(--secondary-color)",
                  color: "var(--bg-color)",
                  py: 1,
                  '&:hover': {
                    backgroundColor: "var(--accent-color)",
                    color: "var(--bg-color)",
                  },
                }}
              >
                {showReplies ? 'Hide Replies' : 'Show Replies'} {hasReplies ? `(${localReplyCount})` : ''}
              </Button>
            </Box>
          </Box>
        </Box>
        
        <Collapse in={showReplyForm} sx={{ mt: 2 }}>
          <form onSubmit={handleReplySubmit}>
            <TextField
              multiline
              rows={3}
              fullWidth
              placeholder="Write your reply..."
              value={replyContent}
              onChange={(e) => setReplyContent(e.target.value)}
              sx={{ mb: 2 }}
            />
            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Button 
                variant="outlined" 
                sx={{ 
                  mr: 1,
                  fontFamily: "'VT323', monospace" 
                }} 
                onClick={() => setShowReplyForm(false)}
              >
                Cancel
              </Button>
              <Button 
                variant="contained" 
                type="submit"
                sx={{ 
                  fontFamily: "'VT323', monospace",
                  bgcolor: "var(--accent-color)",
                  color: "var(--bg-color)",
                  '&:hover': {
                    backgroundColor: "var(--accent-color-dark)",
                  }
                }}
              >
                Submit
              </Button>
            </Box>
          </form>
        </Collapse>
        
        <Collapse in={showReplies} sx={{ mt: 2 }}>
          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}>
              <CircularProgress size={24} sx={{ color: 'var(--accent-color)' }} />
            </Box>
          ) : (
            <>
              {replies.length > 0 ? (
                <Box 
                  sx={{ 
                    pl: { xs: 2, sm: 4 }, 
                    borderLeft: '2px dashed var(--border-color)',
                    ml: 1,
                    position: 'relative'
                  }}
                >
                  <Box 
                    sx={{ 
                      position: 'absolute',
                      left: '0px',
                      top: 0,
                      bottom: 0,
                      width: '2px',
                      bgcolor: 'rgba(var(--accent-color-rgb), 0.3)',
                      display: { xs: 'none', sm: 'block' }
                    }} 
                  />

                  {replies.map((reply) => (
                    <ReplyItem 
                      key={reply.id} 
                      reply={reply} 
                      topicId={comment.topicId}
                      onReplyAdded={() => {
                        setLocalReplyCount(prev => prev + 1);
                        loadReplies(repliesPage);
                      }}
                      onDelete={() => {
                        setLocalReplyCount(prev => Math.max(0, prev - 1));
                        loadReplies(repliesPage);
                      }} 
                    />
                  ))}
                  
                  {repliesPageCount > 1 && (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2, mb: 1 }}>
                      <Pagination 
                        count={repliesPageCount} 
                        page={repliesPage} 
                        onChange={handlePageChange}
                        size="small"
                        sx={{
                          '& .MuiPaginationItem-root': {
                            fontFamily: "'VT323', monospace",
                            color: 'var(--text-color)'
                          },
                          '& .Mui-selected': {
                            backgroundColor: 'var(--accent-color)',
                            color: 'var(--bg-color)'
                          }
                        }}
                      />
                    </Box>
                  )}
                </Box>
              ) : (
                <Typography variant="body2" sx={{ 
                  textAlign: 'center', 
                  my: 2, 
                  fontStyle: 'italic',
                  fontFamily: "'Courier Prime', monospace",
                  color: 'var(--text-secondary)'
                }}>
                  No replies to display
                </Typography>
              )}
            </>
          )}
        </Collapse>
      </CardContent>
    </Card>
  );
};

export default Comment; 