-- ============================================
-- V2: Booking Features & Extensions
-- ============================================

-- Add new columns to users
ALTER TABLE users 
  ADD COLUMN IF NOT EXISTS loyalty_points INT NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS tier VARCHAR(20) NOT NULL DEFAULT 'BRONZE',
  ADD COLUMN IF NOT EXISTS birthday DATE,
  ADD COLUMN IF NOT EXISTS profile_image VARCHAR(500);

-- Add new columns to bookings
ALTER TABLE bookings
  ADD COLUMN IF NOT EXISTS payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  ADD COLUMN IF NOT EXISTS advance_paid DECIMAL(10,2) NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS loyalty_points_earned INT NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS special_request TEXT,
  ADD COLUMN IF NOT EXISTS created_by_admin BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS guest_count INT NOT NULL DEFAULT 1;

-- Add new columns to rooms
ALTER TABLE rooms
  ADD COLUMN IF NOT EXISTS description TEXT,
  ADD COLUMN IF NOT EXISTS size_sqm INT,
  ADD COLUMN IF NOT EXISTS has_balcony BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS has_breakfast BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS has_wifi BOOLEAN NOT NULL DEFAULT TRUE,
  ADD COLUMN IF NOT EXISTS has_ac BOOLEAN NOT NULL DEFAULT TRUE;

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    room_id         BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    booking_id      BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    rating          INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment         TEXT,
    image_url       VARCHAR(500),
    status          VARCHAR(20) NOT NULL DEFAULT 'APPROVED',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_reviews_room ON reviews(room_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user ON reviews(user_id);

-- Wishlist
CREATE TABLE IF NOT EXISTS wishlist (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    room_id         BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, room_id)
);
CREATE INDEX IF NOT EXISTS idx_wishlist_user ON wishlist(user_id);

-- Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            VARCHAR(30) NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT,
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    link            VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(is_read);

-- Loyalty transactions
CREATE TABLE IF NOT EXISTS loyalty_transactions (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    points          INT NOT NULL,
    type            VARCHAR(20) NOT NULL,
    description     VARCHAR(500),
    booking_id      BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_loyalty_user ON loyalty_transactions(user_id);

-- Update seed data for rooms
UPDATE rooms SET 
  size_sqm = CASE room_number
    WHEN '101' THEN 22 WHEN '102' THEN 22
    WHEN '201' THEN 30 WHEN '202' THEN 30
    WHEN '301' THEN 42 WHEN '401' THEN 65
  END,
  has_balcony = room_number IN ('301', '401'),
  has_breakfast = room_number IN ('201', '202', '301', '401'),
  description = CASE room_number
    WHEN '101' THEN 'Cozy single room perfect for solo travelers'
    WHEN '102' THEN 'Comfortable single room with city view'
    WHEN '201' THEN 'Spacious double room for couples'
    WHEN '202' THEN 'Modern double room with premium amenities'
    WHEN '301' THEN 'Luxurious deluxe room with balcony and city view'
    WHEN '401' THEN 'Premium suite with living area and panoramic view'
  END;
