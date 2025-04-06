const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  const backendProxy = createProxyMiddleware({
    target: 'http://localhost:8080',
    changeOrigin: true,
    onProxyRes: (proxyRes) => {
      proxyRes.headers['Access-Control-Allow-Origin'] = '*';
      proxyRes.headers['Access-Control-Allow-Methods'] = 'GET, POST, PUT, DELETE, PATCH, OPTIONS';
      proxyRes.headers['Access-Control-Allow-Headers'] = 'Content-Type, Authorization';
    }
  });

  app.use('/topics', backendProxy);
  app.use('/search', backendProxy);
  app.use('/users', backendProxy);
  app.use('/ratings', backendProxy);
  app.use('/comments', backendProxy);
  
  app.use('/api', backendProxy);
};