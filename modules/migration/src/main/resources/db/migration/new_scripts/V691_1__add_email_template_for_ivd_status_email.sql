DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_IVD_FORM_APPROVED';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_IVD_FORM_APPROVED',
'Mothly IVD form approval email template',
'Notification - Email',
'Mothly IVD form approval email template',
'<h2><strong>Monthly IVD Form Approved</strong></h2><p></p><p>Dear ${model.name},</p><p><br></p><p>IVD form submission for facility ${model.facility_name} and period ${model.period_name} has been Approved.</p><p><br></p><p>Thanks,<br></p>', 'HTML', 41);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_IVD_FORM_SUBMISSION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_IVD_FORM_SUBMISSION',
'Mothly IVD form submission email template',
'Notification - Email',
'Mothly IVD form submission email template',
'<h2><strong>Monthly IVD Form submitted</strong></h2><p></p><p>Dear ${model.name},</p><p><br></p><p>Facility ${model.facility_name} have submitted an IVD form, please review it.</p><p><br></p><p>Thanks,<br></p>', 'HTML', 41);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_IVD_FORM_REJECTION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_IVD_FORM_REJECTION',
'Mothly IVD form rejection email template',
'Notification - Email',
'Mothly IVD form rejection email template',
'<h2><strong>Monthly IVD Form Rejected</strong></h2><p></p><p>Dear ${model.name},</p><p><br></p><p>IVD form submission for facility ${model.facility_name} and period ${model.period_name} have has been rejected. Please review and<br>submit again.</p><p><br></p><p>Thanks,<br></p>', 'HTML', 41);



DELETE FROM configuration_settings where key = 'EMAIL_SUBJECT_IVD_FORM_SUBMISSION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_SUBJECT_IVD_FORM_SUBMISSION',
'Mothly IVD report submission email subject',
'Notification - Email',
'IVD report submssion email subject',
'IVD Report Submitted', 'TEXT_AREA', 41);

DELETE FROM configuration_settings where key = 'EMAIL_SUBJECT_IVD_FORM_APPROVAL';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_SUBJECT_IVD_FORM_APPROVAL',
'Mothly IVD report approval email subject',
'Notification - Email',
'IVD report approval email subject',
'IVD Report Approved', 'TEXT_AREA', 41);

DELETE FROM configuration_settings where key = 'EMAIL_SUBJECT_IVD_FORM_REJECTION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_SUBJECT_IVD_FORM_REJECTION',
'Mothly IVD report rejection email subject',
'Notification - Email',
'IVD report rejection email subject',
'IVD Report Rejected', 'TEXT_AREA', 41);