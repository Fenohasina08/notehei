CREATE TABLE IF NOT EXISTS exam (
                                    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id    UUID NOT NULL REFERENCES course(id),
    type         exam_type NOT NULL,
    exam_date    TIMESTAMP,
    weighting    DECIMAL(5,2) NOT NULL CHECK (weighting > 0)
    );

CREATE TABLE IF NOT EXISTS grade (
                                     id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID NOT NULL REFERENCES student(id),
    exam_id     UUID NOT NULL REFERENCES exam(id),

    value       DECIMAL(4,2) NOT NULL
    CHECK (value BETWEEN 0 AND 20),

    status      VARCHAR(30),
    entered_at  TIMESTAMP NOT NULL DEFAULT now(),

    teacher_id  UUID REFERENCES teacher(id),
    admin_id    UUID REFERENCES admin(id),

    UNIQUE (student_id, exam_id),

    CHECK (
(teacher_id IS NOT NULL AND admin_id IS NULL)
    OR
(admin_id IS NOT NULL AND teacher_id IS NULL)
    )
    );

CREATE TABLE IF NOT EXISTS grade_history (
                                             id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    grade_id         UUID NOT NULL REFERENCES grade(id),
    old_value        DECIMAL(4,2) NOT NULL,
    new_value        DECIMAL(4,2) NOT NULL,
    reason           TEXT,
    modified_at      TIMESTAMP NOT NULL DEFAULT now(),

    teacher_id       UUID REFERENCES teacher(id),
    admin_id         UUID REFERENCES admin(id),

    CHECK (
(teacher_id IS NOT NULL AND admin_id IS NULL)
    OR
(admin_id IS NOT NULL AND teacher_id IS NULL)
    )
    );