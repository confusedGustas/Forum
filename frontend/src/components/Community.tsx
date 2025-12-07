import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

interface CommunityProps {
  community: {
    id: string;
    title: string;
    description: string;
    createdAt: string;
    updatedAt: string;
    isEnabled: boolean;
  };
}

const Community: React.FC<CommunityProps> = ({ community }) => {
  const navigate = useNavigate();

  const handleViewTopics = () => {
      console.log(community);
      navigate(`/communities/${community.id}`);
  };

  return (
    <Box
      sx={{
        border: '1px solid var(--border-color)',
        borderRadius: '4px',
        padding: '16px',
        marginBottom: '16px',
        backgroundColor: 'rgba(0, 0, 0, 0.2)',
      }}
    >
      <Typography
        variant="h6"
        sx={{
          fontFamily: "'VT323', monospace",
          color: 'var(--accent-color)',
          marginBottom: '8px',
        }}
      >
        {community.title}
      </Typography>
      <Typography
        sx={{
          fontFamily: "'Courier Prime', monospace",
          color: 'var(--text-color)',
          marginBottom: '8px',
        }}
      >
        {community.description}
      </Typography>
      <Button
        variant="contained"
        onClick={handleViewTopics}
        sx={{
          fontFamily: "'VT323', monospace",
          fontSize: '1rem',
          backgroundColor: 'var(--accent-color)',
          color: 'var(--bg-color)',
          '&:hover': {
            backgroundColor: 'var(--secondary-color)',
          },
        }}
      >
        View Topics
      </Button>
    </Box>
  );
};

export default Community;
