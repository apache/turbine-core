

                                        
-----------------------------------------------------------------------------
-- ID_TABLE
-----------------------------------------------------------------------------
drop table ID_TABLE cascade constraints;
drop sequence ID_TABLE_SEQ;

CREATE TABLE ID_TABLE
(
    ID_TABLE_ID INT NOT NULL,
    TABLE_NAME VARCHAR2 (255) NOT NULL,
    NEXT_ID INT,
    QUANTITY INT,
    UNIQUE (TABLE_NAME)
);

ALTER TABLE ID_TABLE
    ADD CONSTRAINT ID_TABLE_PK 
PRIMARY KEY (ID_TABLE_ID);




                        