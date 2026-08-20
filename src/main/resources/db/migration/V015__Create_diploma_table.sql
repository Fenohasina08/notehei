CREATE TABLE IF NOT EXISTS "diploma" (
                                         id UUID PRIMARY KEY,
                                         status VARCHAR NOT NULL,
                                         issue_date DATE,
                                         student_id UUID NOT NULL REFERENCES "student"(id)
    );