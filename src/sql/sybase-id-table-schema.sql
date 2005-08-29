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




