-- liquibase formatted sql

-- changeset Alex:1769352409366-1
CREATE SEQUENCE IF NOT EXISTS car_seq START WITH 1 INCREMENT BY 50;

-- changeset Alex:1769352409366-2
CREATE TABLE car
(
    id                       BIGINT      NOT NULL,
    number_of_seats          INTEGER     NOT NULL,
    number_of_doors          INTEGER     NOT NULL,
    is_child_seat_compatible BOOLEAN     NOT NULL,
    has_sunroof              BOOLEAN     NOT NULL,
    car_body_type            VARCHAR(30) NOT NULL,
    CONSTRAINT pk_car PRIMARY KEY (id)
);

-- changeset Alex:1769352409366-3
ALTER TABLE car
    ADD CONSTRAINT FK_CAR_ON_ID FOREIGN KEY (id) REFERENCES vehicle (id);
