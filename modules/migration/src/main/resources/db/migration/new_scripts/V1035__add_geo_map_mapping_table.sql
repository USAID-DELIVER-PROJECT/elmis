
DROP TABLE IF EXISTS geographic_zone_map_mappings;
CREATE TABLE geographic_zone_map_mappings
(
  id SERIAL,
  geographiczoneid integer,
  mapcode character varying(50) NOT NULL,
  CONSTRAINT geographic_zone_map_mappings_pkey PRIMARY KEY (id),
  CONSTRAINT geographic_zone_map_mappings_fkey FOREIGN KEY (geographiczoneid)
      REFERENCES public.geographic_zones (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION

  )