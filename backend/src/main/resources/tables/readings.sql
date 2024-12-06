CREATE TABLE IF NOT EXISTS readings (
    id CHAR(36) NOT NULL PRIMARY KEY,
    customerId CHAR(36),
    meterId VARCHAR(255) NOT NULL,
    meterCount DOUBLE NOT NULL,
    dateOfReading DATE NOT NULL,
    kindOfMeter ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT') NOT NULL,
    comment TEXT,
    substitute BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE SET NULL
);