DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_IVD_FORM_APPROVED';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_IVD_FORM_APPROVED',
'Mothly IVD form approval email template',
'Notification - Email',
'Mothly IVD form approval email template',
'<p><span>Dear </span><b>${model.name}</b><span>,</span><br/></p><p><br/>The following changes to your IVD form, for <b>${model.facility_name}</b> facility and <b>${model.period_name}</b> period were made on <b>${model.date_processed}</b> <br/><br/><b>IVD form status approved.</b><br/><br/>If there is any question you have regarding on this IVD status change, please contact the IVD approver for more information.</p><p><span><br/>Thanks,</span><br/></p>',
'HTML', 41);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_IVD_FORM_SUBMISSION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_IVD_FORM_SUBMISSION',
'Mothly IVD form submission email template',
'Notification - Email',
'Mothly IVD form submission email template',
'Dear <b>${model.name}</b>,<br/><br/><b>${model.user_name}</b> submitted an IVD form on behalf of <b>${model.facility_name}</b> for the <b>${model.period_name}</b> period. <br/>Please <a href="${model.approval_url}">click here</a>  to review and approve this IVD form. You can also login to the system and find this IVD form pending<br/>on Approve IVD page.',
'HTML', 41);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_IVD_FORM_REJECTION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_IVD_FORM_REJECTION',
'Mothly IVD form rejection email template',
'Notification - Email',
'Mothly IVD form rejection email template',
'<p><span>Dear </span><b>${model.name}</b><span>,</span><br/></p><p><br/>The following changes to your IVD form, for <b>${model.facility_name}</b> facility and <b>${model.period_name}</b> period were made on <b>${model.date_processed}</b> <br/><br/><b>IVD form status rejected.</b><br/><br/>If there is any question you have regarding on this IVD status change, please contact the IVD approver for more information.</p><p><span><br/>Thanks,</span><br/></p>',
'HTML', 41);



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