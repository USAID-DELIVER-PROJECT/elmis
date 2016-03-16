ALTER TABLE vaccine_report_cold_chain_line_items
ADD skipped BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE vaccine_report_disease_line_items
ADD skipped BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE vaccine_report_logistics_line_items
ADD skipped BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE vaccine_report_vitamin_supplementation_line_items
ADD skipped BOOLEAN NOT NULL DEFAULT FALSE;