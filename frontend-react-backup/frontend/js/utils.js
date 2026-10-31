const utils = {
  formatPrice(amount) {
    return `৳${Number(amount).toLocaleString('en-BD')}`;
  },
  formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-GB', {
      day: '2-digit', month: 'short', year: 'numeric',
    });
  },
  showAlert(message, type = 'info') {
    const colors = {
      success: 'bg-green-100 border-green-500 text-green-700',
      error: 'bg-red-100 border-red-500 text-red-700',
      warning: 'bg-yellow-100 border-yellow-500 text-yellow-700',
      info: 'bg-blue-100 border-blue-500 text-blue-700',
    };
    const div = document.createElement('div');
    div.className = `fixed top-4 right-4 z-50 ${colors[type]} border-l-4 px-6 py-4 rounded shadow-lg max-w-sm fade-in`;
    div.textContent = message;
    document.body.appendChild(div);
    setTimeout(() => div.remove(), 3000);
  },
  getParam(name) {
    return new URLSearchParams(window.location.search).get(name);
  },
};
