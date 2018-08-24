INSERT INTO master_rnr_columns (name,
                                position,
                                label,
                                source,
                                sourceConfigurable,
                                formula,
                                indicator,
                                used,
                                visible,
                                mandatory,
                                description)
VALUES ('mos', 25, 'MOS', 'R', false, 'formula.column.mos', 'indicator.column.mos', true, true, false, 'description.column.mos');


INSERT INTO program_rnr_columns
    (programid, mastercolumnid, label, visible, position, source, formulavalidationrequired, createdby, createddate)
values
select
    distinct(programid), (select id from master_rnr_columns where name = 'mos'), 'MOS', true, 26, 'R', false, 1, now()
    from program_rnr_columns;