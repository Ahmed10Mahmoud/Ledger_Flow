Create table users(
    id UUID  PRIMARY key gen_random_uuid(),
    email varchar(255) not null unique,
    password varchar(255) not null,
    status varchar(255) not null default 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
)
Create INDEX idx_find_users_email on users(email)

CREATE TABLE accounts (
    id              UUID PRIMARY KEY gen_random_uuid(),
    owner_id        BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    balance         NUMERIC(19, 4) NOT NULL DEFAULT 0,
    account_type    VARCHAR(50)  NOT NULL DEFAULT 'USER',
    currency        VARCHAR(10)  NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_account_user
    FOREIGN KEY(owner_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_accounts_balance_non_negative
            CHECK (balance >= 0)
);
CREATE INDEX idx_accounts_owner_id ON accounts(owner_id);

CREATE TABLE transactions (
    id              UUID PRIMARY KEY gen_random_uuid(),
    user_id UUID NOT NULL,
    reference       VARCHAR(100) NOT NULL UNIQUE,
    type            VARCHAR(50)  NOT NULL, -- DEPOSIT, WITHDRAW, TRANSFER
    amount          NUMERIC(19, 4) NOT NULL,
    currency        VARCHAR(10)  NOT NULL,
    status          VARCHAR(50)  NOT NULL, -- PENDING, SUCCESS, FAILED
    description     TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID
);

CREATE INDEX idx_transactions_reference ON transactions(reference);

CREATE TABLE ledger_entries (
    id              UUID PRIMARY KEY gen_random_uuid(),
    transaction_id  BIGINT       NOT NULL,
    account_id      BIGINT       NOT NULL,
    entry_type      VARCHAR(10)  NOT NULL, -- DEBIT / CREDIT
    amount          NUMERIC(19, 4) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT transaction_account
    FOREIGN KEY (transaction_id)
    REFERENCES transaction_id(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_ledger_account
           FOREIGN KEY (account_id)
           REFERENCES accounts(id)
           ON DELETE CASCADE,

    CONSTRAINT chk_ledger_amount_positive
    CHECK (amount>0)

    CREATE INDEX idx_ledger_transaction_id ON ledger_entries(transaction_id);
    CREATE INDEX idx_ledger_account_id ON ledger_entries(account_id);
    CREATE INDEX idx_ledger_created_at ON ledger_entries(created_at);

CREATE TABLE idempotency_keys (
    id                  UUID PRIMARY KEY gen_random_uuid(),
    request_key         VARCHAR(255) NOT NULL,
    user_id             BIGINT       NOT NULL,
    request_hash        VARCHAR(255) NOT NULL,
    response_body       TEXT,
    status              VARCHAR(50),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at          TIMESTAMP,

    CONSTRAINT fk_idempotency_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_idempotency_request_user
        UNIQUE (request_key, user_id)
);

CREATE INDEX idx_idempotency_request_key ON idempotency_keys(request_key);

