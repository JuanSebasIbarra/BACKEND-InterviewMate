-- Ensure interview_session status constraint allows ABANDONED.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'interview_session_status_check'
    ) THEN
        ALTER TABLE interview_session DROP CONSTRAINT interview_session_status_check;
    END IF;

    ALTER TABLE interview_session
        ADD CONSTRAINT interview_session_status_check
            CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'ABANDONED'));
END $$;

