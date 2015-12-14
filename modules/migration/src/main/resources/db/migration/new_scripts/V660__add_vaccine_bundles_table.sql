DROP VIEW IF EXISTS vw_vaccine_bundles;
DROP TABLE IF EXISTS vaccine_bundle_products;

DROP TABLE IF EXISTS vaccine_bundles;

CREATE TABLE vaccine_bundles
(
  id serial NOT NULL,
  description character varying(200),
  programid integer NOT NULL,
  productid integer NOT NULL,
  multiplier integer,
  minlimit numeric(4,2),
  maxlimit numeric(4,2),
  createdby integer,
  createddate timestamp(6) without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp(6) without time zone DEFAULT now(),
  CONSTRAINT vaccine_bundles_pkey PRIMARY KEY (id),
  CONSTRAINT vaccine_bundles_productid_fkey FOREIGN KEY (productid)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT vaccine_bundles_programid_fkey FOREIGN KEY (programid)
      REFERENCES programs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_bundles
  OWNER TO postgres;
