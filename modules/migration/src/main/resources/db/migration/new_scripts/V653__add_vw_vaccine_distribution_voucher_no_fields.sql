DO $$
BEGIN
DROP VIEW IF EXISTS vw_vaccine_distribution_voucher_no_fields;
CREATE OR REPLACE VIEW vw_vaccine_distribution_voucher_no_fields AS
SELECT
    f.id AS facilityid,
    f.name AS failityname,
    p.name AS national,
    CASE WHEN ft.code='cvs' THEN 'cvs-region-code'
	 ELSE r.name
	 END AS region,
   CASE WHEN ft.code='cvs' THEN 'cvs-district-code' WHEN ft.code='rvs' THEN 'rvs-district-code'
	 ELSE d.name
   END AS district
   FROM facilities f
     JOIN facility_types ft ON ft.id=f.typeid
     JOIN geographic_zones d ON d.id=f.geographiczoneid
     JOIN geographic_zones r ON d.parentid = r.id
     JOIN geographic_zones z ON z.id = r.parentid
     JOIN geographic_zones p ON p.id =z.parentid;

ALTER TABLE vw_vaccine_distribution_voucher_no_fields
  OWNER TO postgres;
END;
$$