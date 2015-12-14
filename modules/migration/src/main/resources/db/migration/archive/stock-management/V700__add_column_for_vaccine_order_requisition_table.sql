ALTER TABLE vaccine_order_requisitions drop column if exists reason;

ALTER TABLE vaccine_order_requisitions ADD COLUMN reason character varying(250);