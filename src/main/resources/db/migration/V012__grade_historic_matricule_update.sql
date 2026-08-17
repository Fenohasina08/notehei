 ALTER TABLE grade_history
    ADD COLUMN IF NOT EXISTS teacher_matricule VARCHAR(20);


 DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'grade_history_teacher_id_fkey'
    ) THEN
ALTER TABLE grade_history
DROP CONSTRAINT grade_history_teacher_id_fkey;
END IF;
END
$$;


 ALTER TABLE grade_history
DROP COLUMN IF EXISTS teacher_id;


 DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'grade_history_check'
    ) THEN
ALTER TABLE grade_history
DROP CONSTRAINT grade_history_check;
END IF;
END
$$;


 DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_grade_history_teacher_matricule'
    ) THEN
ALTER TABLE grade_history
    ADD CONSTRAINT fk_grade_history_teacher_matricule
        FOREIGN KEY (teacher_matricule)
            REFERENCES teacher(matricule);
END IF;
END
$$;


 DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'grade_history_check'
    ) THEN
ALTER TABLE grade_history
    ADD CONSTRAINT grade_history_check
        CHECK (
            (teacher_matricule IS NOT NULL AND admin_id IS NULL)
                OR
            (admin_id IS NOT NULL AND teacher_matricule IS NULL)
            );
END IF;
END
$$;