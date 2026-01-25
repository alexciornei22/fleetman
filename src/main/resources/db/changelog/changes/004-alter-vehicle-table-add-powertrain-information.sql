-- liquibase formatted sql

-- changeset Alex:1769355558564-1
ALTER TABLE vehicle
    ADD engine_displacement_cc INTEGER;
ALTER TABLE vehicle
    ADD engine_type VARCHAR(30);
ALTER TABLE vehicle
    ADD fuel_capacity_liters INTEGER;
ALTER TABLE vehicle
    ADD has_automatic_transmission BOOLEAN;
ALTER TABLE vehicle
    ADD horsepower INTEGER;

-- changeset Alex:1769355558564-2
ALTER TABLE vehicle
    ALTER COLUMN engine_displacement_cc SET NOT NULL;

-- changeset Alex:1769355558564-4
ALTER TABLE vehicle
    ALTER COLUMN engine_type SET NOT NULL;

-- changeset Alex:1769355558564-6
ALTER TABLE vehicle
    ALTER COLUMN fuel_capacity_liters SET NOT NULL;

-- changeset Alex:1769355558564-8
ALTER TABLE vehicle
    ALTER COLUMN has_automatic_transmission SET NOT NULL;

-- changeset Alex:1769355558564-10
ALTER TABLE vehicle
    ALTER COLUMN horsepower SET NOT NULL;

