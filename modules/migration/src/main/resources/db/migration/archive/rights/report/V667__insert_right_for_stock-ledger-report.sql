delete from rights where name = 'VIEW_STOCK_LEDGER_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_STOCK_LEDGER_REPORT','REPORT','Permission to View Stock Ledger Report');

update rights set displayNameKey = 'label.rights.view.stock.ledger.report' where name = 'VIEW_STOCK_LEDGER_REPORT';