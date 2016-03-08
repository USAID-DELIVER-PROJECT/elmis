 DROP VIEW IF EXISTS vw_vaccine_sessions;

CREATE OR REPLACE VIEW vw_vaccine_sessions AS 
 SELECT pp.id AS period_id,
    pp.name AS period_name,
    pp.startdate AS period_start_date,
    pp.enddate AS period_end_date,
    gz.id AS geographic_zone_id,
    gz.name AS geographic_zone_name,
    gz.levelid AS level_id,
    gz.parentid AS parent_id,
    f.id AS facility_id,
    f.code AS facility_code,
    f.name AS facility_name,
    vr.programid AS program_id,
    vr.fixedimmunizationsessions AS fixed_sessions,
    vr.outreachimmunizationsessions AS outreach_sessions,
    vr.outreachimmunizationsessionscanceled AS outreaach_cancelled
   FROM vaccine_reports vr
     JOIN facilities f ON vr.facilityid = f.id
     JOIN geographic_zones gz ON f.geographiczoneid = gz.id
     JOIN processing_periods pp ON vr.periodid = pp.id;

ALTER TABLE vw_vaccine_sessions
  OWNER TO postgres;
