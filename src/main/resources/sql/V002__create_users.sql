CREATE TABLE IF NOT EXISTS student (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firstname   VARCHAR(100) NOT NULL,
    lastname    VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    birth_date  DATE,
    address     VARCHAR(255),
    student_id  VARCHAR(20) NOT NULL UNIQUE
    CHECK (student_id ~ '^STD[0-9]{2}[0-9]{3,}$'),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS teacher (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firstname   VARCHAR(100) NOT NULL,
    lastname    VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    birth_date  DATE,
    address     VARCHAR(255),
    teacher_id  VARCHAR(20) NOT NULL UNIQUE
    CHECK (teacher_id ~ '^TCH[0-9]+$'),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS admin (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firstname   VARCHAR(100) NOT NULL,
    lastname    VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    birth_date  DATE,
    address     VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
    );