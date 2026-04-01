-- Add OAuth2-related columns in a safe order for existing data.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS google_id VARCHAR(255);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS profile_picture_url VARCHAR(1000);

-- Backfill legacy users before enforcing NOT NULL.
UPDATE users
SET auth_provider = 'LOCAL'
WHERE auth_provider IS NULL;

ALTER TABLE users
    ALTER COLUMN auth_provider SET DEFAULT 'LOCAL';

ALTER TABLE users
    ALTER COLUMN auth_provider SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'users_auth_provider_check'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT users_auth_provider_check
                CHECK (auth_provider IN ('LOCAL', 'GOOGLE'));
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_google_id
    ON users (google_id)
    WHERE google_id IS NOT NULL;

