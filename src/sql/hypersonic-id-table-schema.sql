-----------------------------------------------------------------------------
-- ID_TABLE
-----------------------------------------------------------------------------
CREATE TABLE ID_TABLE
(
    ID_TABLE_ID integer IDENTITY,
    TABLE_NAME VARCHAR (255),
    NEXT_ID integer,
    QUANTITY integer,
    PRIMARY KEY(ID_TABLE_ID),
    UNIQUE (TABLE_NAME)
);



