delete from ID_TABLE where id_table_id >= $initialID;

#foreach ($tbl in $tables)
insert into ID_TABLE (id_table_id, table_name, next_id, quantity) VALUES ($initialID, '$tbl.Name', 100, 10);
#set ( $initialID = $initialID + 1 )
#end
