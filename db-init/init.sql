CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    fio VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) CHECK (role IN ('CANDIDATE', 'HR', 'ADMIN'))
);
