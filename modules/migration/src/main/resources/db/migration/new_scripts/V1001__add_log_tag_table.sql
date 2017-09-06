DROP TABLE IF EXISTS log_tags;

CREATE TABLE log_tags
(
  id serial NOT NULL,
  logdate character varying(200),
  logtime character varying(100),
  temperature character varying(100),
  facilityId Integer null,
  route character varying(100),
  createddate timestamp without time zone,
  CONSTRAINT log_tags_pkey PRIMARY KEY (id)
)