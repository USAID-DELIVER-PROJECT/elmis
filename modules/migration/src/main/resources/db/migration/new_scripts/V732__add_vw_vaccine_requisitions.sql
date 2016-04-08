-- View: vw_vaccine_requisitions

DROP VIEW IF EXISTS vw_vaccine_requisitions;

CREATE OR REPLACE VIEW vw_vaccine_requisitions AS 
 SELECT vd.parent,
    vd.zone_id AS zoneid,
    vd.zone_name,
    vd.region_id AS regionid,
    vd.region_name,
    vd.district_id AS districtid,
    vd.district_name,
    vr.id AS orderid,
    vr.programid,
    pp.startdate,
    pp.enddate,
    pp.name AS period_name,
    pp.scheduleid,
    vr.orderdate::date AS orderdate,
    vs.createddate::date AS statusdate,
    vr.emergency,
    vr.isverified,
    vs.status,
    vr.status AS current_status
   FROM vaccine_order_requisitions vr
     JOIN facilities f ON vr.facilityid = f.id
     JOIN vw_districts vd ON vd.district_id = f.geographiczoneid
     JOIN vaccine_order_requisition_status_changes vs ON vr.id = vs.orderid
     JOIN processing_periods pp ON vr.periodid = pp.id
  WHERE vs.status::text = ANY (ARRAY['SUBMITTED'::character varying, 'ISSUED'::character varying]::text[]);

