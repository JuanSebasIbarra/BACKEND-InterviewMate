-- Link study sessions to interview templates so each study session can generate AI questions from template context.
ALTER TABLE study_session
    ADD COLUMN IF NOT EXISTS template_id UUID REFERENCES interview_template(id);

CREATE INDEX IF NOT EXISTS idx_study_session_template_id ON study_session(template_id);

