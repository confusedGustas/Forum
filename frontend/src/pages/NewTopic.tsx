import React, {useState} from 'react';
import { TextField, Button, Box, Typography } from '@mui/material';
import axios from "axios";
import {useNavigate} from "react-router-dom";

const NewTopic = () => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [files, setFiles] = useState([]);
    const navigate = useNavigate();

    const handleTitleChange = (e) => {
        setTitle(e.target.value);
    };

    const handleContentChange = (e) => {
        setContent(e.target.value);
    };

    const handleCreate = async () => {

        if (title && content) {
            const formData = new FormData();
            formData.append('topicRequestDto', JSON.stringify({title, content}));

            for (let i = 0; i < files.length; i++){
                formData.append('files', files[i]);
            }
            try{
                const response = await axios.post('http://localhost:8080/api/v1/topics', formData, {
                    withCredentials: true
                });
                console.log('Topic created successfully:', response.data);
                alert('New topic created successfully!');

                setTitle('');
                setContent('');
                setErrorMessage('');
                setFiles([]);
            } catch (error){
                console.error('Error creating topic', error);
                setErrorMessage('An error occurred while creating the topic');
            }

        } else {
            setErrorMessage('Both title and content are required.');
        }
    };

    const handleCancel = () => {
        setTitle('');
        setContent('');
        setErrorMessage('');
        navigate(-1);
    };

    const handleFileChange = (e) => {
        setFiles(e.target.files);
    };

    return (
        <Box sx={{ maxWidth: 600, margin: '0 auto', padding: 3 }}>
            <Typography variant="h4" gutterBottom>
                Create New Topic
            </Typography>

            {errorMessage && (
                <Typography color="error" variant="body2" gutterBottom>
                    {errorMessage}
                </Typography>
            )}

            <TextField
                label="Title"
                id="title"
                value={title}
                onChange={handleTitleChange}
                variant="outlined"
                fullWidth
                margin="normal"
                required
                error={Boolean(errorMessage) && !title}
                helperText={Boolean(errorMessage) && !title ? "Title is required" : ""}
            />

            <TextField
                label="Content"
                id="content"
                value={content}
                onChange={handleContentChange}
                variant="outlined"
                fullWidth
                margin="normal"
                required
                multiline
                rows={4}
                error={Boolean(errorMessage) && !content}
                helperText={Boolean(errorMessage) && !content ? "Content is required" : ""}
            />

            <Box sx={{ marginTop: 2 }}>
                <Typography variant="body1" gutterBottom>
                    Attach files:
                </Typography>
                <input
                    type="file"
                    multiple
                    onChange={handleFileChange}
                />
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', marginTop: 2 }}>
                <Button variant="outlined" onClick={handleCancel} color="secondary">
                    Cancel
                </Button>
                <Button variant="contained" onClick={handleCreate} color="primary">
                    Create
                </Button>
            </Box>
        </Box>
    );
};

export default NewTopic;
