DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uq_student_matricule'
    ) THEN
ALTER TABLE student
    ADD CONSTRAINT uq_student_matricule
        UNIQUE (matricule);
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uq_teacher_matricule'
    ) THEN
ALTER TABLE teacher
    ADD CONSTRAINT uq_teacher_matricule
        UNIQUE (matricule);
END IF;
END $$;

 ALTER TABLE grade
    ADD COLUMN IF NOT EXISTS student_matricule VARCHAR(20);

ALTER TABLE grade
    ADD COLUMN IF NOT EXISTS teacher_matricule VARCHAR(20);

 ALTER TABLE grade
ALTER COLUMN student_matricule TYPE VARCHAR(20);

ALTER TABLE grade
    ALTER COLUMN student_matricule SET NOT NULL;

ALTER TABLE grade
ALTER COLUMN teacher_matricule TYPE VARCHAR(20);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_grade_student_matricule'
    ) THEN
ALTER TABLE grade
    ADD CONSTRAINT fk_grade_student_matricule
        FOREIGN KEY (student_matricule)
            REFERENCES student(matricule);
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_grade_teacher_matricule'
    ) THEN
ALTER TABLE grade
    ADD CONSTRAINT fk_grade_teacher_matricule
        FOREIGN KEY (teacher_matricule)
            REFERENCES teacher(matricule);
END IF;
END $$;