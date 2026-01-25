-- liquibase formatted sql

-- changeset Alex:1769353055771-1
CREATE SEQUENCE IF NOT EXISTS truck_seq START WITH 1 INCREMENT BY 50;

-- changeset Alex:1769353055771-2
CREATE TABLE truck
(
    id                     BIGINT  NOT NULL,
    max_load_kg            INTEGER NOT NULL,
    number_of_axles        INTEGER NOT NULL,
    has_refrigeration_unit BOOLEAN NOT NULL,
    CONSTRAINT pk_truck PRIMARY KEY (id)
);

-- changeset Alex:1769353055771-3
ALTER TABLE truck
    ADD CONSTRAINT FK_TRUCK_ON_ID FOREIGN KEY (id) REFERENCES vehicle (id);

