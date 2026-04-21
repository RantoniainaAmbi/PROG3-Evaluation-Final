CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');


CREATE TYPE member_occupation AS ENUM (
    'JUNIOR',
    'SENIOR',
    'SECRETARY',
    'TREASURER',
    'VICE_PRESIDENT',
    'PRESIDENT'
    );

CREATE TABLE member (
                        id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        first_name  VARCHAR(100),
                        last_name   VARCHAR(100),
                        birth_date  DATE,
                        gender      gender,
                        address     VARCHAR(255),
                        profession  VARCHAR(100),
                        phone_number BIGINT,
                        email       VARCHAR(150),
                        occupation  member_occupation
);

CREATE TABLE collectivity (
                              id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              location            VARCHAR(255),
                              federation_approval BOOLEAN,
                              president_id        UUID REFERENCES member(id),
                              vice_president_id   UUID REFERENCES member(id),
                              treasurer_id        UUID REFERENCES member(id),
                              secretary_id        UUID REFERENCES member(id)
);

CREATE TABLE collectivity_member (
                                     collectivity_id UUID REFERENCES collectivity(id),
                                     member_id       UUID REFERENCES member(id),
                                     PRIMARY KEY (collectivity_id, member_id)
);

ALTER TABLE member
    ADD COLUMN collectivity_id UUID REFERENCES collectivity(id);

CREATE TABLE member_referee (
                                member_id  UUID REFERENCES member(id),
                                referee_id UUID REFERENCES member(id),
                                PRIMARY KEY (member_id, referee_id)
);