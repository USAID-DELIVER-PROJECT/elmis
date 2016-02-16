DROP TABLE IF EXISTS stock_requirements;
CREATE TABLE stock_requirements
(
  id serial NOT NULL,
  programid integer NOT NULL,
  facilityid integer NOT NULL,
  productid integer NOT NULL,
  annualneed bigint,
  supplyperiodneed bigint,
  isavalue bigint,
  reorderlevel bigint,
  bufferstock bigint,
  maximumstock bigint,
  createdby integer,
  modifiedby integer,
  modifieddate timestamp without time zone,
  createddate timestamp without time zone,
  year integer NOT NULL,
  productcategory character varying(100),
  wapi character varying(100),
  CONSTRAINT demand_forecast_pkey PRIMARY KEY (id),
  CONSTRAINT demand_forecast_facility FOREIGN KEY (facilityid)
      REFERENCES facilities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT demand_forecast_product_fkey FOREIGN KEY (productid)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT demand_forecast_program_fkey FOREIGN KEY (programid)
      REFERENCES programs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE stock_requirements
  OWNER TO postgres;