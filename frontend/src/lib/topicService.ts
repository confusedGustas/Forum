import axios from 'axios';

const API_URL = '/api/v1';

export interface TopicRequestDto {
    title: string;
    content: string;
}

export interface TopicResponseDto {
    id: string;
    title: string;
    content: string;
    createdAt: string | number[];
    author?: {
        id: string;
        name: string;
    };
    authorId?: string;
    authorName?: string;
    attachments?: Array<{
        id: string;
        filename: string;
        contentType: string;
        url: string;
    }>;
    files?: Array<{
        id: string;
        minioObjectName: string;
        contentType: string;
    }>;
    rating?: number;
    userRating?: number;
}

export interface PaginatedResponseDto {
    items?: TopicResponseDto[];
    content?: TopicResponseDto[];
    totalItems?: number;
    totalElements?: number;
    totalPages: number;
    currentPage?: number;
    number?: number;
}

export const createTopic = async (
    topic: TopicRequestDto, 
    files?: File[]
): Promise<TopicResponseDto> => {
    const formData = new FormData();
    formData.append('title', topic.title);
    formData.append('content', topic.content);
    
    if (files && files.length > 0) {
        files.forEach(file => {
            formData.append('files', file);
        });
    }
    
    const response = await axios.post(`/api/topics`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    
    return response.data;
};

export const getAllTopics = async (): Promise<TopicResponseDto[]> => {
    const response = await axios.get(`/api/topics`);
    return response.data;
};

export const getTopicById = async (id: string): Promise<TopicResponseDto> => {
    const response = await axios.get(`/api/topics/${id}`);
    return response.data;
};

export const updateTopicRating = async (
    id: string, 
    rating: number
): Promise<TopicResponseDto> => {
    const response = await axios.patch(`/api/topics/${id}/rating`, { rating });
    return response.data;
};