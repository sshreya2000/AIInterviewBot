import http from './httpClient';

const aiApi = {
  generateQuestion: (payload) =>
    http.post('/api/v1/ai/question', payload),

  evaluateAnswer: (payload) =>
    http.post('/api/v1/ai/evaluate', payload),

  extractSkills: (payload) =>
    http.post('/api/v1/ai/extract-skills', payload),
};

export default aiApi;
