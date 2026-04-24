import http from './httpClient';

const assessmentApi = {
  uploadResume: (file) => {
    const form = new FormData();
    form.append('file', file);
    return http.post('/api/v1/assessment/resume/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  uploadJd: (file) => {
    const form = new FormData();
    form.append('file', file);
    return http.post('/api/v1/assessment/jd/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  startInterview: (payload) =>
    http.post('/api/v1/assessment/interview/start', payload),

  nextQuestion: (sessionId) =>
    http.get(`/api/v1/assessment/interview/${sessionId}/next-question`),

  submitTextAnswer: (sessionId, payload) =>
    http.post(`/api/v1/assessment/interview/${sessionId}/answer/text`, payload),

  completeInterview: (sessionId) =>
    http.post(`/api/v1/assessment/interview/${sessionId}/complete`),

  getResult: (sessionId) =>
    http.get(`/api/v1/assessment/result/${sessionId}`),
};

export default assessmentApi;
