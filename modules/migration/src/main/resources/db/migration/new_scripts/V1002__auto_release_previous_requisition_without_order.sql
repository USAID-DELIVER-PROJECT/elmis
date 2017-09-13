INSERT INTO configuration_settings(key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES
  ('AUTO_RELEASE_PREVIOUS_REQUISITIONS_WITHOUT_ORDER', 'false', 'Auto release previous regular requisitions', 'Turning this setting on will automatically release Approved R&Rs ' ||
                                                                                                              '<br />that were not converted to order When an R&R is converted to order while ' ||
                                                                                                              '<br />there are other R&Rs pending in the approval process.', 'R & R', 0, 'BOOLEAN',null, true);
