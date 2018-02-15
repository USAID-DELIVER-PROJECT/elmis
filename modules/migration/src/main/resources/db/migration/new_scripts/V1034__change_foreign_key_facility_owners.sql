ALTER TABLE facility_owners
  DROP CONSTRAINT facility_owners_ownerid_fkey;
ALTER TABLE facility_owners
  ADD FOREIGN KEY (ownerid) REFERENCES owners (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
