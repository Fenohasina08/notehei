CREATE TABLE IF NOT EXISTS transcript (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id    UUID NOT NULL REFERENCES student(id),
    semester_id   UUID NOT NULL REFERENCES semester(id),
    s3_url        VARCHAR(500),
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    generated_at  TIMESTAMP,

    UNIQUE (student_id, semester_id)
    );

CREATE INDEX IF NOT EXISTS idx_transcript_student
    ON transcript(student_id);