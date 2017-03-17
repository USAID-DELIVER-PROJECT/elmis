CREATE TABLE IF NOT EXISTS supply_partners
(
  id SERIAL PRIMARY KEY,
  code varchar(10) NOT NULL UNIQUE,
  name varchar(50) NOT NULL UNIQUE,
  isActive BOOLEAN NOT NULL DEFAULT TRUE,

  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT NOW(),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP
);


CREATE TABLE IF NOT EXISTS supply_partner_programs
(
  id SERIAL PRIMARY KEY,
  supplyPartnerId INTEGER NOT NULL REFERENCES supply_partners(id),
  sourceProgramId INTEGER NOT NULL REFERENCES programs(id),
  destinationProgramId INTEGER NOT NULL REFERENCES programs(id),
  destinationSupervisoryNodeId INTEGER NOT NULL REFERENCES supervisory_nodes(id),
  destinationRequisitionGroupId INTEGER NOT NULL REFERENCES requisition_groups(id),

  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT NOW(),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP
);


CREATE TABLE IF NOT EXISTS supply_partner_program_products(
  id SERIAL PRIMARY KEY,
  supplyPartnerProgramId INTEGER REFERENCES supply_partner_programs(id),
  productId INTEGER REFERENCES products(id),
  percentageSupported INTEGER NOT NULL DEFAULT (100),
  active BOOLEAN NOT NULL DEFAULT (TRUE),

  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT NOW(),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP
);


CREATE TABLE IF NOT EXISTS supply_partner_program_facilities(
  id SERIAL PRIMARY KEY,
  supplyPartnerProgramId INTEGER REFERENCES supply_partner_programs(id),
  facilityId INTEGER REFERENCES facilities(id),
  active BOOLEAN NOT NULL DEFAULT (TRUE),

  createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT NOW(),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP
);
