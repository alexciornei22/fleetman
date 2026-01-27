-- liquibase formatted sql

-- changeset Alex:1769537314489-1
CREATE SEQUENCE IF NOT EXISTS trip_seq START WITH 1 INCREMENT BY 50;

-- changeset Alex:1769537314489-2
CREATE TABLE trip
(
    id             BIGINT                      NOT NULL,
    vehicle_id     BIGINT                      NOT NULL,
    driver_id      BIGINT                      NOT NULL,
    start_location VARCHAR(255)                NOT NULL,
    end_location   VARCHAR(255)                NOT NULL,
    start_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time       TIMESTAMP WITHOUT TIME ZONE,
    distance_km    DOUBLE PRECISION,
    status         VARCHAR(20)                 NOT NULL,
    notes          VARCHAR(500),
    CONSTRAINT pk_trip PRIMARY KEY (id)
);

-- changeset Alex:1769537314489-3
ALTER TABLE trip
    ADD CONSTRAINT FK_TRIP_ON_DRIVER FOREIGN KEY (driver_id) REFERENCES driver (id);

-- changeset Alex:1769537314489-4
ALTER TABLE trip
    ADD CONSTRAINT FK_TRIP_ON_VEHICLE FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);

