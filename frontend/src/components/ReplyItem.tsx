import React, { useContext, useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  Avatar, 
  IconButton, 
  Card, 
  CardContent,
  Button,
  TextField,
  Collapse,
  CircularProgress,
  Pagination,
  Tooltip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';
import { Delete, Reply, ExpandMore, ExpandLess } from '@mui/icons-material';
import { ReplyResponseDto } from '../lib/commentService';
import { KeycloakContext } from '../context/KeycloakContext';
import apiProxy from '../lib/apiProxy';

interface ReplyItemProps {
  reply: ReplyResponseDto;
  onDelete: () => void;
  topicId: string;
  onReplyAdded: () => void;
  level?: number;
}

const ReplyItem: React.FC<ReplyItemProps> = ({ 
  reply, 
  onDelete, 
  topicId, 
  onReplyAdded,
  level = 1
}) => {
  const { userDetails, authenticated, login } = useContext(KeycloakContext);
  const [error, setError] = useState('');
  const [showReplyForm, setShowReplyForm] = useState(false);
  const [replyContent, setReplyContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  
  // New state for handling nested replies
  const [childReplies, setChildReplies] = useState<ReplyResponseDto[]>([]);
  const [showChildReplies, setShowChildReplies] = useState(false);
  const [loadingChildReplies, setLoadingChildReplies] = useState(false);
  const [hasChildReplies, setHasChildReplies] = useState(false);
  const [childReplyPage, setChildReplyPage] = useState(1);
  const [childReplyPageCount, setChildReplyPageCount] = useState(1);
  const [childReplyCount, setChildReplyCount] = useState(0);
  
  const MAX_REPLIES_PER_PAGE = 3;
  const MAX_NESTING_LEVEL = 5; // Prevent too deep nesting
  
  const isReplyAuthor = userDetails?.id === reply.authorId;
  
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };
  
  // Check if this reply has child replies
  useEffect(() => {
    const checkForChildReplies = async () => {
      try {
        const response = await apiProxy.comments.getReplies(reply.id, 0, 1);
        const hasReplies = response.data.totalElements > 0;
        setHasChildReplies(hasReplies);
        setChildReplyCount(response.data.totalElements);
      } catch (err) {
        console.error("Error checking for child replies:", err);
      }
    };
    
    checkForChildReplies();
  }, [reply.id]);
  
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  
  const handleDeleteClick = () => {
    setOpenDeleteDialog(true);
  };

  const handleCloseDeleteDialog = () => {
    setOpenDeleteDialog(false);
  };
  
  const handleConfirmDelete = async () => {
    try {
      await apiProxy.comments.delete(reply.id);
      setOpenDeleteDialog(false);
      onDelete();
    } catch (err: any) {
      setError(`Failed to delete reply: ${err.message}`);
      setOpenDeleteDialog(false);
    }
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
    
    setSubmitting(true);
    setError('');

    try {
      await apiProxy.comments.create({
        text: replyContent,
        topicId: topicId,
        parentCommentId: reply.id // Use the reply's ID as the parent
      });
      
      setReplyContent('');
      setShowReplyForm(false);
      
      // Update child reply count
      setChildReplyCount(prev => prev + 1);
      setHasChildReplies(true);
      
      // If showing child replies, refresh them
      if (showChildReplies) {
        loadChildReplies(1);
      } else {
        // Show child replies automatically after posting
        setShowChildReplies(true);
        loadChildReplies(1);
      }
      
      // Notify parent component about new reply
      onReplyAdded();
      
    } catch (err: any) {
      setError(`Failed to post reply: ${err.message}`);
    } finally {
      setSubmitting(false);
    }
  };
  
  const toggleChildReplies = () => {
    const newShowReplies = !showChildReplies;
    setShowChildReplies(newShowReplies);
    
    if (newShowReplies && childReplies.length === 0) {
      loadChildReplies(1);
    }
  };
  
  const loadChildReplies = async (page: number) => {
    if (loadingChildReplies) return;
    
    setLoadingChildReplies(true);
    setError('');
    
    try {
      const response = await apiProxy.comments.getReplies(reply.id, page - 1, MAX_REPLIES_PER_PAGE);
      
      if (response.data && response.data.content) {
        setChildReplies(response.data.content);
        setChildReplyPageCount(response.data.totalPages || 1);
        setChildReplyPage(page);
        setChildReplyCount(response.data.totalElements || 0);
      } else {
        setChildReplies([]);
        setChildReplyPageCount(1);
      }
    } catch (err: any) {
      setError(`Failed to load replies: ${err.message}`);
      console.error("Error loading child replies:", err);
    } finally {
      setLoadingChildReplies(false);
    }
  };
  
  const handleChildReplyPageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    loadChildReplies(page);
  };
  
  return (
    <>
      <Card sx={{ 
        mt: 1, 
        mb: 1, 
        bgcolor: 'var(--bg-secondary)', 
        border: '1px solid var(--border-color)',
        '&:hover': {
          borderColor: 'var(--accent-color)'
        },
        position: 'relative',
        overflow: 'visible'
      }}>
        {/* Connection line to indicate reply */}
        <Box
          sx={{
            position: 'absolute',
            left: -10,
            top: '50%',
            width: 10,
            height: 1,
            bgcolor: 'rgba(var(--accent-color-rgb), 0.4)',
            display: { xs: 'none', sm: 'block' }
          }}
        />
        
        <CardContent sx={{ py: 1, '&:last-child': { pb: 1 } }}>
          {error && (
            <Typography color="error" sx={{ 
              mb: 1,
              fontFamily: "'Courier Prime', monospace" 
            }}>
              {error}
            </Typography>
          )}
          
          <Box sx={{ display: 'flex', alignItems: 'flex-start' }}>
            <Avatar 
              sx={{ 
                width: 24, 
                height: 24, 
                mr: 1, 
                bgcolor: 'var(--accent-color)', 
                fontSize: '0.8rem' 
              }}
            >
              {reply.authorName ? reply.authorName.charAt(0).toUpperCase() : 'U'}
            </Avatar>
            
            <Box sx={{ flexGrow: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="subtitle2" sx={{ 
                  fontWeight: 'bold',
                  fontFamily: "'VT323', monospace" 
                }}>
                  {reply.authorName || 'Unknown User'}
                </Typography>
                <Typography variant="caption" color="textSecondary" sx={{ 
                  fontFamily: "'Courier Prime', monospace" 
                }}>
                  {formatDate(reply.createdAt)}
                </Typography>
              </Box>
              
              <Typography variant="body2" sx={{ 
                mt: 0.5, 
                whiteSpace: 'pre-wrap',
                fontFamily: "monospace" 
              }}>
                {reply.deleted ? <em>This reply has been deleted</em> : (reply.text || reply.content)}
              </Typography>
              
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
                <Box sx={{ display: 'flex', gap: 1 }}>
                  {!reply.deleted && level < MAX_NESTING_LEVEL && (
                    <Button 
                      startIcon={<Reply sx={{ fontSize: 'small' }} />} 
                      size="small" 
                      onClick={toggleReplyForm}
                      sx={{
                        fontFamily: "'VT323', monospace",
                        fontSize: '0.7rem',
                        color: "var(--text-color)",
                        '&:hover': {
                          color: "var(--accent-color)",
                        }
                      }}
                    >
                      Reply
                    </Button>
                  )}
                  
                  {hasChildReplies && (
                    <Button
                      startIcon={showChildReplies ? <ExpandLess sx={{ fontSize: 'small' }} /> : <ExpandMore sx={{ fontSize: 'small' }} />}
                      size="small"
                      onClick={toggleChildReplies}
                      sx={{
                        fontFamily: "'VT323', monospace",
                        fontSize: '0.7rem',
                        color: "var(--text-color)",
                        '&:hover': {
                          color: "var(--accent-color)",
                        }
                      }}
                    >
                      {showChildReplies ? 'Hide Replies' : 'Show Replies'} ({childReplyCount})
                    </Button>
                  )}
                </Box>
                
                {isReplyAuthor && !reply.deleted && (
                  <Tooltip title="Delete reply" placement="top" arrow>
                    <IconButton 
                      size="small" 
                      color="error" 
                      onClick={handleDeleteClick}
                      aria-label="delete reply"
                      sx={{
                        color: 'var(--danger-color)',
                        p: 0.5,
                        border: '1px solid transparent',
                        '&:hover': {
                          backgroundColor: 'rgba(255, 0, 0, 0.1)',
                          border: '1px solid var(--danger-color)',
                        }
                      }}
                    >
                      <Delete fontSize="small" />
                    </IconButton>
                  </Tooltip>
                )}
              </Box>

              <Collapse in={showReplyForm} sx={{ mt: 1 }}>
                <form onSubmit={handleReplySubmit}>
                  <TextField
                    multiline
                    rows={2}
                    fullWidth
                    placeholder="Write your reply..."
                    value={replyContent}
                    onChange={(e) => setReplyContent(e.target.value)}
                    size="small"
                    sx={{ 
                      mb: 1,
                      '& .MuiOutlinedInput-root': {
                        fontSize: '0.85rem'
                      }
                    }}
                  />
                  <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                    <Button 
                      variant="outlined" 
                      size="small"
                      sx={{ 
                        mr: 1,
                        fontFamily: "'VT323', monospace",
                        fontSize: '0.7rem'
                      }} 
                      onClick={() => setShowReplyForm(false)}
                      disabled={submitting}
                    >
                      Cancel
                    </Button>
                    <Button 
                      variant="contained" 
                      type="submit"
                      size="small"
                      disabled={submitting || !replyContent.trim()}
                      sx={{ 
                        fontFamily: "'VT323', monospace",
                        fontSize: '0.7rem',
                        bgcolor: "var(--accent-color)",
                        color: "var(--bg-color)",
                        '&:hover': {
                          backgroundColor: "var(--accent-color-dark)",
                        }
                      }}
                    >
                      {submitting ? (
                        <CircularProgress size={16} sx={{ color: 'white' }} />
                      ) : 'Submit'}
                    </Button>
                  </Box>
                </form>
              </Collapse>
              
              {/* Nested Replies Section */}
              <Collapse in={showChildReplies} sx={{ mt: 1 }}>
                {loadingChildReplies ? (
                  <Box sx={{ display: 'flex', justifyContent: 'center', my: 1 }}>
                    <CircularProgress size={16} sx={{ color: 'var(--accent-color)' }} />
                  </Box>
                ) : (
                  <>
                    {childReplies.length > 0 ? (
                      <Box 
                        sx={{ 
                          pl: { xs: 1, sm: 2 }, 
                          ml: 0.5,
                          borderLeft: `1px dashed rgba(var(--accent-color-rgb), 0.4)`,
                          position: 'relative'
                        }}
                      >
                        {childReplies.map((childReply) => (
                          <ReplyItem 
                            key={childReply.id} 
                            reply={childReply} 
                            topicId={topicId}
                            level={level + 1}
                            onReplyAdded={() => {
                              // When a reply is added to a child reply, refresh
                              loadChildReplies(childReplyPage);
                            }}
                            onDelete={() => {
                              // When a child reply is deleted, refresh
                              loadChildReplies(childReplyPage);
                            }} 
                          />
                        ))}
                        
                        {childReplyPageCount > 1 && (
                          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 1 }}>
                            <Pagination 
                              count={childReplyPageCount} 
                              page={childReplyPage} 
                              onChange={handleChildReplyPageChange}
                              size="small"
                              sx={{
                                '& .MuiPaginationItem-root': {
                                  fontFamily: "'VT323', monospace",
                                  color: 'var(--text-color)',
                                  fontSize: '0.7rem'
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
                      <Typography variant="caption" sx={{ 
                        display: 'block',
                        textAlign: 'center', 
                        my: 1, 
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
            </Box>
          </Box>
        </CardContent>
      </Card>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={openDeleteDialog}
        onClose={handleCloseDeleteDialog}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
        PaperProps={{
          sx: {
            backgroundColor: 'var(--card-bg)',
            color: 'var(--text-color)',
            border: '1px solid var(--border-color)'
          }
        }}
      >
        <DialogTitle id="alert-dialog-title" sx={{ fontFamily: "'VT323', monospace" }}>
          Confirm Deletion
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description" sx={{ color: 'var(--text-color)' }}>
            Are you sure you want to delete this reply? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button 
            onClick={handleCloseDeleteDialog} 
            sx={{ 
              color: 'var(--text-color)',
              fontFamily: "'VT323', monospace"
            }}
          >
            Cancel
          </Button>
          <Button 
            onClick={handleConfirmDelete} 
            autoFocus
            sx={{ 
              color: 'var(--danger-color)',
              fontFamily: "'VT323', monospace"
            }}
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default ReplyItem; 