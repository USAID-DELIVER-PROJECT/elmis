-- for now use a placeholder for data_element_id for bednet product, We'll replace the placeholder when we receive the actual.
DELETE FROM interface_dhis2_products where elmis_code = '20090063BE';
INSERT INTO interface_dhis2_products (
	data_element,
	data_element_id,
	elmis_code,
	data_element_key
)
VALUES
	(
		'Mosquito Net Size 180(L) X 160 (W) X 180 (H) With Long Lasting Insecticide Treatment (LLIN)',
		'XXXXXXXXXX',
		'20090063BE',
		'CLOSING_BALANCE'
	);
INSERT INTO interface_dhis2_products (
	data_element,
	data_element_id,
	elmis_code,
	data_element_key
)
VALUES
	(
		'Mosquito Net Size 180(L) X 160 (W) X 180 (H) With Long Lasting Insecticide Treatment (LLIN)',
		'XXXXXXXXXX',
		'20090063BE',
		'CONSUMED'
	);




