ALTER TABLE vaccine_distributions DROP COLUMN IF EXISTS notified  RESTRICT;
ALTER TABLE vaccine_distributions ADD COLUMN notified BOOLEAN  DEFAULT false;