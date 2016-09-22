
CREATE TABLE daily_stock_status(
  id SERIAL PRIMARY KEY,
  facilityId INT NOT NULL REFERENCES public.facilities(id),
  programId INT NOT NULL REFERENCES public.programs(id),
  date DATE NOT NULL,
  source VARCHAR(50) NOT NULL DEFAULT ('ELMIS_FE'),
  createdDate TIMESTAMP NOT NULL DEFAULT NOW(),
  createdBy INT
);


CREATE TABLE daily_stock_status_line_items(
  id SERIAL PRIMARY KEY,
  stockStatusSubmissionId INT REFERENCES daily_stock_status(id) ON DELETE CASCADE,
  productId INT NOT NULL REFERENCES public.products(id),
  stockOnHand INTEGER NOT NULL DEFAULT (0),
  lastTransactionDate DATE NULL,
  createdDate TIMESTAMP NOT NULL DEFAULT NOW(),
  createdBy INT
);
