delete from configuration_settings where key = 'VIMS_DISTRIBUTION_PERIODS';

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VIMS_DISTRIBUTION_PERIODS','4','Number of Month for facility to distribute','Used to calculate number of month to be shown during distribution','VACCINE','TEXT',true);
