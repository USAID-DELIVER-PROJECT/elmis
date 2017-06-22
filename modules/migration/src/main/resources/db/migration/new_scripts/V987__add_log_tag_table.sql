DROP TABLE IF EXISTS log_tags;

CREATE TABLE log_tags
(
  id serial not null,
  logdate character varying(100),
  logtime character varying(100),
  temperature character varying(100),
    events character varying(100)

)
WITH (
  OIDS=FALSE
);
ALTER TABLE log_tags
  OWNER TO postgres;