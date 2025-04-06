import React, { useContext, useState } from 'react';
import { 
  Box, 
  Typography, 
  Avatar, 
  IconButton, 
  Card, 
  CardContent
} from '@mui/material';
import { Delete } from '@mui/icons-material';
import { ReplyResponseDto } from '../lib/commentService';
import { KeycloakContext } from '../context/KeycloakContext';
import apiProxy from '../lib/apiProxy';

interface ReplyItemProps {
  reply: ReplyResponseDto;
  onDelete: () => void;
}

const ReplyItem: React.FC<ReplyItemProps> = ({ reply, onDelete }) => {
  const { userDetails } = useContext(KeycloakContext);
  const [error, setError] = useState('');
  
  const isReplyAuthor = userDetails?.id === reply.authorId;
  
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };
  
  const handleDeleteReply = async () => {
    try {
      await apiProxy.comments.delete(reply.id);
      onDelete();
    } catch (err: any) {
      setError(`Failed to delete reply: ${err.message}`);
    }
  };
  
  return (
    <Card sx={{ 
      mt: 1, 
      mb: 1, 
      bgcolor: 'var(--bg-secondary)', 
      border: '1px solid var(--border-color)',
      '&:hover': {
        borderColor: 'var(--accent-color)'
      }
    }}>
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
            
            {isReplyAuthor && !reply.deleted && (
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 1 }}>
                <IconButton 
                  size="small" 
                  color="error" 
                  onClick={handleDeleteReply}
                  aria-label="delete reply"
                >
                  <Delete fontSize="small" />
                </IconButton>
              </Box>
            )}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ReplyItem; 