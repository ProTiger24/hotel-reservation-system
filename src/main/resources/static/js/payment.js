function selectPayment(method) {
    selectedPayment = method;
    
    document.querySelectorAll('.payment-option').forEach(el => {
        el.classList.remove('selected');
    });
    event.currentTarget.classList.add('selected');
    
    const form = document.getElementById('paymentForm');
    form.style.display = 'block';
    form.innerHTML = getPaymentForm(method);
}

function getPaymentForm(method) {
    // Guest Details Form
    const guestForm = `
        <div style="background:#f0f3ff;padding:15px;border-radius:10px;margin-bottom:15px;border:2px solid #667eea;">
            <h4 style="color:#333;margin-bottom:10px;"><i class="fas fa-user" style="color:#667eea;"></i> Guest Details</h4>
            <p style="font-size:12px;color:#666;margin-bottom:10px;">Please fill your details for booking confirmation</p>
            <input type="text" id="guestName" placeholder="Full Name *" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
            <input type="email" id="guestEmail" placeholder="Email Address *" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
            <input type="tel" id="guestPhone" placeholder="Phone Number *" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
        </div>
    `;
    
    // Payment Info - 30% Advance
    const advanceInfo = `
        <div style="background:#fff3e0;padding:12px;border-radius:8px;margin:10px 0;border-left:4px solid #ff9800;">
            <p style="font-size:13px;color:#666;margin:0;">
                <i class="fas fa-info-circle" style="color:#ff9800;"></i>
                <strong>Payment Policy:</strong> Pay 30% advance now, remaining 70% at check-out
            </p>
            <p style="font-size:12px;color:#999;margin:5px 0 0 0;">
                Advance: ৳${(bookingData.total * 0.30).toFixed(2)} | 
                Remaining: ৳${(bookingData.total * 0.70).toFixed(2)}
            </p>
        </div>
    `;
    
    const forms = {
        'BKASH': `
            ${guestForm}
            ${advanceInfo}
            <h4 style="color:#333;margin-bottom:10px;"><i class="fas fa-mobile-alt" style="color:#e67e22;"></i> bKash Payment (30% Advance)</h4>
            <div style="background:#e8f5e9;padding:12px;border-radius:8px;margin:10px 0;">
                <small>📱 Demo: 01712345678 | PIN: 1234</small>
            </div>
            <input type="text" id="bkashNumber" placeholder="bKash Number" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
            <input type="password" id="bkashPin" placeholder="PIN (4 digits)" maxlength="4" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
        `,
        'CARD': `
            ${guestForm}
            ${advanceInfo}
            <h4 style="color:#333;margin-bottom:10px;"><i class="fas fa-credit-card" style="color:#3498db;"></i> Card Payment (30% Advance)</h4>
            <div style="background:#e3f2fd;padding:12px;border-radius:8px;margin:10px 0;">
                <small>💳 Demo: 4242 4242 4242 4242 | Exp: 12/25 | CVV: 123</small>
            </div>
            <input type="text" id="cardNumber" placeholder="Card Number" maxlength="19" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;">
                <input type="text" id="cardExpiry" placeholder="MM/YY" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
                <input type="password" id="cardCvv" placeholder="CVV" maxlength="4" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
            </div>
            <input type="text" id="cardHolderName" placeholder="Cardholder Name" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
        `,
        'BANK': `
            ${guestForm}
            ${advanceInfo}
            <h4 style="color:#333;margin-bottom:10px;"><i class="fas fa-university" style="color:#2ecc71;"></i> Bank Transfer (30% Advance)</h4>
            <div style="background:#fff3e0;padding:12px;border-radius:8px;margin:10px 0;">
                <small>🏦 Demo: Any bank details</small>
            </div>
            <input type="text" id="bankName" placeholder="Bank Name" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
            <input type="text" id="accountNumber" placeholder="Account Number" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
            <input type="text" id="accountHolder" placeholder="Account Holder Name" required style="width:100%;padding:12px;margin:5px 0;border:2px solid #e0e0e0;border-radius:8px;font-size:14px;">
        `
    };
    return forms[method] || '<p style="color:#999;text-align:center;padding:20px;">Select payment method</p>';
}

