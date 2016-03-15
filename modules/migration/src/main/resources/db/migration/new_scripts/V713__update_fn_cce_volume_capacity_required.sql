DROP function IF EXISTS fn_cce_volume_capacity_required(integer,character varying (100));
CREATE OR REPLACE FUNCTION fn_cce_volume_capacity_required(facility_id integer,facility_level character varying (100))
RETURNS numeric(10,2) AS
$BODY$
DECLARE
volume_required numeric(10,2);
this_year integer;
population_category character varying (300);
vaccine_category character varying (300);
population integer;
net_volume_per_fic numeric;
net_volume_per_fic_with_diluent numeric;
vfic numeric;
max_months_of_stock numeric;
coverage numeric;

BEGIN
population=0;
this_year=EXTRACT(year from NOW());
population_category=(SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('CCE_VOLUME_CAPACITY_DEMOGRAPHIC_CATEGORY') LIMIT 1);
vaccine_category=(SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('VACCINE_REPORT_VACCINE_CATEGORY_CODE') LIMIT 1);
coverage=(SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('CCE_VOLUME_CAPACITY_COVERAGE_PERCENTAGE') LIMIT 1);

Select sum(fic), sum(ficwithdiluent) into net_volume_per_fic,net_volume_per_fic_with_diluent from(
	select p.primaryname,
	       isa.dosesperyear,
	       isa.wastagefactor,
	       COALESCE(vc.packedvolumeperdose,0) as packedvolume,
	       COALESCE(vc.diluentpackedvolumeperdose,0) as diluentpackedvolume,
	       COALESCE((COALESCE(vc.packedvolumeperdose,0)*isa.wastagefactor*isa.dosesperyear),0) as fic,
	       COALESCE(((COALESCE(vc.packedvolumeperdose,0) + COALESCE(vc.diluentpackedvolumeperdose,0))*isa.wastagefactor*isa.dosesperyear),0) as ficwithdiluent
	from program_products pp
	join isa_coefficients isa on pp.isacoefficientsid=isa.id
	join products p on p.id=pp.productid
	join product_categories pc on pc.id=pp.productcategoryid
	join vaccine_inventory_product_configurations vc on vc.productid=pp.productid
	where pc.code=vaccine_category and pp.active=true) as fic_table LIMIT 1;

max_months_of_stock=(select MAX(maxmonthsofstock) from facility_approved_products fap
	join facility_types ft on ft.id=fap.facilitytypeid
	join program_products pp on pp.id=fap.programproductid
	join product_categories pc on pc.id=pp.productcategoryid
	where LOWER(ft.code)=LOWER(facility_level) and pc.code=vaccine_category
	group by ft.code);

IF LOWER(facility_level) = 'cvs' THEN
population=(select SUM(value) from district_demographic_estimates dd
	join demographic_estimate_categories dc on dd.demographicestimateid=dc.id
	where dc.name = population_category
	and dd.year=this_year);
vfic=net_volume_per_fic;
ELSIF LOWER(facility_level) = 'rvs' THEN
population=(select SUM(value) from district_demographic_estimates dd
	join demographic_estimate_categories dc on dd.demographicestimateid=dc.id
	where dc.name = population_category and dd.districtid
	IN (select id from geographic_zones where parentid=
	(select gz.parentid from facilities f join geographic_zones gz on gz.id=f.geographiczoneid where f.id=facility_id))
	and dd.year=this_year);
vfic=net_volume_per_fic;
ELSIF LOWER(facility_level) = 'dvs' THEN
population=(select value from district_demographic_estimates dd
	join demographic_estimate_categories dc on dd.demographicestimateid=dc.id
	join facilities f on f.geographiczoneid=dd.districtid
	where dc.name = population_category and f.id=facility_id and dd.year=this_year limit 1);
vfic=net_volume_per_fic;
ELSE
population=(select value from facility_demographic_estimates fd
	join demographic_estimate_categories dc on fd.demographicestimateid=dc.id
	where dc.name = population_category and fd.facilityid=facility_id and fd.year=this_year limit 1);
vfic=net_volume_per_fic_with_diluent;
END IF;

volume_required=(((population * vfic*(coverage/100))/1000)/12)*max_months_of_stock;

RETURN volume_required;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_cce_volume_capacity_required(integer,character varying (100))
  OWNER TO postgres;
