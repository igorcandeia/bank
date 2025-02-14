CREATE DATABASE bank;

\c bank;

CREATE TABLE IF NOT EXISTS account
(
    id         SERIAL PRIMARY KEY,
    account_id VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS transaction
(
    id         SERIAL PRIMARY KEY,
    amount     BIGINT      NOT NULL CHECK (amount >= 0),
    merchant   VARCHAR(50) NOT NULL,
    mcc        VARCHAR(4)  NOT NULL,
    account_id VARCHAR(50) NOT NULL REFERENCES account (account_id)
);

CREATE TABLE IF NOT EXISTS balance
(
    id               SERIAL PRIMARY KEY,
    balance_id       VARCHAR(50) NOT NULL,
    available_amount BIGINT      NOT NULL CHECK (available_amount >= 0),
    account_id       VARCHAR(50) REFERENCES account (account_id),
    UNIQUE (balance_id, account_id)
);

CREATE TABLE IF NOT EXISTS merchant
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    mcc  VARCHAR(4)   NOT NULL
);

INSERT INTO merchant (name, mcc)
VALUES ('UBER EATS', '5811');

INSERT INTO merchant (name, mcc)
VALUES ('PADARIA DO ZE', '5411');