async function processPayment() {
    if (!selectedRoom) {
        showError('Please select a room');
        return;
    }
    
    if (!selectedPayment) {
        showError('Please select a payment method');
        return;
    }
    
    const guestName = document.getElementById('guestName')?.value.trim();
    const guestEmail = document.getElementById('guestEmail')?.value.trim();
    const guestPhone = document.getElementById('guestPhone')?.value.trim();
    
    if (!guestName || guestName.length < 2) {
        showError('Please enter your full name');
        return;
    }
    
    if (!guestEmail || !guestEmail.includes('@')) {
        showError('Please enter valid email');
        return;
    }
    
    if (!guestPhone || guestPhone.length < 11) {
        showError('Please enter valid phone number');
        return;
    }
    
    const paymentData = collectPaymentData();
    if (!paymentData) return;
    
    const btn = document.getElementById('payBtn');
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing Advance Payment...';
    
    try {
        // Process 30% advance payment
        const advanceAmount = bookingData.total * 0.30;
        const paymentResult = await API.processPayment({
            method: selectedPayment,
            amount: advanceAmount,
            ...paymentData
        });
        
        if (!paymentResult.success) {
            showError(paymentResult.message || 'Advance payment failed');
            btn.disabled = false;
            btn.innerHTML = '<i class="fas fa-check"></i> Confirm & Pay';
            return;
        }
        
        // Create booking with advance payment
        const bookingPayload = {
            room: { id: selectedRoom.id },
            guestName: guestName,
            guestEmail: guestEmail,
            guestPhone: guestPhone,
            checkIn: bookingData.checkIn,
            checkOut: bookingData.checkOut,
            guests: bookingData.guests,
            totalAmount: bookingData.total,
            advancePaid: advanceAmount,
            remainingAmount: bookingData.total - advanceAmount,
            paymentMethod: selectedPayment,
            paymentStatus: 'ADVANCE_PAID',
            transactionId: paymentResult.transactionId || 'TXN-' + Date.now(),
            status: 'CONFIRMED'
        };
        
        const booking = await API.createBooking(bookingPayload);
        
        localStorage.setItem('userEmail', guestEmail);
        
        showSuccess(
            '✅ Advance Payment Successful!\n\n' +
            '🏠 Room: ' + selectedRoom.type + '\n' +
            '🆔 Booking ID: #' + booking.id + '\n' +
            '💰 Advance Paid: ৳' + advanceAmount.toFixed(2) + ' (30%)\n' +
            '💵 Remaining: ৳' + (bookingData.total - advanceAmount).toFixed(2) + ' (70%)\n' +
            '📅 Check-in: ' + formatDate(bookingData.checkIn) + '\n' +
            '📧 Email: ' + guestEmail + '\n\n' +
            '💡 Pay remaining at check-out'
        );
        
        closeModal();
        loadRooms();
        
    } catch (error) {
        console.error('Error:', error);
        showError('Payment failed: ' + error.message);
    }
    
    btn.disabled = false;
    btn.innerHTML = '<i class="fas fa-check"></i> Confirm & Pay';
}

function collectPaymentData() {
    const data = {};
    const inputs = document.getElementById('paymentForm').querySelectorAll('input');
    let isValid = true;
    const skipIds = ['guestName', 'guestEmail', 'guestPhone'];
    
    inputs.forEach(input => {
        if (skipIds.includes(input.id)) return;
        
        const value = input.value.trim();
        if (!value) {
            isValid = false;
            input.style.borderColor = '#e74c3c';
        } else {
            input.style.borderColor = '#27ae60';
            const id = input.id;
            if (id === 'bkashNumber') data.bkashNumber = value;
            else if (id === 'bkashPin') data.pin = value;
            else if (id === 'cardNumber') data.cardNumber = value;
            else if (id === 'cardExpiry') data.expiryDate = value;
            else if (id === 'cardCvv') data.cvv = value;
            else if (id === 'cardHolderName') data.cardHolderName = value;
            else if (id === 'bankName') data.bankName = value;
            else if (id === 'accountNumber') data.accountNumber = value;
            else if (id === 'accountHolder') data.accountHolder = value;
        }
    });
    
    if (!isValid) {
        showError('Please fill all payment details');
        return null;
    }
    return data;
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
