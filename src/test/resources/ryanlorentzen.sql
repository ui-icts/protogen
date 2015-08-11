---
-- #%L
-- Protogen
-- %%
-- Copyright (C) 2009 - 2015 University of Iowa Institute for Clinical and Translational Science (ICTS)
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
CREATE TABLE ryanlorentzen.foo (
       foo_id INT NOT NULL
     , name TEXT
     , PRIMARY KEY (foo_id)
);

CREATE TABLE ryanlorentzen.bar (
       bar INT NOT NULL
     , name TEXT
     , PRIMARY KEY (bar)
);

CREATE TABLE ryanlorentzen.tblaccess (
       PKId INT NOT NULL
     , strAccess TEXT
     , PRIMARY KEY (PKId)
);

CREATE TABLE ryanlorentzen.tblMemberAccess (
       PKId INT NOT NULL
     , intFkAccessId INT
     , dateAdd TIME(10) WITH TIME ZONE
     , PRIMARY KEY (PKId)
     , CONSTRAINT FK_tblMemberAccess_1 FOREIGN KEY (intFkAccessId)
                  REFERENCES ryanlorentzen.tblaccess (PKId)
);

CREATE TABLE ryanlorentzen.foobar (
       foo_id INT NOT NULL
     , bar INT NOT NULL
     , PRIMARY KEY (foo_id, bar)
     , CONSTRAINT FK_TABLE_5_1 FOREIGN KEY (foo_id)
                  REFERENCES ryanlorentzen.foo (foo_id)
     , CONSTRAINT FK_TABLE_5_2 FOREIGN KEY (bar)
                  REFERENCES ryanlorentzen.bar (bar)
);

