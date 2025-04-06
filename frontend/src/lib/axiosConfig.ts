import axios from 'axios';
import config from './config';

const axiosInstance = axios.create({
  baseURL: config.api.baseUrl,
});

axiosInstance.interceptors.request.use(
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

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('keycloak_token');

      window.location.href = '/';
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;