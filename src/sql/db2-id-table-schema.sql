

                                        
-----------------------------------------------------------------------------
-- ID_TABLE
-----------------------------------------------------------------------------
drop table ID_TABLE;


CREATE TABLE ID_TABLE
(
    ID_TABLE_ID INT GENERATED ALWAYS AS IDENTITY,
    TABLE_NAME VARCHAR (255) NOT NULL,
    NEXT_ID INT NOT NULL,
    QUANTITY INT NOT NULL,
    UNIQUE (TABLE_NAME)
);

ALTER TABLE ID_TABLE
    ADD CONSTRAINT ID_TABLE_PK 
PRIMARY KEY (ID_TABLE_ID);




                        