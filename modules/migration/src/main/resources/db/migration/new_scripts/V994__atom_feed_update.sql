/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

DROP TABLE IF EXISTS atomfeed.chunking_history;

CREATE TABLE atomfeed.chunking_history
(
    id           SERIAL PRIMARY KEY,
    chunk_length bigint,
    start        bigint NOT NULL
);


ALTER TABLE atomfeed.chunking_history
    OWNER TO postgres;

DROP TABLE IF EXISTS atomfeed.databasechangelog;

CREATE TABLE atomfeed.databasechangelog
(
    id            character varying(63)    NOT NULL,
    author        character varying(63)    NOT NULL,
    filename      character varying(200)   NOT NULL,
    dateexecuted  timestamp with time zone NOT NULL,
    orderexecuted integer                  NOT NULL,
    exectype      character varying(10)    NOT NULL,
    md5sum        character varying(35),
    description   character varying(255),
    comments      character varying(255),
    tag           character varying(255),
    liquibase     character varying(20)
);


ALTER TABLE atomfeed.databasechangelog
    OWNER TO postgres;

CREATE TABLE atomfeed.databasechangeloglock
(
    id          integer NOT NULL,
    locked      boolean NOT NULL,
    lockgranted timestamp with time zone,
    lockedby    character varying(255)
);


ALTER TABLE atomfeed.databasechangeloglock
    OWNER TO postgres;

DROP TABLE IF EXISTS atomfeed.event_records;

CREATE TABLE atomfeed.event_records
(
    id           SERIAL PRIMARY KEY,
    uuid         character varying(40),
    title        character varying(255),
    "timestamp"  timestamp with time zone DEFAULT CURRENT_TIMESTAMP(6),
    uri          character varying(255),
    object       character varying(1000),
    category     character varying(255),
    date_created timestamp with time zone,
    tags         character varying(255)
);


DROP TABLE IF EXISTS atomfeed.event_records_offset_marker;

CREATE TABLE atomfeed.event_records_offset_marker
(
    id          SERIAL PRIMARY KEY,
    event_id    integer,
    event_count integer,
    category    character varying(255)
);

DROP TABLE IF EXISTS atomfeed.event_records_queue;

CREATE TABLE atomfeed.event_records_queue
(
    id          SERIAL PRIMARY KEY,
    uuid        character varying(40),
    title       character varying(255),
    "timestamp" timestamp with time zone DEFAULT CURRENT_TIMESTAMP(6),
    uri         character varying(255),
    object      character varying(1000),
    category    character varying(255),
    tags        character varying(255)
);

CREATE INDEX event_records_category_idx ON atomfeed.event_records USING btree (category);

