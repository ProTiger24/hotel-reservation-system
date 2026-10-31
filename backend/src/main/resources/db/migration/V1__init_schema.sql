CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    phone           VARCHAR(20),
    role            VARCHAR(20) NOT NULL DEFAULT 'USER',
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_users_email ON users(email);

CREATE TABLE room_types (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    base_price      DECIMAL(10, 2) NOT NULL,
    capacity        INT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE rooms (
    id              BIGSERIAL PRIMARY KEY,
    room_number     VARCHAR(20) NOT NULL UNIQUE,
    room_type_id    BIGINT NOT NULL REFERENCES room_types(id),
    floor           INT,
    status          VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    image_url       VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_type ON rooms(room_type_id);

CREATE TABLE bookings (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    room_id         BIGINT NOT NULL REFERENCES rooms(id),
    check_in        DATE NOT NULL,
    check_out       DATE NOT NULL,
    total_price     DECIMAL(10, 2) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_dates CHECK (check_out > check_in)
);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_room_dates ON bookings(room_id, check_in, check_out);
CREATE INDEX idx_bookings_status ON bookings(status);

CREATE TABLE payments (
    id                  BIGSERIAL PRIMARY KEY,
    booking_id          BIGINT NOT NULL REFERENCES bookings(id),
    amount              DECIMAL(10, 2) NOT NULL,
    method              VARCHAR(30) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_id      VARCHAR(100),
    gateway_response    TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_payments_booking ON payments(booking_id);
CREATE INDEX idx_payments_txn ON payments(transaction_id);

INSERT INTO room_types (name, description, base_price, capacity) VALUES
('Single', 'Single bed room', 2000.00, 1),
('Double', 'Double bed room', 3500.00, 2),
('Deluxe', 'Deluxe room with city view', 5500.00, 3),
('Suite', 'Luxury suite with living area', 9000.00, 4);

INSERT INTO rooms (room_number, room_type_id, floor, status) VALUES
('101', 1, 1, 'AVAILABLE'),
('102', 1, 1, 'AVAILABLE'),
('201', 2, 2, 'AVAILABLE'),
('202', 2, 2, 'AVAILABLE'),
('301', 3, 3, 'AVAILABLE'),
('401', 4, 4, 'AVAILABLE');
