-- liquibase formatted sql

-- changeset Alex:1769361040102-1
CREATE SEQUENCE IF NOT EXISTS driver_seq START WITH 1 INCREMENT BY 50;

-- changeset Alex:1769361040102-2
CREATE SEQUENCE IF NOT EXISTS employee_seq START WITH 1 INCREMENT BY 50;

-- changeset Alex:1769361040102-3
CREATE TABLE driver
(
    id                              BIGINT NOT NULL,
    medical_certificate_expiry_date date,
    tachograph_card_number          VARCHAR(20),
    CONSTRAINT pk_driver PRIMARY KEY (id)
);

-- changeset Alex:1769361040102-4
CREATE TABLE driver_licenses
(
    driver_id    BIGINT      NOT NULL,
    license_type VARCHAR(20) NOT NULL,
    issue_date   date        NOT NULL,
    expiry_date  date        NOT NULL
);

-- changeset Alex:1769361040102-5
CREATE TABLE employee
(
    id            BIGINT       NOT NULL,
    employee_code VARCHAR(255) NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(255),
    CONSTRAINT pk_employee PRIMARY KEY (id)
);

-- changeset Alex:1769361040102-6
ALTER TABLE driver
    ADD CONSTRAINT uc_driver_tachograph_card_number UNIQUE (tachograph_card_number);

-- changeset Alex:1769361040102-7
ALTER TABLE employee
    ADD CONSTRAINT uc_employee_email UNIQUE (email);

-- changeset Alex:1769361040102-8
ALTER TABLE employee
    ADD CONSTRAINT uc_employee_employee_code UNIQUE (employee_code);

-- changeset Alex:1769361040102-9
ALTER TABLE driver
    ADD CONSTRAINT FK_DRIVER_ON_ID FOREIGN KEY (id) REFERENCES employee (id);

-- changeset Alex:1769361040102-10
ALTER TABLE driver_licenses
    ADD CONSTRAINT fk_driver_licenses_on_driver FOREIGN KEY (driver_id) REFERENCES driver (id);
