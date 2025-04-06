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

const hasModerationPermission = (userDetails: any): boolean => {
  if (!userDetails) return false;
  
  const roles = userDetails.realm_access?.roles || [];
  return roles.includes('moderator') || roles.includes('admin');
};

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
  
  const [childReplies, setChildReplies] = useState<ReplyResponseDto[]>([]);
  const [showChildReplies, setShowChildReplies] = useState(false);
  const [loadingChildReplies, setLoadingChildReplies] = useState(false);
  const [hasChildReplies, setHasChildReplies] = useState(false);
  const [childReplyPage, setChildReplyPage] = useState(1);
  const [childReplyPageCount, setChildReplyPageCount] = useState(1);
  const [childReplyCount, setChildReplyCount] = useState(0);
  
  const MAX_REPLIES_PER_PAGE = 3;
  const MAX_NESTING_LEVEL = 5;
  
  const isReplyAuthor = userDetails?.id === reply.authorId;
  
  useEffect(() => {
    console.log("Reply createdAt format:", reply.id, reply.createdAt, typeof reply.createdAt);
  }, [reply]);
  
  useEffect(() => {
    if (userDetails) {
      console.log("Reply author check:", {
        userId: userDetails.id,
        authorId: reply.authorId,
        isAuthor: userDetails.id === reply.authorId,
        deleted: reply.deleted
      });
    }
  }, [userDetails, reply]);
  
  const [isAuthor, setIsAuthor] = useState(false);
  const [isModerator, setIsModerator] = useState(false);
  
  useEffect(() => {
    if (userDetails) {
      const isAuthorMatch = userDetails.id === reply.authorId;
      console.log(`UserID: ${userDetails.id}, AuthorID: ${reply.authorId}, Match: ${isAuthorMatch}`);
      
      const hasModerationRights = hasModerationPermission(userDetails);
      
      setIsAuthor(isAuthorMatch);
      setIsModerator(hasModerationRights);
      
      console.log("Permission check:", {
        isAuthorMatch,
        hasModerationRights,
        userDetailsId: userDetails?.id,
        authorId: reply.authorId
      });
    } else {
      setIsAuthor(false);
      setIsModerator(false);
    }
  }, [userDetails, reply.authorId]);
  
  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return 'No date';
    
    try {
      if (Array.isArray(dateString)) {
        const [year, month, day, hour, minute] = dateString;
        return new Date(year, month - 1, day, hour, minute).toLocaleString();
      }
      
      if (typeof dateString === 'string') {
        return new Date(dateString).toLocaleString();
      }
      
      return 'Unknown date format';
    } catch (err) {
      console.error("Error formatting date:", err, dateString);
      return 'Invalid date';
    }
  };
  
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
  const [deletingReply, setDeletingReply] = useState(false);
  
  const handleDeleteClick = () => {
    setOpenDeleteDialog(true);
  };

  const handleCloseDeleteDialog = () => {
    if (deletingReply) return;
    setOpenDeleteDialog(false);
  };
  
  const handleConfirmDelete = async () => {
    setDeletingReply(true);
    try {
      console.log("Deleting reply:", reply.id);
      const response = await apiProxy.comments.delete(reply.id);
      console.log("Delete response:", response);
      setOpenDeleteDialog(false);
      onDelete();
    } catch (err: any) {
      console.error("Error deleting reply:", err);
      setError(`Failed to delete reply: ${err.message || 'Unknown error'}`);
    } finally {
      setDeletingReply(false);
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
        parentCommentId: reply.id
      });
      
      setReplyContent('');
      setShowReplyForm(false);
      
      setChildReplyCount(prev => prev + 1);
      setHasChildReplies(true);
      
      if (showChildReplies) {
        loadChildReplies(1);
      } else {
        setShowChildReplies(true);
        loadChildReplies(1);
      }
      
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

  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => setError(''), 5000);
      return () => clearTimeout(timer);
    }
  }, [error]);
  
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
            <Box sx={{ 
              position: 'absolute',
              top: -40,
              left: 0,
              right: 0,
              zIndex: 10,
              p: 1,
              bgcolor: 'rgba(255, 0, 0, 0.9)',
              color: 'white',
              borderRadius: '4px',
              fontFamily: "'Courier Prime', monospace",
              fontSize: '0.85rem',
              fontWeight: 'bold',
              textAlign: 'center',
              boxShadow: '0 2px 10px rgba(0,0,0,0.2)',
              animation: 'fadeIn 0.3s ease'
            }}>
              {error}
            </Box>
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
              {reply.authorName ? reply.authorName.charAt(0).toUpperCase() : 
               reply.userName ? reply.userName.charAt(0).toUpperCase() : 'A'}
            </Avatar>
            
            <Box sx={{ flexGrow: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="subtitle2" sx={{ 
                  fontWeight: 'bold',
                  fontFamily: "'VT323', monospace" 
                }}>
                  {reply.authorName || reply.userName || 'Anonymous'}
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
                
                {!reply.deleted && userDetails && (
                  <Tooltip title="Delete reply" placement="top" arrow>
                    <IconButton 
                      size="small" 
                      color="error" 
                      onClick={handleDeleteClick}
                      aria-label="delete reply"
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
                              loadChildReplies(childReplyPage);
                            }}
                            onDelete={() => {
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
          Confirm Delete Reply
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description" sx={{ color: 'var(--text-color, #000)' }}>
            <strong>Are you sure you want to delete this reply?</strong><br/>
            <Box sx={{ 
              mt: 1, 
              p: 1, 
              backgroundColor: 'rgba(0,0,0,0.05)',
              borderLeft: '3px solid var(--accent-color, #1976d2)',
              fontStyle: 'italic'
            }}>
              "{reply.text?.length > 100 ? reply.text.substring(0, 100) + '...' : reply.text}"
            </Box>
            <Box sx={{ mt: 1 }}>
              This action cannot be undone.
            </Box>
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button 
            onClick={handleCloseDeleteDialog} 
            disabled={deletingReply}
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
            disabled={deletingReply}
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
            {deletingReply ? (
              <CircularProgress size={20} sx={{ color: 'white' }} />
            ) : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default ReplyItem; 