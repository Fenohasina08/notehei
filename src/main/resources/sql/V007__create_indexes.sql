CREATE INDEX IF NOT EXISTS idx_semester_cohort
    ON semester(cohort_id);

CREATE INDEX IF NOT EXISTS idx_group_cohort
    ON "group"(cohort_id);

CREATE INDEX IF NOT EXISTS idx_course_unit_semester
    ON course_unit(semester_id);

CREATE INDEX IF NOT EXISTS idx_exam_course
    ON exam(course_id);

CREATE INDEX IF NOT EXISTS idx_grade_student
    ON grade(student_id);

CREATE INDEX IF NOT EXISTS idx_grade_exam
    ON grade(exam_id);

CREATE INDEX IF NOT EXISTS idx_teaching_assignment_teacher
    ON teaching_assignment(teacher_id);