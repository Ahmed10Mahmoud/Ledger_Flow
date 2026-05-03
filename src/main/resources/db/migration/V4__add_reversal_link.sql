ALTER TABLE transactions
ADD COLUMN reversal_of_id UUID NULL;

ALTER TABLE transactions
ADD CONSTRAINT fk_tx_reversal_of
FOREIGN KEY (reversal_of_id)
REFERENCES transactions(id);

CREATE INDEX idx_tx_reversal_of
ON transactions(reversal_of_id);