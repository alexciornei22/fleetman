-- liquibase formatted sql

-- changeset Alex:1769624100963-1
CREATE SEQUENCE IF NOT EXISTS fleet_manager_seq START WITH 1 INCREMENT BY 50;

-- changeset Alex:1769624100963-2
CREATE TABLE fleet_manager
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_fleet_manager PRIMARY KEY (id)
);

-- changeset Alex:1769624100963-3
ALTER TABLE vehicle
    ADD fleet_manager_id BIGINT;

-- changeset Alex:1769624100963-4
ALTER TABLE vehicle
    ALTER COLUMN fleet_manager_id SET NOT NULL;

-- changeset Alex:1769624100963-5
ALTER TABLE fleet_manager
    ADD CONSTRAINT FK_FLEET_MANAGER_ON_ID FOREIGN KEY (id) REFERENCES employee (id);

-- changeset Alex:1769624100963-6
ALTER TABLE vehicle
    ADD CONSTRAINT FK_VEHICLE_ON_FLEETMANAGER FOREIGN KEY (fleet_manager_id) REFERENCES fleet_manager (id);

