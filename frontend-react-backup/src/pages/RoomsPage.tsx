import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { roomApi } from '../api/rooms';
import type { Room } from '../types';

export default function RoomsPage() {
  const { user, isAuthenticated, logout } = useAuth();

  const { data: rooms, isLoading, error } = useQuery({
    queryKey: ['rooms'],
    queryFn: roomApi.getAll,
  });

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navbar */}
      <nav className="bg-white shadow-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
          <Link to="/" className="text-2xl font-bold text-blue-600 flex items-center gap-2">
            <span>🏨</span> HotelReserve
          </Link>
          <div className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <Link to="/rooms" className="text-blue-600 font-medium">
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

      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white py-12">
        <div className="max-w-7xl mx-auto px-6">
          <h1 className="text-4xl font-bold mb-2">Our Rooms</h1>
          <p className="text-blue-100 text-lg">Choose your perfect stay from our collection</p>
        </div>
      </div>

      {/* Rooms Grid */}
      <div className="max-w-7xl mx-auto px-6 py-12">
        {isLoading && (
          <div className="text-center py-20">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-600 border-t-transparent"></div>
            <p className="text-gray-600 mt-4">Loading rooms...</p>
          </div>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-6 py-4 rounded-lg">
            Failed to load rooms. Make sure backend is running.
          </div>
        )}

        {rooms && rooms.length === 0 && (
          <div className="text-center py-20">
            <p className="text-gray-600 text-lg">No rooms available</p>
          </div>
        )}

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {rooms?.map((room: Room) => (
            <div
              key={room.id}
              className="bg-white rounded-2xl shadow-sm hover:shadow-lg transition overflow-hidden"
            >
              {/* Image placeholder */}
              <div className="h-48 bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center">
                <span className="text-6xl">🛏️</span>
              </div>

              <div className="p-6">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <h3 className="text-xl font-bold text-gray-800">
                      Room {room.roomNumber}
                    </h3>
                    <p className="text-blue-600 font-medium">{room.roomType}</p>
                  </div>
                  <span className="bg-green-100 text-green-700 text-xs font-semibold px-3 py-1 rounded-full">
                    {room.status}
                  </span>
                </div>

                <p className="text-gray-600 text-sm mb-4">
                  {room.description || 'Comfortable room for your stay'}
                </p>

                <div className="flex items-center gap-4 text-sm text-gray-500 mb-4">
                  <span>👥 {room.capacity} guests</span>
                  <span>🏢 Floor {room.floor}</span>
                </div>

                <div className="flex justify-between items-center pt-4 border-t border-gray-100">
                  <div>
                    <span className="text-2xl font-bold text-gray-800">৳{room.price}</span>
                    <span className="text-gray-500 text-sm">/night</span>
                  </div>
                  <Link
                    to={`/rooms/${room.id}`}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-lg font-medium transition"
                  >
                    Book Now
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
