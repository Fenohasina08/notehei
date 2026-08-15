CREATE TABLE IF NOT EXISTS course_unit (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(20) NOT NULL UNIQUE,
    name        VARCHAR(150) NOT NULL,
    credits     INT NOT NULL CHECK (credits > 0),
    semester_id UUID NOT NULL REFERENCES semester(id)
    );

CREATE TABLE IF NOT EXISTS course_unit_program (
    course_unit_id  UUID NOT NULL REFERENCES course_unit(id),
    program_id      UUID NOT NULL REFERENCES program(id),

    PRIMARY KEY (course_unit_id, program_id)
    );

CREATE TABLE IF NOT EXISTS course (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference   VARCHAR(20) NOT NULL UNIQUE,
    title       VARCHAR(150) NOT NULL,
    coefficient DECIMAL(4,2) NOT NULL CHECK (coefficient > 0)
    );

CREATE TABLE IF NOT EXISTS course_unit_course (
    course_unit_id UUID NOT NULL REFERENCES course_unit(id),
    course_id      UUID NOT NULL REFERENCES course(id),
    credits        INT NOT NULL CHECK (credits > 0),

    PRIMARY KEY (course_unit_id, course_id)
    );

CREATE TABLE IF NOT EXISTS teaching_assignment (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id  UUID NOT NULL REFERENCES teacher(id),
    course_id   UUID NOT NULL REFERENCES course(id),
    group_id    UUID NOT NULL REFERENCES "group"(id),

    UNIQUE (teacher_id, course_id, group_id)
    );