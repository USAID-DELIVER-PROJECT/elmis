DROP VIEW public.vw_cold_chain_equipment;
CREATE OR REPLACE VIEW public.vw_cold_chain_equipment AS
 SELECT nested.id AS equipmentid,
    nested.manufacturer,
    nested.model,
    eet.name AS energytypename,
    ecce.ccecode AS equipmentcoldchainequipmentscode,
    ei.serialnumber AS equipmentcoldchainequipmentsserial,
    ecce.refrigerant,
    ecce.refrigeratorcapacity,
    ecce.freezercapacity,
    eos1.functional_status,
    eos2.non_functional_status,
    ei.yearofinstallation,
    date_part('year'::text, 'now'::text::date) - ei.yearofinstallation::double precision AS equipmentage,
    ei.yearofinstallation + 11 AS yearofreplacement,
    facilities.id AS facilityid,
    facilities.name AS facilityname,
    ft.name AS facilitytypename,
    ft.code AS facilitytypecode,
    facilities.address1 AS facilityaddress1,
    facilities.address2 AS facilityaddress2,
    facilities.haselectricity AS facilityhaselectricity,
    fo.text AS facilityoperator,
    gz.id AS geozoneid,
    gz.name AS geozonename,
    geo_zone_tree.hierarchy AS geozonehierarchy
   FROM equipment_inventories ei
     JOIN ( SELECT DISTINCT e.id,
            e.name,
            e.createdby,
            e.createddate,
            e.modifiedby,
            e.modifieddate,
            e.equipmenttypeid,
            e.manufacturer,
            e.model,
            e.energytypeid
           FROM equipments e
             JOIN equipment_types et ON e.equipmenttypeid = et.id
          WHERE et.iscoldchain = true) nested ON nested.id = ei.equipmentid
     LEFT JOIN equipment_cold_chain_equipments ecce ON nested.id = ecce.equipmentid
     LEFT JOIN equipment_energy_types eet ON nested.energytypeid = eet.id
     LEFT JOIN facilities ON ei.facilityid = facilities.id
     LEFT JOIN facility_types ft ON facilities.typeid = ft.id
     LEFT JOIN equipment_inventory_statuses eis ON ei.id = eis.inventoryid AND eis.createddate = (( SELECT max(equipment_inventory_statuses.createddate) AS max
           FROM equipment_inventory_statuses
          WHERE equipment_inventory_statuses.inventoryid = ei.id))
     LEFT JOIN ( SELECT equipment_operational_status.id,
            equipment_operational_status.name AS functional_status
           FROM equipment_operational_status) eos1 ON eos1.id = eis.statusid
     LEFT JOIN ( SELECT equipment_operational_status.id,
            equipment_operational_status.name AS non_functional_status
           FROM equipment_operational_status) eos2 ON eos2.id = eis.notfunctionalstatusid
     LEFT JOIN facility_operators fo ON facilities.operatedbyid = fo.id
     LEFT JOIN geographic_zones gz ON facilities.geographiczoneid = gz.id
     LEFT JOIN ( SELECT fn_get_geozonetree_names.hierarchy,
            fn_get_geozonetree_names.leafid
           FROM fn_get_geozonetree_names() fn_get_geozonetree_names(hierarchy, leafid)) geo_zone_tree ON gz.id = geo_zone_tree.leafid;
