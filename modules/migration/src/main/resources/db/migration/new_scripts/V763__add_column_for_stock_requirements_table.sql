ALTER TABLE stock_requirements drop column if exists currentPrice;

ALTER TABLE stock_requirements ADD COLUMN currentPrice numeric(20,2) DEFAULT 0;