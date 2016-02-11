DROP VIEW IF EXISTS vw_vaccine_bundles;

CREATE OR REPLACE VIEW vw_vaccine_bundles AS 
 SELECT s.programid,
    s.periodid,
    s.period_name,
    s.facilityid,
    s.productid,
    COALESCE(s.quantityreceived, 0) AS sup_received,
    COALESCE(s.closingbalance, 0) AS sup_closing,
    v.received AS vac_received,
    v.closing AS vac_closing,
        CASE
            WHEN v.received > 0 THEN COALESCE(s.quantityreceived, 0) / (v.received / v.multiplier)
            ELSE 0::bigint
        END AS bund_received,
        CASE
            WHEN (v.received - v.closing) > 0 THEN COALESCE(s.quantityreceived, 0) - COALESCE(s.closingbalance, 0) / (v.received / v.multiplier - v.closing / v.multiplier)
            ELSE 0::bigint
        END AS bund_issued
   FROM ( SELECT vr.programid,
            vr.periodid,
            vr.facilityid,
            vb.productid,
            vb.multiplier,
            sum(COALESCE(vli.quantityreceived, 0)) AS received,
            sum(COALESCE(vli.closingbalance, 0)) AS closing
           FROM vaccine_reports vr
             JOIN vaccine_report_logistics_line_items vli ON vli.reportid = vr.id
             JOIN facilities f_1 ON f_1.id = vr.facilityid
             JOIN vaccine_bundle_products vbp ON vbp.productid = vli.productid
             JOIN vaccine_bundles vb ON vbp.bundleid = vb.id AND vb.programid = vr.programid
          GROUP BY vr.programid, vr.periodid, vr.facilityid, vb.productid, vb.multiplier) v
     JOIN ( SELECT r.programid,
            r.facilityid,
            r.periodid,
            pp.name AS period_name,
            li.productid,
            li.quantityreceived,
            li.closingbalance
           FROM vaccine_reports r
             JOIN vaccine_report_logistics_line_items li ON li.reportid = r.id
             JOIN processing_periods pp ON r.periodid = pp.id) s ON v.productid = s.productid AND v.programid = s.programid AND v.facilityid = s.facilityid
     JOIN facilities f ON f.id = s.facilityid;

ALTER TABLE vw_vaccine_bundles
  OWNER TO postgres;