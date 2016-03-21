ALTER TABLE vaccine_order_requisitions DROP COLUMN IF EXISTS isVerified RESTRICT;
ALTER TABLE vaccine_order_requisitions ADD COLUMN isVerified BOOLEAN NOT NULL DEFAULT false;
