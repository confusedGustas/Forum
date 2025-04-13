import React, { useState, useRef, useContext } from "react";
import { 
    Container, 
    Box, 
    Typography, 
    TextField, 
    Button, 
    Paper,
    List,
    ListItem,
    ListItemText,
    IconButton,
    Divider,
    Alert
} from "@mui/material";
import { 
    FileUpload as FileUploadIcon, 
    Clear as ClearIcon, 
    Send as SendIcon,
    ArrowBack as ArrowBackIcon
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import apiProxy from "../lib/apiProxy";
import { KeycloakContext } from "../context/KeycloakContext";

const asciiArt = `
  _   _                 ____            _   
 | \\ | | _____      __ |  _ \\ ___  ___| |_ 
 |  \\| |/ _ \\ \\ /\\ / / | |_) / _ \\/ __| __|
 | |\\  |  __/\\ V  V /  |  __/ (_) \\__ \\ |_ 
 |_| \\_|\\___| \\_/\\_/   |_|   \\___/|___/\\__|
                                           
 ========================================== 
`;

const NewPost = () => {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [files, setFiles] = useState<File[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const fileInputRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();
    const { authenticated, login } = useContext(KeycloakContext);

    const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTitle(e.target.value);
    };

    const handleContentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setContent(e.target.value);
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            const newFiles = Array.from(e.target.files);
            setFiles(prevFiles => [...prevFiles, ...newFiles]);
        }
    };

    const handleRemoveFile = (indexToRemove: number) => {
        setFiles(prevFiles => prevFiles.filter((_, index) => index !== indexToRemove));
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        
        setIsLoading(true);
        setError(null);
        
        if (!title.trim() || !content.trim()) {
            setError("Title and content are required.");
            setIsLoading(false);
            return;
        }
        
        try {
            const formData = new FormData();
            formData.append('title', title.trim());
            formData.append('content', content.trim());
            
            for (let i = 0; i < files.length; i++) {
                formData.append('files', files[i]);
            }
            
            await apiProxy.topics.create(formData);
            
            setTitle('');
            setContent('');
            setFiles([]);
            
            setIsLoading(false);
            setTimeout(() => {
                navigate('/home');
            }, 2000);
        } catch (err: any) {
            let errorMessage = "Failed to create post. Please try again.";
            
            if (err.response) {
                if (err.response.status === 401) {
                    errorMessage = "You must be logged in to create a post.";
                } else if (err.response.data.error.includes("Image file contains restricted content")) {
                    errorMessage = "One or more uploaded images were rejected due to restricted content.";
                }
                else {
                    errorMessage = err.response.data?.message || err.message;
                    console.log(err.response);
                }
            }
            
            setError(errorMessage);
            console.log(errorMessage);
            setIsLoading(false);
        }
    };

    if (!authenticated) {
        return (
            <Container className="retro-container crt-effect" sx={{ mt: 4, mb: 4 }}>
                <Box className="ascii-art" sx={{ color: "var(--accent-color)", mb: 3, textAlign: "center" }}>
                    <pre>{asciiArt}</pre>
                </Box>
                
                <Alert 
                    severity="error" 
                    sx={{ 
                        backgroundColor: 'rgba(255, 0, 0, 0.1)', 
                        color: 'var(--danger-color)',
                        border: '2px solid var(--danger-color)',
                        fontFamily: "'VT323', monospace",
                        fontSize: "1.2rem",
                        mb: 3
                    }}
                >
                    You must be logged in to create a post.
                </Alert>
                
                <Box sx={{ display: "flex", justifyContent: "center", gap: 2 }}>
                    <Button
                        onClick={() => navigate("/home")}
                        sx={{ 
                            fontFamily: "'VT323', monospace",
                            fontSize: "1.2rem",
                            bgcolor: "transparent",
                            color: "var(--secondary-color)",
                            border: "2px solid var(--secondary-color)",
                            borderRadius: 0,
                            padding: "8px 16px",
                            "&:hover": {
                                bgcolor: "var(--secondary-color)",
                                color: "var(--bg-color)"
                            }
                        }}
                    >
                        [BACK TO HOME]
                    </Button>
                    
                    <Button
                        onClick={login}
                        sx={{ 
                            fontFamily: "'VT323', monospace",
                            fontSize: "1.2rem",
                            bgcolor: "transparent",
                            color: "var(--success-color)",
                            border: "2px solid var(--success-color)",
                            borderRadius: 0,
                            padding: "8px 16px",
                            "&:hover": {
                                bgcolor: "var(--success-color)",
                                color: "var(--bg-color)"
                            }
                        }}
                    >
                        [LOGIN]
                    </Button>
                </Box>
            </Container>
        );
    }
    
    return (
        <Container className="retro-container crt-effect" sx={{ mt: 4, mb: 4 }}>
            <Button
                startIcon={<ArrowBackIcon />}
                onClick={() => navigate("/home")}
                sx={{ 
                    mb: 2, 
                    fontFamily: "'VT323', monospace",
                    fontSize: "1.2rem",
                    bgcolor: "transparent",
                    color: "var(--secondary-color)",
                    border: "2px solid var(--secondary-color)",
                    borderRadius: 0,
                    padding: "8px 16px",
                    "&:hover": {
                        bgcolor: "var(--secondary-color)",
                        color: "var(--bg-color)"
                    }
                }}
            >
                [BACK]
            </Button>

            <Box className="ascii-art" sx={{ color: "var(--accent-color)", mb: 3, textAlign: "center" }}>
                <pre>{asciiArt}</pre>
            </Box>

            <Typography variant="h4" sx={{ 
                fontFamily: "'Press Start 2P', cursive", 
                color: "var(--text-color)", 
                textAlign: "center",
                mb: 4,
                fontSize: "1.5rem",
                textShadow: "3px 3px 0 #111"
            }}>
                CREATE NEW TOPIC
            </Typography>

            {error && (
                <Box sx={{ 
                    bgcolor: "rgba(255, 0, 0, 0.1)", 
                    p: 2, 
                    mb: 3, 
                    border: "2px solid var(--danger-color)" 
                }}>
                    <Typography sx={{ 
                        fontFamily: "'VT323', monospace", 
                        color: "var(--danger-color)",
                        fontSize: "1.2rem"
                    }}>
                        {error}
                    </Typography>
                </Box>
            )}

            <Paper className="retro-card" sx={{ p: 3 }}>
                <form onSubmit={handleSubmit}>
                    <Typography sx={{ 
                        fontFamily: "'VT323', monospace", 
                        color: "var(--text-color)",
                        fontSize: "1.2rem",
                        mb: 1
                    }}>
                        TITLE:
                    </Typography>
                    <TextField
                        fullWidth
                        value={title}
                        onChange={handleTitleChange}
                        margin="normal"
                        required
                        placeholder="Enter post title..."
                        InputProps={{
                            sx: { 
                                fontFamily: "'Courier Prime', monospace",
                                color: "var(--text-color)",
                                bgcolor: "rgba(0, 0, 0, 0.3)",
                                '& .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'var(--border-color) !important',
                                },
                                '&:hover .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'var(--accent-color) !important',
                                },
                            }
                        }}
                        sx={{ mb: 3 }}
                    />

                    <Typography sx={{ 
                        fontFamily: "'VT323', monospace", 
                        color: "var(--text-color)",
                        fontSize: "1.2rem",
                        mb: 1
                    }}>
                        CONTENT:
                    </Typography>
                    <TextField
                        fullWidth
                        multiline
                        rows={8}
                        value={content}
                        onChange={handleContentChange}
                        margin="normal"
                        required
                        placeholder="Enter post content..."
                        InputProps={{
                            sx: { 
                                fontFamily: "'Courier Prime', monospace",
                                color: "var(--text-color)",
                                bgcolor: "rgba(0, 0, 0, 0.3)",
                                '& .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'var(--border-color) !important',
                                },
                                '&:hover .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'var(--accent-color) !important',
                                },
                            }
                        }}
                        sx={{ mb: 3 }}
                    />

                    <Box sx={{ mb: 3 }}>
                        <Typography sx={{ 
                            fontFamily: "'VT323', monospace", 
                            color: "var(--text-color)",
                            fontSize: "1.2rem",
                            mb: 1
                        }}>
                            ATTACHMENTS:
                        </Typography>
                        
                        <input
                            type="file"
                            multiple
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            style={{ display: 'none' }}
                        />
                        
                        <Button
                            variant="outlined"
                            startIcon={<FileUploadIcon />}
                            onClick={() => fileInputRef.current?.click()}
                            sx={{ 
                                fontFamily: "'VT323', monospace",
                                fontSize: "1.2rem",
                                bgcolor: "transparent",
                                color: "var(--secondary-color)",
                                border: "2px solid var(--secondary-color)",
                                borderRadius: 0,
                                padding: "8px 16px",
                                mb: 2,
                                "&:hover": {
                                    bgcolor: "var(--secondary-color)",
                                    color: "var(--bg-color)"
                                }
                            }}
                        >
                            [SELECT FILES]
                        </Button>

                        {files.length > 0 && (
                            <Paper sx={{ 
                                bgcolor: "rgba(0, 0, 0, 0.3)", 
                                border: "1px dashed var(--border-color)", 
                                mt: 2 
                            }}>
                                <List>
                                    {files.map((file, index) => (
                                        <React.Fragment key={index}>
                                            <ListItem
                                                secondaryAction={
                                                    <IconButton 
                                                        edge="end" 
                                                        onClick={() => handleRemoveFile(index)}
                                                        sx={{ color: "var(--danger-color)" }}
                                                    >
                                                        <ClearIcon />
                                                    </IconButton>
                                                }
                                            >
                                                <ListItemText 
                                                    primary={file.name} 
                                                    secondary={`${(file.size / 1024).toFixed(2)} KB`}
                                                    primaryTypographyProps={{
                                                        style: { 
                                                            fontFamily: "'VT323', monospace",
                                                            color: "var(--accent-color)",
                                                            fontSize: "1.1rem"
                                                        }
                                                    }}
                                                    secondaryTypographyProps={{
                                                        style: { 
                                                            fontFamily: "'Courier Prime', monospace",
                                                            color: "var(--text-color)",
                                                            fontSize: "0.8rem"
                                                        }
                                                    }}
                                                />
                                            </ListItem>
                                            {index < files.length - 1 && <Divider sx={{ borderColor: "var(--border-color)" }} />}
                                        </React.Fragment>
                                    ))}
                                </List>
                            </Paper>
                        )}
                    </Box>

                    <Box sx={{ display: "flex", justifyContent: "center" }}>
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={isLoading}
                            endIcon={<SendIcon />}
                            sx={{ 
                                fontFamily: "'Press Start 2P', cursive",
                                fontSize: "1rem",
                                bgcolor: "transparent",
                                color: "var(--success-color)",
                                border: "2px solid var(--success-color)",
                                borderRadius: 0,
                                padding: "16px 24px",
                                "&:hover": {
                                    bgcolor: "var(--success-color)",
                                    color: "var(--bg-color)"
                                },
                                "&:disabled": {
                                    opacity: 0.5,
                                    color: "var(--text-color)"
                                }
                            }}
                        >
                            {isLoading ? "SENDING..." : "POST"}
                        </Button>
                    </Box>
                </form>
            </Paper>
            
            <Typography sx={{ 
                fontFamily: "'VT323', monospace", 
                color: "var(--secondary-color)", 
                textAlign: "center",
                fontSize: "1rem",
                mt: 3
            }}>
                &gt; All fields marked with * are required <span className="blink">█</span>
            </Typography>
        </Container>
    );
};

export default NewPost; 