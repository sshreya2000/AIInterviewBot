import http from './httpClient';

const authApi = {
  register: (payload) => http.post('/api/v1/auth/register', payload),
  login: (payload) => http.post('/api/v1/auth/login', payload),
  me: () => http.get('/api/v1/auth/me'),
};

export default authApi;
