-- ============================================
-- V4: Travel Management Module
-- ============================================

-- Vehicles
CREATE TABLE IF NOT EXISTS vehicles (
    id                  BIGSERIAL PRIMARY KEY,
    vehicle_number      VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type        VARCHAR(50) NOT NULL,
    brand               VARCHAR(50),
    model               VARCHAR(50),
    seat_capacity       INT NOT NULL,
    ac_type             VARCHAR(20) DEFAULT 'AC',
    per_hour_price      DECIMAL(10, 2),
    per_day_price       DECIMAL(10, 2) NOT NULL,
    image_url           TEXT,
    status              VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    description         TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_vehicles_status ON vehicles(status);
CREATE INDEX IF NOT EXISTS idx_vehicles_type ON vehicles(vehicle_type);

-- Drivers
CREATE TABLE IF NOT EXISTS drivers (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    phone               VARCHAR(20) NOT NULL,
    license_number      VARCHAR(50) UNIQUE,
    experience_years    INT DEFAULT 0,
    assigned_vehicle_id BIGINT REFERENCES vehicles(id) ON DELETE SET NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    image_url           TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_drivers_status ON drivers(status);

-- Destinations
CREATE TABLE IF NOT EXISTS destinations (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    distance_km     INT,
    estimated_hours INT,
    base_fare       DECIMAL(10, 2),
    image_url       VARCHAR(500),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Promo Codes
CREATE TABLE IF NOT EXISTS promo_codes (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(50) NOT NULL UNIQUE,
    description         VARCHAR(200),
    discount_type       VARCHAR(20) NOT NULL DEFAULT 'PERCENTAGE',
    discount_value      DECIMAL(10, 2) NOT NULL,
    max_discount        DECIMAL(10, 2),
    min_order_amount    DECIMAL(10, 2) DEFAULT 0,
    applies_to          VARCHAR(20) NOT NULL DEFAULT 'ALL',
    valid_from          DATE,
    valid_until         DATE,
    usage_limit         INT,
    used_count          INT NOT NULL DEFAULT 0,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_promo_code ON promo_codes(code);

-- Travel Packages
CREATE TABLE IF NOT EXISTS travel_packages (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    description         TEXT,
    destination_id      BIGINT REFERENCES destinations(id),
    includes_room       BOOLEAN NOT NULL DEFAULT TRUE,
    includes_vehicle    BOOLEAN NOT NULL DEFAULT TRUE,
    room_type_id        BIGINT REFERENCES room_types(id),
    vehicle_id          BIGINT REFERENCES vehicles(id),
    duration_days       INT NOT NULL DEFAULT 1,
    duration_nights     INT NOT NULL DEFAULT 0,
    base_price          DECIMAL(10, 2) NOT NULL,
    discount_percent    INT NOT NULL DEFAULT 0,
    discount_amount     DECIMAL(10, 2) NOT NULL DEFAULT 0,
    final_price         DECIMAL(10, 2) NOT NULL,
    image_url           TEXT,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Vehicle Bookings
CREATE TABLE IF NOT EXISTS vehicle_bookings (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    vehicle_id          BIGINT REFERENCES vehicles(id),
    driver_id           BIGINT REFERENCES drivers(id),
    package_id          BIGINT REFERENCES travel_packages(id),
    booking_type        VARCHAR(30) NOT NULL DEFAULT 'VEHICLE_ONLY',
    destination         VARCHAR(200),
    pickup_location     VARCHAR(200),
    dropoff_location    VARCHAR(200),
    pickup_date         DATE NOT NULL,
    return_date         DATE NOT NULL,
    days                INT NOT NULL,
    base_price          DECIMAL(10, 2) NOT NULL,
    discount_amount     DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_price         DECIMAL(10, 2) NOT NULL,
    promo_code          VARCHAR(50),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    hotel_booking_id    BIGINT REFERENCES bookings(id),
    special_request     TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_vehicle_bookings_user ON vehicle_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_bookings_status ON vehicle_bookings(status);

-- Sample Data
INSERT INTO destinations (name, description, distance_km, estimated_hours, base_fare) VALUES
('Cox''s Bazar', 'World''s longest natural sea beach', 400, 8, 8000.00),
('Sylhet', 'Tea gardens and natural beauty', 250, 5, 6000.00),
('Rangamati', 'Hill tracts and lake', 320, 7, 7000.00),
('Sundarbans', 'Mangrove forest', 300, 6, 7500.00),
('Bandarban', 'Hill district with waterfalls', 350, 7, 8000.00)
ON CONFLICT DO NOTHING;

INSERT INTO vehicles (vehicle_number, vehicle_type, brand, model, seat_capacity, ac_type, per_hour_price, per_day_price, status, description) VALUES
('DHK-1001', 'Car', 'Toyota', 'Premio', 4, 'AC', 800.00, 5500.00, 'AVAILABLE', 'Comfortable sedan for city tours and short trips'),
('DHK-1002', 'Microbus', 'Toyota', 'Noah', 7, 'AC', 1000.00, 7000.00, 'AVAILABLE', 'Perfect for family trips'),
('DHK-1003', 'Hiace', 'Toyota', 'Hiace', 12, 'AC', 1500.00, 10000.00, 'AVAILABLE', 'Spacious van for group tours'),
('DHK-1004', 'Car', 'Nissan', 'X-Trail', 5, 'AC', 1200.00, 8500.00, 'AVAILABLE', 'Premium SUV for comfort travel'),
('DHK-1005', 'Bus', 'Hino', 'AK1J', 30, 'AC', 2500.00, 18000.00, 'AVAILABLE', 'Large bus for big groups')
ON CONFLICT DO NOTHING;

INSERT INTO promo_codes (code, description, discount_type, discount_value, min_order_amount, applies_to, valid_until, is_active) VALUES
('WELCOME10', 'Welcome discount for new users', 'PERCENTAGE', 10.00, 2000.00, 'ALL', '2027-12-31', TRUE),
('SUMMER500', 'Summer special discount', 'FIXED', 500.00, 5000.00, 'ALL', '2027-06-30', TRUE),
('ROOM15', 'Room booking discount', 'PERCENTAGE', 15.00, 3000.00, 'ROOM', '2027-12-31', TRUE),
('TRAVEL20', 'Travel package discount', 'PERCENTAGE', 20.00, 5000.00, 'TRAVEL', '2027-12-31', TRUE)
ON CONFLICT DO NOTHING;
