import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function HomePage() {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
          <Link to="/" className="text-2xl font-bold text-blue-600 flex items-center gap-2">
            <span>🏨</span> HotelReserve
          </Link>
          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <Link to="/rooms" className="text-gray-700 hover:text-blue-600 font-medium">
                  Rooms
                </Link>
                <Link to="/my-bookings" className="text-gray-700 hover:text-blue-600 font-medium">
                  My Bookings
                </Link>
                <div className="flex items-center gap-3 pl-4 border-l border-gray-200">
                  <span className="text-gray-700 font-medium">Hi, {user?.name}</span>
                  <button
                    onClick={logout}
                    className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium transition"
                  >
                    Logout
                  </button>
                </div>
              </>
            ) : (
              <>
                <Link to="/login" className="text-gray-700 hover:text-blue-600 font-medium px-4 py-2">
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-lg font-medium transition shadow-sm"
                >
                  Register
                </Link>
              </>
            )}
          </div>
        </div>
      </nav>

      <section className="bg-gradient-to-br from-blue-600 to-indigo-700 text-white">
        <div className="max-w-7xl mx-auto px-6 py-24 text-center">
          <h1 className="text-5xl md:text-6xl font-bold mb-6">
            Book Your Perfect Stay
          </h1>
          <p className="text-xl md:text-2xl text-blue-100 max-w-3xl mx-auto mb-10">
            Discover comfortable rooms, great amenities, and unforgettable experiences
          </p>
          <Link
            to={isAuthenticated ? '/rooms' : '/register'}
            className="inline-block bg-white text-blue-600 hover:bg-blue-50 px-8 py-4 rounded-xl text-lg font-semibold transition shadow-lg"
          >
            {isAuthenticated ? 'Browse Rooms →' : 'Get Started →'}
          </Link>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-6 py-20">
        <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
          Why Choose Us?
        </h2>
        <div className="grid md:grid-cols-3 gap-8">
          <div className="bg-white rounded-2xl p-8 text-center shadow-sm hover:shadow-md transition">
            <div className="text-6xl mb-4">🛏️</div>
            <h3 className="text-xl font-bold text-gray-800 mb-2">Comfortable Rooms</h3>
            <p className="text-gray-600">Single, Double, Deluxe, and Suite options for every need</p>
          </div>
          <div className="bg-white rounded-2xl p-8 text-center shadow-sm hover:shadow-md transition">
            <div className="text-6xl mb-4">💰</div>
            <h3 className="text-xl font-bold text-gray-800 mb-2">Best Prices</h3>
            <p className="text-gray-600">Affordable rates starting from ৳2000 per night</p>
          </div>
          <div className="bg-white rounded-2xl p-8 text-center shadow-sm hover:shadow-md transition">
            <div className="text-6xl mb-4">⚡</div>
            <h3 className="text-xl font-bold text-gray-800 mb-2">Easy Booking</h3>
            <p className="text-gray-600">Book your room in just a few clicks</p>
          </div>
        </div>
      </section>

      <footer className="bg-gray-800 text-gray-300 py-8">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <p>&copy; 2026 HotelReserve. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
