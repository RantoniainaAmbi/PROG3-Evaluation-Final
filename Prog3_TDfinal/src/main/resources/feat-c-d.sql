CREATE TYPE payment_mode AS ENUM ('CASH', 'MOBILE_BANKING', 'BANK_TRANSFER');
CREATE TYPE frequency AS ENUM ('WEEKLY', 'MONTHLY', 'ANNUALLY', 'PUNCTUALLY');
CREATE TYPE activity_status AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE mobile_service AS ENUM ('AIRTEL_MONEY', 'MVOLA', 'ORANGE_MONEY');
CREATE TYPE bank_name AS ENUM ('BRED', 'MCB', 'BMOI', 'BOA', 'BGFI', 'AFG', 'ACCES_BAQUE', 'BAOBAB', 'SIPEM');

CREATE TABLE membership_fee (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                label VARCHAR(255) NOT NULL,
                                amount DECIMAL(15, 2) CHECK (amount >= 0),
                                eligible_from DATE NOT NULL,
                                frequency frequency NOT NULL,
                                status activity_status DEFAULT 'ACTIVE',
                                collectivity_id UUID REFERENCES collectivity(id) ON DELETE CASCADE
);

CREATE TABLE financial_account (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   amount DECIMAL(15, 2) DEFAULT 0.00,
                                   collectivity_id UUID NOT NULL REFERENCES collectivity(id) ON DELETE CASCADE
);

CREATE TABLE cash_account (
                              id UUID PRIMARY KEY REFERENCES financial_account(id) ON DELETE CASCADE
);

CREATE TABLE bank_account (
                              id UUID PRIMARY KEY REFERENCES financial_account(id) ON DELETE CASCADE,
                              holder_name VARCHAR(255) NOT NULL,
                              bank_name bank_name NOT NULL,
                              bank_code VARCHAR(5) NOT NULL,
                              branch_code VARCHAR(5) NOT NULL,
                              account_number VARCHAR(11) NOT NULL,
                              account_key VARCHAR(2) NOT NULL,
                              CONSTRAINT valid_rib CHECK (
                                  length(bank_code) = 5 AND
                                  length(branch_code) = 5 AND
                                  length(account_number) = 11 AND
                                  length(account_key) = 2
                                  )
);

CREATE TABLE mobile_banking_account (
                                        id UUID PRIMARY KEY REFERENCES financial_account(id) ON DELETE CASCADE,
                                        holder_name VARCHAR(255) NOT NULL,
                                        mobile_service mobile_service NOT NULL,
                                        mobile_number VARCHAR(15) NOT NULL UNIQUE
);

CREATE TABLE collectivity_transaction (
                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          amount DECIMAL(15, 2) NOT NULL,
                                          payment_mode payment_mode NOT NULL,
                                          account_credited_id UUID NOT NULL REFERENCES financial_account(id),
                                          member_debited_id UUID NOT NULL REFERENCES member(id),
                                          collectivity_id UUID NOT NULL REFERENCES collectivity(id)
);