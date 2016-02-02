
ALTER TABLE vaccine_distributions DROP COLUMN IF EXISTS remarks RESTRICT;
ALTER TABLE vaccine_distributions ADD COLUMN remarks VARCHAR (250);
