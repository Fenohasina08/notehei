CREATE TABLE IF NOT EXISTS program (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code     VARCHAR(10) NOT NULL UNIQUE,
    name     VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS cohort (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entry_year INT NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS academic_year (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(20) NOT NULL UNIQUE,
    start_year  INT NOT NULL,
    end_year    INT NOT NULL,

    CHECK (end_year = start_year + 1)
    );

CREATE TABLE IF NOT EXISTS semester (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number           INT NOT NULL CHECK (number BETWEEN 1 AND 6),
    cohort_id        UUID NOT NULL REFERENCES cohort(id),
    academic_year_id UUID NOT NULL REFERENCES academic_year(id),

    UNIQUE (cohort_id, number)
    );