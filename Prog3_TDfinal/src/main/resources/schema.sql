CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');

CREATE TYPE member_occupation AS ENUM (
    'JUNIOR',
    'SENIOR',
    'SECRETARY',
    'TREASURER',
    'VICE_PRESIDENT',
    'PRESIDENT'
);

CREATE TABLE collectivity (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location            VARCHAR(255),
    specialty           VARCHAR(100),
    creation_date       DATE DEFAULT CURRENT_DATE,
    name                VARCHAR(150) UNIQUE,
    number              VARCHAR(50) UNIQUE,
    federation_approval BOOLEAN DEFAULT false,
    president_id        UUID,
    vice_president_id   UUID,
    treasurer_id        UUID,
    secretary_id        UUID
);

CREATE TABLE member (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name            VARCHAR(100),
    last_name             VARCHAR(100),
    birth_date            DATE,
    gender                gender,
    address               VARCHAR(255),
    profession            VARCHAR(100),
    phone_number          BIGINT,
    email                 VARCHAR(150),
    occupation            member_occupation,
    registration_date     DATE DEFAULT CURRENT_DATE,
    registration_fee_paid BOOLEAN DEFAULT false,
    membership_dues_paid  BOOLEAN DEFAULT false,
    collectivity_id       UUID REFERENCES collectivity(id)
);

ALTER TABLE collectivity
    ADD CONSTRAINT fk_collectivity_president FOREIGN KEY (president_id) REFERENCES member(id),
    ADD CONSTRAINT fk_collectivity_vice_president FOREIGN KEY (vice_president_id) REFERENCES member(id),
    ADD CONSTRAINT fk_collectivity_treasurer FOREIGN KEY (treasurer_id) REFERENCES member(id),
    ADD CONSTRAINT fk_collectivity_secretary FOREIGN KEY (secretary_id) REFERENCES member(id);

CREATE TABLE collectivity_member (
    collectivity_id UUID NOT NULL REFERENCES collectivity(id),
    member_id       UUID NOT NULL REFERENCES member(id),
    PRIMARY KEY (collectivity_id, member_id)
);

CREATE TABLE member_referee (
    member_id  UUID NOT NULL REFERENCES member(id),
    referee_id UUID NOT NULL REFERENCES member(id),
    PRIMARY KEY (member_id, referee_id)
);

CREATE TABLE sponsorship (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id  UUID NOT NULL REFERENCES member(id),
    sponsor_id    UUID NOT NULL REFERENCES member(id),
    relation      VARCHAR(100),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);