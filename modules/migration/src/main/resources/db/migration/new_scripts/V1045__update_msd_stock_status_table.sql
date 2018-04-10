DROP TABLE IF EXISTS public.msd_stock_statuses;

CREATE TABLE public.msd_stock_statuses
(
  id serial,
  ilid character varying(200),
  facilityCode character varying(200),
  productCode character varying(200),
  onhanddate character varying(200),
  onhandquantity integer NOT NULL DEFAULT 0,
  mos numeric(10,0) DEFAULT 0,
  createddate timestamp without time zone NOT NULL DEFAULT now(),
  createdby integer,
  CONSTRAINT msd_stock_status_pkey PRIMARY KEY (id)
)