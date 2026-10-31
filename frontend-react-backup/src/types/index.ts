export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: 'USER' | 'ADMIN';
}

export interface RoomType {
  id: number;
  name: string;
  description: string;
  basePrice: number;
  capacity: number;
}

export interface Room {
  id: number;
  roomNumber: string;
  roomType: RoomType;
  floor: number;
  status: 'AVAILABLE' | 'BOOKED' | 'MAINTENANCE';
  imageUrl?: string;
}

export interface Booking {
  id: number;
  user: User;
  room: Room;
  checkIn: string;
  checkOut: string;
  totalPrice: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phone?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface BookingRequest {
  roomId: number;
  checkIn: string;
  checkOut: string;
}
