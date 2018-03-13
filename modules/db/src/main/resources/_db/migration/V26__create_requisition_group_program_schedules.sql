--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE TABLE requisition_group_program_schedules (
  id SERIAL PRIMARY KEY,
  requisitionGroupId INTEGER REFERENCES requisition_groups(id) NOT NULL,
  programId INTEGER REFERENCES programs(id) NOT NULL,
  scheduleId INTEGER REFERENCES processing_schedules(id) NOT NULL,
  directDelivery BOOLEAN NOT NULL,
  dropOffFacilityId INTEGER REFERENCES facilities(id),
  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (requisitionGroupId, programId)
);

CREATE INDEX i_requisition_group_program_schedules_requisitionGroupId ON requisition_group_program_schedules(requisitionGroupId);