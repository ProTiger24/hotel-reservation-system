const API_BASE_URL = 'http://localhost:8080/api';

const API = {
    // Rooms
    getRooms: async () => {
        const res = await fetch(`${API_BASE_URL}/rooms`);
        return await res.json();
    },
    
    getAvailableRooms: async () => {
        const res = await fetch(`${API_BASE_URL}/rooms/available`);
        return await res.json();
    },
    
    // Bookings
    createBooking: async (data) => {
        const res = await fetch(`${API_BASE_URL}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return await res.json();
    },
    
    getBookingsByEmail: async (email) => {
        const res = await fetch(`${API_BASE_URL}/bookings/email/${email}`);
        return await res.json();
    },
    
    // Check-in / Check-out
    checkIn: async (id) => {
        const res = await fetch(`${API_BASE_URL}/bookings/${id}/checkin`, {
            method: 'PUT'
        });
        return await res.json();
    },
    
    checkOut: async (id) => {
        const res = await fetch(`${API_BASE_URL}/bookings/${id}/checkout`, {
            method: 'PUT'
        });
        return await res.json();
    },
    
    cancelBooking: async (id) => {
        const res = await fetch(`${API_BASE_URL}/bookings/${id}/cancel`, {
            method: 'PUT'
        });
        return await res.json();
    },
    
    // Payment
    processPayment: async (data) => {
        const res = await fetch(`${API_BASE_URL}/payments/process`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return await res.json();
    }
};
