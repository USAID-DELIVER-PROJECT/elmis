DROP VIEW IF EXISTS vw_rnr_feedback;
DROP INDEX IF EXISTS i_previous_rnr_id;
DROP MATERIALIZED VIEW IF EXISTS vw_previous_rnr;

CREATE MATERIALIZED VIEW vw_previous_rnr
  AS
  select r.id, (select id from
                      requisitions p
                          where r.periodid > p.periodid
                                and p.facilityid = r.facilityid
                                and p.programid = r.programid
                                and p.emergency = false
                          order by p.periodid desc limit 1) as previousRnrId
  from requisitions r;

CREATE INDEX i_previous_rnr_id
  ON vw_previous_rnr (id);
-- end of vw_previous_rnr

