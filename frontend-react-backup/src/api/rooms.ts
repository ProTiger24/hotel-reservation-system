import api from './axios';
import type { ApiResponse, Room } from '../types';

export const roomApi = {
  getAll: async (): Promise<Room[]> => {
    const response = await api.get<ApiResponse<Room[]>>('/rooms');
    return response.data.data;
  },

  getAvailable: async (): Promise<Room[]> => {
    const response = await api.get<ApiResponse<Room[]>>('/rooms/available');
    return response.data.data;
  },

  getById: async (id: number): Promise<Room> => {
    const response = await api.get<ApiResponse<Room>>(`/rooms/${id}`);
    return response.data.data;
  },
};
