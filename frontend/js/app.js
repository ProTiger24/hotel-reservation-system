let selectedRoom = null;
let selectedPayment = null;
let bookingData = {};

document.addEventListener('DOMContentLoaded', function() {
    setDateDefaults();
    loadRooms();
    setupNavigation();
    setupDateChangeListener();  // ← নতুন ফাংশন
    
    const savedEmail = localStorage.getItem('userEmail');
    if (savedEmail) {
        document.getElementById('userEmailDisplay').textContent = '👤 ' + savedEmail;
    }
});

function setDateDefaults() {
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dayAfter = new Date(today);
    dayAfter.setDate(dayAfter.getDate() + 2);
    
    document.getElementById('checkIn').value = tomorrow.toISOString().split('T')[0];
    document.getElementById('checkOut').value = dayAfter.toISOString().split('T')[0];
}

// Date Change Listener - Auto Search
function setupDateChangeListener() {
    const checkIn = document.getElementById('checkIn');
    const checkOut = document.getElementById('checkOut');
    const guests = document.getElementById('guests');
    
    checkIn.addEventListener('change', function() {
        autoSearchRooms();
    });
    
    checkOut.addEventListener('change', function() {
        autoSearchRooms();
    });
    
    guests.addEventListener('change', function() {
        autoSearchRooms();
    });
}

// Auto Search Function
function autoSearchRooms() {
    const checkIn = document.getElementById('checkIn').value;
    const checkOut = document.getElementById('checkOut').value;
    const guests = document.getElementById('guests').value;
    
    if (!checkIn || !checkOut) return;
    if (new Date(checkIn) >= new Date(checkOut)) {
        showError('Check-out must be after check-in');
        return;
    }
    
    console.log('🔍 Auto searching rooms for:', checkIn, checkOut, guests);
    loadRooms();
}

async function loadRooms() {
    showLoading(true);
    try {
        const rooms = await API.getAvailableRooms();
        const guests = parseInt(document.getElementById('guests').value) || 1;
        const filtered = rooms.filter(r => r.capacity >= guests);
        displayRooms(filtered);
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to load rooms');
    }
    showLoading(false);
}

function displayRooms(rooms) {
    const list = document.getElementById('roomList');
    if (!rooms || rooms.length === 0) {
        list.innerHTML = `<div class="empty-state"><i class="fas fa-bed"></i><p>No rooms available for selected dates</p></div>`;
        return;
    }
    
    list.innerHTML = rooms.map(room => `
        <div class="room-card">
            <div class="room-image"><i class="fas fa-hotel"></i></div>
            <div class="room-info">
                <h3>${room.type}</h3>
                <div class="room-meta">
                    <span><i class="fas fa-users"></i> ${room.capacity} Guests</span>
                    <span><i class="fas fa-tag"></i> ৳${room.price}/night</span>
                </div>
                <p>${room.description || 'Comfortable room with modern amenities'}</p>
                <div class="amenities">
                    ${room.amenities ? room.amenities.split(',').map(a => 
                        `<span><i class="fas fa-check-circle"></i> ${a.trim()}</span>`
                    ).join('') : '<span>Standard amenities</span>'}
                </div>
                <button onclick="openBookingModal(${room.id})" class="btn-book">
                    <i class="fas fa-calendar-check"></i> Book Now
                </button>
            </div>
        </div>
    `).join('');
}

