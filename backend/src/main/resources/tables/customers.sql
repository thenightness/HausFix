CREATE TABLE IF NOT EXISTS customers (
    id CHAR(36) PRIMARY KEY,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    birthDate DATE NOT NULL,
    gender ENUM('M', 'W', 'D', 'U') NOT NULL
);
