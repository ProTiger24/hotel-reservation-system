
const API_BASE_URL = 'https://hotel-reservation-system-update.onrender.com/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      if (!window.location.pathname.includes('login')) {
        window.location.href = 'login.html';
      }
    }
    return Promise.reject(error);
  }
);

const authApi = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

const roomApi = {
  getAll: () => api.get('/rooms'),
  getAvailable: () => api.get('/rooms/available'),
  getById: (id) => api.get(`/rooms/${id}`),
};

const bookingApi = {
  create: (data) => api.post('/bookings', data),
  myBookings: () => api.get('/bookings/my'),
  cancel: (id) => api.put(`/bookings/${id}/cancel`),
};

const adminApi = {
  getAllBookings: () => api.get('/admin/bookings'),
  confirm: (id) => api.put(`/admin/bookings/${id}/confirm`),
  reject: (id) => api.put(`/admin/bookings/${id}/reject`),
};