DROP TABLE IF EXISTS msd_stock_statuses;
CREATE TABLE public.msd_stock_statuses
(
  id serial,
  ilid character varying(200),
  facilityid integer,
  productid integer NOT NULL,
  onhanddate character varying(200),
  onhandquantity integer NOT NULL DEFAULT 0,
  mos numeric(10,0) DEFAULT 0,
  createddate timestamp without time zone NOT NULL DEFAULT now(),
  createdby integer,
  CONSTRAINT msd_stock_status_pkey PRIMARY KEY (id),
  CONSTRAINT msd_stock_status_facilityid_fkey FOREIGN KEY (facilityid)
      REFERENCES public.facilities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT msd_stock_status_productid_fkey FOREIGN KEY (productid)
      REFERENCES public.products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)