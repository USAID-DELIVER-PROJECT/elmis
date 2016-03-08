delete from rights where name = 'VIEW_VACCINE_STOCK_STATUS_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_VACCINE_STOCK_STATUS_REPORT','REPORT','Permission to View Vaccine Stock Status Report');

update rights set displayNameKey = 'label.rights.vew.vaccine.stock.status.report' where name = 'VIEW_VACCINE_STOCK_STATUS_REPORT';
