INSERT INTO admin (id, firstname, lastname, email, password, birth_date, address, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           'Super',
           'Admin',
           'superadmin@notehei.local',
           '$2b$10$/jJn2aXhkkEpNVm6stXzYuGZ2sY41haEjw4zzUsuzlN6vapZjoPaW',
           '2000-01-01',
           'HQ',
           now(),
           now()
       )
    ON CONFLICT (email) DO NOTHING;
