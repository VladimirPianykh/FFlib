CREATE TABLE users(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(100),
    fail2ban_tries INT DEFAULT 0,
    tries INT DEFAULT 0,
    lock_time TIMESTAMP
);