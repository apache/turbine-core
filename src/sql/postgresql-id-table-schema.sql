

                                        
-----------------------------------------------------------------------------
-- ID_TABLE
-----------------------------------------------------------------------------
drop sequence ID_TABLE_SEQ;
drop table ID_TABLE;

CREATE TABLE ID_TABLE
(
    ID_TABLE_ID serial,
    TABLE_NAME varchar (255) NOT NULL,
    NEXT_ID integer,
    QUANTITY integer,
    PRIMARY KEY(ID_TABLE_ID),
    UNIQUE (TABLE_NAME)
);

                        