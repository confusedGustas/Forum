const config = {
    api: {
        baseUrl: '',
        
        endpoints: {
            topics: '/topics',
            topic: (id: string) => `/topics/${id}`,
            rating: '/ratings',
            searchTopics: (id: string) => `/search/topics/${id}`,
            userTopics: '/users/me/topics',
            comments: '/comments',
            topicComments: (topicId: string) => `/comments/topics/${topicId}`,
            comment: (commentId: string) => `/comments/${commentId}`,
            commentReplies: (commentId: string) => `/comments/${commentId}/replies`,
            communities: '/communities',
            community: (id: string) => `/communities/${id}`
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