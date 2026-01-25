-- liquibase formatted sql

-- changeset Alex:1769348940886-1
CREATE SEQUENCE IF NOT EXISTS vehicle_seq START WITH 1 INCREMENT BY 50;

-- changeset Alex:1769348940886-2
CREATE TABLE vehicle
(
    id            BIGINT       NOT NULL,
    vin           VARCHAR(255) NOT NULL,
    license_plate VARCHAR(255),
    mileage       INTEGER,
    CONSTRAINT pk_vehicle PRIMARY KEY (id)
);

-- changeset Alex:1769348940886-3
ALTER TABLE vehicle
    ADD CONSTRAINT uc_vehicle_vin UNIQUE (vin);