async function openBookingModal(roomId) {
    try {
        const rooms = await API.getRooms();
        selectedRoom = rooms.find(r => r.id === roomId);
        if (!selectedRoom) {
            showError('Room not found!');
            return;
        }
        
        const modal = document.getElementById('bookingModal');
        modal.style.display = 'flex';
        
        const checkIn = document.getElementById('checkIn').value;
        const checkOut = document.getElementById('checkOut').value;
        const guests = parseInt(document.getElementById('guests').value) || 2;
        const nights = calculateNights(checkIn, checkOut);
        const total = selectedRoom.price * nights;
        
        bookingData = {
            roomId: selectedRoom.id,
            checkIn: checkIn,
            checkOut: checkOut,
            guests: guests,
            nights: nights,
            total: total
        };
        
        document.getElementById('bookingDetails').innerHTML = `
            <div class="booking-summary">
                <div><strong>🏠 Room:</strong> ${selectedRoom.type}</div>
                <div><strong>💰 Price:</strong> ৳${selectedRoom.price}/night</div>
                <div><strong>📅 Check-in:</strong> ${formatDate(checkIn)}</div>
                <div><strong>📅 Check-out:</strong> ${formatDate(checkOut)}</div>
                <div><strong>🌙 Nights:</strong> ${nights}</div>
                <div><strong>👥 Guests:</strong> ${guests}</div>
                <div class="total"><strong>Total:</strong> ৳${total}</div>
                <div style="grid-column:span 2;background:#fff3e0;padding:10px;border-radius:8px;margin-top:5px;">
                    <p style="margin:0;font-size:13px;color:#666;">
                        <i class="fas fa-info-circle" style="color:#ff9800;"></i>
                        Pay 30% (৳${(total * 0.30).toFixed(2)}) now, 
                        remaining 70% (৳${(total * 0.70).toFixed(2)}) at check-out
                    </p>
                </div>
            </div>
        `;
        
        document.getElementById('paymentForm').innerHTML = '';
        document.querySelectorAll('.payment-option').forEach(el => el.classList.remove('selected'));
        selectedPayment = null;
        
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to open booking modal');
    }
}

function closeModal() {
    document.getElementById('bookingModal').style.display = 'none';
}

function calculateNights(checkIn, checkOut) {
    const start = new Date(checkIn);
    const end = new Date(checkOut);
    return Math.ceil(Math.abs(end - start) / (1000 * 60 * 60 * 24));
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { 
        weekday: 'short', 
        month: 'short', 
        day: 'numeric', 
        year: 'numeric' 
    });
}

function setupNavigation() {
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.dataset.page;
            document.querySelectorAll('.nav-links a').forEach(l => l.classList.remove('active'));
            this.classList.add('active');
            
            if (page === 'bookings') {
                document.getElementById('bookingsSection').style.display = 'block';
                document.querySelector('.hero').style.display = 'none';
                document.querySelector('.rooms-section').style.display = 'none';
                loadBookings();
            } else {
                document.getElementById('bookingsSection').style.display = 'none';
                document.querySelector('.hero').style.display = 'block';
                document.querySelector('.rooms-section').style.display = 'block';
                loadRooms();
            }
        });
    });
}

async function searchRooms() {
    const checkIn = document.getElementById('checkIn').value;
    const checkOut = document.getElementById('checkOut').value;
    const guests = document.getElementById('guests').value;
    
    if (!checkIn || !checkOut) {
        showError('Please select check-in and check-out dates');
        return;
    }
    
    if (new Date(checkIn) >= new Date(checkOut)) {
        showError('Check-out must be after check-in');
        return;
    }
    
    await loadRooms();
}

async function loadBookings() {
    let email = localStorage.getItem('userEmail');
    if (!email) {
        email = prompt('📧 Enter your email to view bookings:');
        if (!email) return;
    }
    
    showLoading(true);
    try {
        const bookings = await API.getBookingsByEmail(email);
        if (bookings && bookings.length > 0) {
            localStorage.setItem('userEmail', email);
            document.getElementById('userEmailDisplay').textContent = '👤 ' + email;
        }
        displayBookings(bookings, email);
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to load bookings');
    }
    showLoading(false);
}

