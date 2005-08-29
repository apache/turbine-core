-- Copyright 2001-2005 The Apache Software Foundation.
--
-- Licensed under the Apache License, Version 2.0 (the "License")
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
/* ---------------------------------------------------------------------- */
/* ID_TABLE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'ID_TABLE')
BEGIN
	DROP TABLE ID_TABLE
END
;

CREATE TABLE ID_TABLE
(
    ID_TABLE_ID INT NOT NULL,
    TABLE_NAME VARCHAR (255) NOT NULL,
    NEXT_ID INT NULL,
    QUANTITY INT NULL,
    CONSTRAINT ID_TABLE_PK PRIMARY KEY(ID_TABLE_ID),
    UNIQUE (TABLE_NAME)
);




