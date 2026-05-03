Alter Table accounts
add constraint chk_account_type
check (account_type IN ('USER', 'SYSTEM', 'SAVINGS', 'CURRENT'));

INSERT INTO users (email, password, status)
VALUES ('system@internal.local', 'NO_LOGIN', 'SYSTEM');

INSERT INTO accounts (owner_id, balance, currency, status)
SELECT id, 0, 'USD', 'ACTIVE'
FROM users
WHERE email = 'system@internal.local';