function displayBookings(bookings, email) {
    const list = document.getElementById('bookingList');
    
    if (!bookings || bookings.length === 0) {
        list.innerHTML = `<div class="empty-state"><i class="fas fa-calendar-times"></i><p>No bookings found for ${email}</p></div>`;
        return;
    }
    
    list.innerHTML = bookings.map(b => {
        const statusColor = b.status === 'CONFIRMED' ? '#ff9800' : 
                           b.status === 'CHECKED_IN' ? '#2196f3' : 
                           b.status === 'COMPLETED' ? '#4caf50' : '#e74c3c';
        const statusIcon = b.status === 'CONFIRMED' ? '⏳' :
                          b.status === 'CHECKED_IN' ? '🏨' :
                          b.status === 'COMPLETED' ? '✅' : '❌';
        
        let actionButtons = '';
        if (b.status === 'CONFIRMED' && !b.isCheckedIn) {
            actionButtons = `
                <button onclick="checkInBooking(${b.id})" class="btn-checkin" style="background:#2196f3;color:white;border:none;padding:8px 15px;border-radius:8px;cursor:pointer;margin:5px;">
                    <i class="fas fa-sign-in-alt"></i> Check-in
                </button>
                <button onclick="cancelBooking(${b.id})" class="btn-cancel" style="background:#e74c3c;color:white;border:none;padding:8px 15px;border-radius:8px;cursor:pointer;margin:5px;">
                    <i class="fas fa-times"></i> Cancel
                </button>
            `;
        } else if (b.status === 'CHECKED_IN' && b.isCheckedIn && !b.isCheckedOut) {
            actionButtons = `
                <button onclick="checkOutBooking(${b.id})" class="btn-checkout" style="background:#4caf50;color:white;border:none;padding:8px 15px;border-radius:8px;cursor:pointer;margin:5px;">
                    <i class="fas fa-sign-out-alt"></i> Check-out & Pay
                </button>
            `;
        }
        
        return `
        <div class="booking-item" style="border-left:5px solid ${statusColor};padding:15px;margin:10px 0;background:white;border-radius:10px;box-shadow:0 2px 5px rgba(0,0,0,0.1);">
            <div class="booking-info">
                <h4>${statusIcon} ${b.room?.type || 'Unknown'} Room</h4>
                <p><i class="fas fa-user"></i> ${b.guestName}</p>
                <p><i class="fas fa-envelope"></i> ${b.guestEmail}</p>
                <p><i class="fas fa-phone"></i> ${b.guestPhone}</p>
                <p><i class="fas fa-calendar-alt"></i> ${formatDate(b.checkIn)} - ${formatDate(b.checkOut)}</p>
                <p><i class="fas fa-users"></i> ${b.guests} guests</p>
                <p><i class="fas fa-money-bill-wave"></i> Total: ৳${b.totalAmount}</p>
                <p><i class="fas fa-hand-holding-usd"></i> Advance: ৳${b.advancePaid?.toFixed(2) || 0} | Remaining: ৳${b.remainingAmount?.toFixed(2) || 0}</p>
                <p><i class="fas fa-credit-card"></i> ${b.paymentMethod}</p>
                <p><i class="fas fa-info-circle"></i> Status: <span style="color:${statusColor};font-weight:bold;">${b.status}</span></p>
                ${b.checkInTime ? `<p><i class="fas fa-sign-in-alt"></i> Checked in: ${new Date(b.checkInTime).toLocaleString()}</p>` : ''}
                ${b.checkOutTime ? `<p><i class="fas fa-sign-out-alt"></i> Checked out: ${new Date(b.checkOutTime).toLocaleString()}</p>` : ''}
                ${b.transactionId ? `<p style="font-size:12px;color:#888;"><i class="fas fa-receipt"></i> Transaction: ${b.transactionId}</p>` : ''}
            </div>
            <div class="booking-actions" style="margin-top:10px;">
                ${actionButtons}
            </div>
        </div>
    `}).join('');
}

async function checkInBooking(id) {
    if (!confirm('Are you sure you want to check in to this room?')) return;
    
    showLoading(true);
    try {
        const result = await API.checkIn(id);
        showSuccess('✅ Check-in successful! Welcome to your room! 🏨');
        loadBookings();
        loadRooms();
    } catch (error) {
        console.error('Error:', error);
        showError('Check-in failed: ' + error.message);
    }
    showLoading(false);
}

async function checkOutBooking(id) {
    if (!confirm('Are you sure you want to check out? This will process the remaining payment.')) return;
    
    showLoading(true);
    try {
        const result = await API.checkOut(id);
        showSuccess('✅ Check-out successful! Remaining payment processed. Thank you for staying with us! 🎉');
        loadBookings();
        loadRooms();
    } catch (error) {
        console.error('Error:', error);
        showError('Check-out failed: ' + error.message);
    }
    showLoading(false);
}

async function cancelBooking(id) {
    if (!confirm('Are you sure you want to cancel this booking?')) return;
    
    showLoading(true);
    try {
        await API.cancelBooking(id);
        showSuccess('✅ Booking cancelled successfully!');
        loadBookings();
        loadRooms();
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to cancel booking: ' + error.message);
    }
    showLoading(false);
}

function showLoading(show) {
    document.getElementById('loading').style.display = show ? 'block' : 'none';
}

function showError(message) {
    alert('❌ ' + message);
}

function showSuccess(message) {
    alert('✅ ' + message);
}
