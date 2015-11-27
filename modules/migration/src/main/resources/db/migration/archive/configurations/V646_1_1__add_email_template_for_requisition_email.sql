DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_ESS_MEDS';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values 
('EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_ESS_MEDS',
'Requisition authorization mail template for via',
'Notification - Email',
'Requisition authorization mail templatefor via',
'<h2><strong>Dados de requisi�0�4�0�0o Via Classica anexados </strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identifica�0�4�0�0o da US</p><div style="font-size: 11pt"><p>Nos arquivos em anexo, voc�� encontrar��:<ul><li>2 arquivos em Excel intitulados: Requisi�0�4�0�0o e Regime.</li></ul></p><p>Se voc�� teve uma forma�0�4�0�0o sobre o uso do SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se voc�� n�0�0o tiver recebido nenhuma forma�0�4�0�0o ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da requisi�0�4�0�0o Via Classica para o SIMAM.</p></div>', 'HTML', 40);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MMIA';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values 
('EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MMIA',
'Requisition authorization mail  template for mmia',
'Notification - Email',
'Requisition authorization mail template for mmia',
'<h2><strong>Relatorio de MMIA anexado</strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identifica�0�4�0�0o da US</p><div style="font-size: 11pt"><p>Nos arquivos anexados, voc�� encontrar��:<ul><li>2 arquivos em Excel intitulados: Requisi�0�4�0�0o e Regime.</li></ul></p><p>Se voc�� teve uma forma�0�4�0�0o sobre o uso do  SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se voc�� n�0�0o tiver recebido nenhuma forma�0�4�0�0o ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da requisi�0�4�0�0o Via Classica para o SIMAM.</p></div>', 'HTML', 40);