const config = {
    api: {
        baseUrl: '',
        
        endpoints: {
            topics: '/topics',
            topic: (id: string) => `/topics/${id}`,
            rating: '/ratings',
            searchTopics: '/search/topics',
            userTopics: '/users/me/topics',
            comments: '/comments',
            topicComments: (topicId: string) => `/comments/topics/${topicId}`,
            comment: (commentId: string) => `/comments/${commentId}`,
            commentReplies: (commentId: string) => `/comments/${commentId}/replies`
        }
    },
    
    auth: {
        baseUrl: 'http://localhost:8181',
    },
    
    keycloak: {
        url: 'http://localhost:8181',
        directUrl: 'http://localhost:8181',
        realm: 'forum',
        clientId: 'client',
        silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
    }
};

export default config;