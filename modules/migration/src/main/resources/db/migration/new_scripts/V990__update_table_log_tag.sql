DROP TABLE IF EXISTS log_tags;
CREATE TABLE log_tags
(
  id serial NOT NULL,
  logDate character varying(200),
  logTime character varying(100),
  temperature character varying(100),
  serialnumber character varying(100),
  createddate timestamp without time zone,
  CONSTRAINT log_tags_pkey PRIMARY KEY (id)
);