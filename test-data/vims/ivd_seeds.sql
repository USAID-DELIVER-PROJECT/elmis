delete from vaccine_vitamin_supplementation_age_groups;

SELECT pg_catalog.setval('vaccine_vitamin_supplementation_age_groups_id_seq', 3, true);

INSERT INTO vaccine_vitamin_supplementation_age_groups VALUES (1, '9 Months', '0 to 9 months of age', 1, NULL, NOW(), NULL, NOW());
INSERT INTO vaccine_vitamin_supplementation_age_groups VALUES (2, '15 Months', '9 - 15 months of age', 2, NULL, NOW(), NULL, NOW());
INSERT INTO vaccine_vitamin_supplementation_age_groups VALUES (3, '18 months', '15 - 18 months of age', 3, 1, NOW(), 1, NOW());


delete from vaccine_discarding_reasons;

SELECT pg_catalog.setval('vaccine_discarding_reasons_id_seq', 6, true);

INSERT INTO vaccine_discarding_reasons VALUES (1, 'Expired', false, 1, NULL, NOW(), NULL, NOW());
INSERT INTO vaccine_discarding_reasons VALUES (2, 'Broken', false, 2, NULL, NOW(), NULL, NOW());
INSERT INTO vaccine_discarding_reasons VALUES (3, 'Cold Chain Failure', false, 3, NULL, NOW(), NULL, NOW());
INSERT INTO vaccine_discarding_reasons VALUES (4, 'VVM Change', false, 4, NULL, NOW(), NULL, NOW());
INSERT INTO vaccine_discarding_reasons VALUES (5, 'Frozen Vials', false, 5, NULL, NOW(), NULL, NOW());
INSERT INTO vaccine_discarding_reasons VALUES (6, 'Other (Specify)', true, 6, NULL, NOW(), NULL, NOW());