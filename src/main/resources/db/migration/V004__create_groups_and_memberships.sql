CREATE TABLE IF NOT EXISTS "group" (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference   VARCHAR(20) NOT NULL,
    cohort_id   UUID NOT NULL REFERENCES cohort(id),

    UNIQUE (reference, cohort_id)
    );

CREATE TABLE IF NOT EXISTS group_program_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id    UUID NOT NULL REFERENCES "group"(id),
    program_id  UUID NOT NULL REFERENCES program(id),
    start_date  DATE NOT NULL,
    end_date    DATE,

    CHECK (
              end_date IS NULL
              OR end_date > start_date
          )
    );

CREATE UNIQUE INDEX IF NOT EXISTS ux_group_program_active
    ON group_program_history (group_id)
    WHERE end_date IS NULL;

CREATE TABLE IF NOT EXISTS group_membership (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID NOT NULL REFERENCES student(id),
    group_id    UUID NOT NULL REFERENCES "group"(id),
    start_date  DATE NOT NULL,
    end_date    DATE,

    CHECK (
              end_date IS NULL
              OR end_date > start_date
          )
    );

CREATE UNIQUE INDEX IF NOT EXISTS ux_group_membership_active
    ON group_membership (student_id)
    WHERE end_date IS NULL;