const auth = {
  save(data) {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('user', JSON.stringify(data.user));
  },
  getUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },
  isAuthenticated() { return !!localStorage.getItem('accessToken'); },
  isAdmin() {
    const user = this.getUser();
    return user?.role === 'ADMIN';
  },
  logout() {
    localStorage.clear();
    window.location.href = 'index.html';
  },
  requireAuth() {
    if (!this.isAuthenticated()) {
      window.location.href = 'login.html';
      return false;
    }
    return true;
  },
};
