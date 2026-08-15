ALTER TABLE student
    RENAME COLUMN student_id TO matricule;

ALTER TABLE teacher
    RENAME COLUMN teacher_id TO matricule;

CREATE SEQUENCE IF NOT EXISTS user_matricule_seq
    START WITH 1
    INCREMENT BY 1;