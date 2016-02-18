DROP VIEW IF EXISTS vw_vaccine_distribution_voucher_no_fields;
CREATE OR REPLACE VIEW vw_vaccine_distribution_voucher_no_fields AS
SELECT f.id AS facilityid,
    f.name AS failityname,
    CASE
        WHEN p.name IS NULL then z.name
        ELSE p.name
    END AS "national",
    CASE
         WHEN ft.code::text = 'cvs'::text THEN 'cvs-region-code'::character varying
         ELSE r.name
    END AS region,
    CASE
          WHEN ft.code::text = 'cvs'::text THEN 'cvs-district-code'::character varying
          WHEN ft.code::text = 'rvs'::text THEN 'rvs-district-code'::character varying
          ELSE d.name
    END AS district
   FROM facilities f
     JOIN facility_types ft ON ft.id = f.typeid
     JOIN geographic_zones d ON d.id = f.geographiczoneid
     JOIN geographic_zones r ON d.parentid = r.id
     LEFT JOIN geographic_zones z ON z.id = r.parentid
     LEFT JOIN geographic_zones p ON p.id = z.parentid;
ALTER TABLE vw_vaccine_distribution_voucher_no_fields
  OWNER TO postgres;