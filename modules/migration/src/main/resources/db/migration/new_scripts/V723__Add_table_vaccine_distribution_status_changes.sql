DROP TABLE IF EXISTS vaccine_distribution_status_changes;

CREATE TABLE vaccine_distribution_status_changes
(
  id serial NOT NULL,
  distributionid integer NOT NULL,
  status character varying(50) NOT NULL,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT vaccine_distributions_status_changes_pkey PRIMARY KEY (id),
  CONSTRAINT vaccine_distributions_status_changes_fkey FOREIGN KEY (distributionid)
      REFERENCES vaccine_distributions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_status_changes
  OWNER TO postgres;


