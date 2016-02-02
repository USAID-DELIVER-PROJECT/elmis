DROP TABLE IF EXISTS vaccine_bundle_products;

CREATE TABLE vaccine_bundle_products
(
  id serial NOT NULL,
  bundleid integer,
  productid integer NOT NULL,
  multiplier integer,
  createdby integer,
  createddate timestamp(6) without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp(6) without time zone DEFAULT now(),
  CONSTRAINT vaccine_bundle_products_pkey PRIMARY KEY (id),
  CONSTRAINT vaccine_bundle_products_bundleid_fkey FOREIGN KEY (bundleid)
      REFERENCES vaccine_bundles (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_bundle_products
  OWNER TO postgres;
