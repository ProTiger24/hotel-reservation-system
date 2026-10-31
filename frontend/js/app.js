function loadNavbar() {
  const navbar = document.getElementById('navbar');
  if (!navbar) return;
  const user = auth.getUser();
  const isAuth = auth.isAuthenticated();
  let authLinks = '';
  if (isAuth && user) {
    authLinks = `
      <a href="rooms.html" class="text-gray-700 hover:text-primary-600 font-medium">Rooms</a>
      <a href="my-bookings.html" class="text-gray-700 hover:text-primary-600 font-medium">My Bookings</a>
      ${auth.isAdmin() ? '<a href="admin.html" class="text-gray-700 hover:text-primary-600 font-medium">Admin</a>' : ''}
      <div class="flex items-center gap-3 pl-4 border-l border-gray-200">
        <span class="text-gray-700 font-medium">Hi, ${user.name}</span>
        <button onclick="auth.logout()" class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium">Logout</button>
      </div>
    `;
  } else {
    authLinks = `
      <a href="login.html" class="text-gray-700 hover:text-primary-600 font-medium px-4 py-2">Login</a>
      <a href="register.html" class="bg-primary-600 hover:bg-primary-700 text-white px-5 py-2 rounded-lg font-medium shadow-sm">Register</a>
    `;
  }
  navbar.innerHTML = `
    <div class="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
      <a href="index.html" class="text-2xl font-bold text-primary-600 flex items-center gap-2">
        <span>🏨</span> HotelReserve
      </a>
      <div class="flex items-center gap-4">${authLinks}</div>
    </div>
  `;
}
document.addEventListener('DOMContentLoaded', loadNavbar);
