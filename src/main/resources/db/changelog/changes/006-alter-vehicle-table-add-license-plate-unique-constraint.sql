-- liquibase formatted sql

-- changeset Alex:1769368453199-1
ALTER TABLE vehicle
    ADD CONSTRAINT uc_vehicle_license_plate UNIQUE (license_plate);

