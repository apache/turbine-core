package org.apache.turbine.test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.jdbcDriver;

public class HsqlDB
{
    private Connection connection = null;
    private static Log log = LogFactory.getLog(HsqlDB.class);

    public HsqlDB(String uri, String loadFile)
            throws Exception
    {
        Class.forName(jdbcDriver.class.getName());

        this.connection = DriverManager.getConnection(uri, "sa", "");

        if (StringUtils.isNotEmpty(loadFile))
        {
            loadSqlFile(loadFile);
        }
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void close()
    {
        try
        {
            connection.close();
        }
        catch (Exception e)
        {
            // ignore
        }
    }

    private void loadSqlFile(String fileName)
            throws Exception
    {
        try (Statement statement = connection.createStatement())
        {
            String commands = getFileContents(fileName);

            for (int targetPos = commands.indexOf(';'); targetPos > -1; targetPos = commands.indexOf(';'))
            {
                String cmd = commands.substring(0, targetPos + 1).trim();

                if (cmd.startsWith("--"))
                {
                    // comment
                    int lineend = commands.indexOf('\n');
                    if (lineend > -1)
                    {
                        targetPos = lineend - 1;
                    }
                }
                else
                {
                    try
                    {
                        statement.execute(cmd);
                    }
                    catch (SQLException sqle)
                    {
                        log.warn("Statement: " + cmd + ": " + sqle.getMessage());
                    }
                }

                commands = commands.substring(targetPos + 2);
            }
        }
    }

    private String getFileContents(String fileName)
            throws Exception
    {
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));

        return new String(bytes, StandardCharsets.ISO_8859_1);
    }
}

