/*order requisition Notifiacation*/

delete from configuration_settings where key = 'EMAIL_TEMPLATE_FOR_VACCINE_ORDER_REQUISITION';
INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('EMAIL_TEMPLATE_FOR_VACCINE_ORDER_REQUISITION',
  'Vaccine Order requisition Submition Email for supervisor Notification Template','Notification','',

E'Dear {approver_name}

This is to inform you that {facility_name} has submitted its Vaccine Order Requisition Form for the Period {period} and requires your approval. Please login to Issue it.

{link}

Thank you.','TEXT_AREA', 42);


DELETE FROM configuration_settings where key = 'EMAIL_SUBJECT_VACCINE_ORDER_REQUISITION_FORM_SUBMISSION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_SUBJECT_VACCINE_ORDER_REQUISITION_FORM_SUBMISSION',
'Quarterly Vaccine Order requisition report email subject',
'Notification - Email',
'Quarterly Vaccine Order requisition report email subject',
'VOR Report Submitted', 'TEXT_AREA', 42);


/*
Consolidation template and subject
*/

delete from configuration_settings where key = 'EMAIL_TEMPLATE_FOR_ORDER_CONSOLIDATION';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('EMAIL_TEMPLATE_FOR_ORDER_CONSOLIDATION',
  'Email Notification Template for Supervised Facilities','Notification','',

E'Dear {user_name}

This is to inform you that {from_facility_name} has consolidated the Order that was requested on {date_submitted} for the Period {period} . Please make follow-up on consignment.

Thank you.','TEXT_AREA', 43);


DELETE FROM configuration_settings where key = 'EMAIL_SUBJECT_FOR_ORDER_CONSOLIDATION';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_SUBJECT_FOR_ORDER_CONSOLIDATION',
'Consolidated Order email subject',
'Notification - Email',
'Consolidated Order List email subject',
'Consolidated Order Details', 'TEXT_AREA', 44);


/*
Issue Voucher template and Subject
*/

delete from configuration_settings where key = 'EMAIL_TEMPLATE_FOR_ISSUE_VOUCHER';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('EMAIL_TEMPLATE_FOR_ISSUE_VOUCHER',
  'Email Notification Template for Issuing Stock','Notification','',

E'Dear {user_name}

This is to inform you that {from_facility_name} has Issued Stock for the order that was requested on {date_submitted} for the Period {period} . Please make follow-up on consignment.
{link}
Thank you.','TEXT_AREA', 45);


DELETE FROM configuration_settings where key = 'EMAIL_SUBJECT_FOR_ISSUE_VOUCHER';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_SUBJECT_FOR_ISSUE_VOUCHER',
'Issue Voucher email subject',
'Notification - Email',
'Issue Voucher email subject',
'ISSUE VOUCHER', 'TEXT_AREA', 46);