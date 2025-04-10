import axios from 'axios';
import config from './config';
import { TopicResponseDto, PaginatedResponseDto } from './topicService';

const apiClient = axios.create({
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('keycloak_token');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

const apiProxy = {
  topics: {
    getAll: () => apiClient.get<TopicResponseDto[]>(config.api.endpoints.topics),
    getById: (id: string) => apiClient.get<TopicResponseDto>(config.api.endpoints.topic(id)),
    create: (formData: FormData) => {
      const token = localStorage.getItem('keycloak_token');
      
      const headers: Record<string, string> = {
        'Accept': 'application/json, text/plain, */*',
      };
      
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      return axios.post<TopicResponseDto>(config.api.endpoints.topics, formData, {
        headers
      });
    },
    delete: (id: string) => apiClient.delete(config.api.endpoints.topic(id))
  },

  search: {
    topics: (params: {
      limit?: number;
      offset?: number;
      search?: string;
      sortBy?: string;
      sortOrder?: 'ASC' | 'DESC';
    }) => {
      const cleanParams: Record<string, any> = {};
      
      if (params.limit !== undefined) cleanParams.limit = params.limit;
      if (params.offset !== undefined) cleanParams.offset = params.offset;
      if (params.search) cleanParams.search = params.search;
      
      if (params.sortBy && ['title', 'rating'].includes(params.sortBy)) {
        cleanParams.sortBy = params.sortBy;
      }
      
      if (params.sortOrder && ['ASC', 'DESC'].includes(params.sortOrder)) {
        cleanParams.sortOrder = params.sortOrder;
      }
      
      return apiClient.get<PaginatedResponseDto>(config.api.endpoints.searchTopics, { params: cleanParams });
    }
  },

  comments: {
    getForTopic: (topicId: string, page?: number, pageSize?: number) => {
      const params: Record<string, any> = {};
      if (page !== undefined) params.page = page;
      if (pageSize !== undefined) params.pageSize = pageSize;
      
      return apiClient.get(config.api.endpoints.topicComments(topicId), { params });
    },
    create: (commentRequestDto: { text: string, topicId: string, parentCommentId: string | null }) => {
      return apiClient.post(config.api.endpoints.comments, commentRequestDto);
    },
    getById: (commentId: string) => 
      apiClient.get(config.api.endpoints.comment(commentId)),
    delete: (commentId: string) => 
      apiClient.delete(config.api.endpoints.comment(commentId)),
    getReplies: (commentId: string, page?: number, pageSize?: number) => {
      const params: Record<string, any> = {};
      if (page !== undefined) params.page = page;
      if (pageSize !== undefined) params.pageSize = pageSize;
      
      console.log(`Fetching replies for comment ${commentId}, page: ${page}, size: ${pageSize}`);
      return apiClient.get(config.api.endpoints.commentReplies(commentId), { params });
    }
  },

  users: {
    getCurrent: () => apiClient.get(`${config.api.baseUrl}/users/me`),
    getTopics: (params: {
      page?: number;
      pageSize?: number;
    }) => {
      const cleanParams: Record<string, any> = {};
      
      if (params.page !== undefined) cleanParams.page = params.page;
      if (params.pageSize !== undefined) cleanParams.pageSize = params.pageSize;
      
      return apiClient.get<PaginatedResponseDto>(config.api.endpoints.userTopics, { params: cleanParams });
    }
  },

  ratings: {
    rateTopic: (topicId: string, rating: number) => {
      const params = new URLSearchParams();
      params.append('topicId', topicId);
      params.append('rating', rating.toString());
      
      return apiClient.post(config.api.endpoints.rating, null, { 
        params: params
      });
    }
  }
};

export default apiProxy;