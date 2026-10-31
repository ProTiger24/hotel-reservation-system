-- ============================================
-- V3: Guest CRM Features
-- ============================================

-- Add new columns to users
ALTER TABLE users 
  ADD COLUMN IF NOT EXISTS address TEXT,
  ADD COLUMN IF NOT EXISTS id_number VARCHAR(50),
  ADD COLUMN IF NOT EXISTS nationality VARCHAR(50),
  ADD COLUMN IF NOT EXISTS phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS preferred_language VARCHAR(10) DEFAULT 'en',
  ADD COLUMN IF NOT EXISTS outstanding_balance DECIMAL(10, 2) NOT NULL DEFAULT 0;

-- Guest Notes
CREATE TABLE IF NOT EXISTS guest_notes (
    id              BIGSERIAL PRIMARY KEY,
    guest_id        BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    admin_id        BIGINT NOT NULL REFERENCES users(id),
    note            TEXT NOT NULL,
    is_internal     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_guest_notes_guest ON guest_notes(guest_id);
CREATE INDEX IF NOT EXISTS idx_guest_notes_admin ON guest_notes(admin_id);

-- Guest Tags
CREATE TABLE IF NOT EXISTS guest_tags (
    id              BIGSERIAL PRIMARY KEY,
    guest_id        BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tag             VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(guest_id, tag)
);
CREATE INDEX IF NOT EXISTS idx_guest_tags_guest ON guest_tags(guest_id);

-- Activity Log
CREATE TABLE IF NOT EXISTS activity_log (
    id              BIGSERIAL PRIMARY KEY,
    actor_id        BIGINT REFERENCES users(id) ON DELETE SET NULL,
    target_user_id  BIGINT REFERENCES users(id) ON DELETE CASCADE,
    action          VARCHAR(100) NOT NULL,
    entity          VARCHAR(50),
    entity_id       BIGINT,
    metadata        TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_activity_target ON activity_log(target_user_id);
CREATE INDEX IF NOT EXISTS idx_activity_actor ON activity_log(actor_id);
CREATE INDEX IF NOT EXISTS idx_activity_created ON activity_log(created_at DESC);

-- Guest Preferences
CREATE TABLE IF NOT EXISTS guest_preferences (
    id                      BIGSERIAL PRIMARY KEY,
    guest_id                BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    preferred_room_type     VARCHAR(50),
    preferred_floor         INT,
    dietary_requirements    TEXT,
    special_requests        TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_guest_pref_guest ON guest_preferences(guest_id);
