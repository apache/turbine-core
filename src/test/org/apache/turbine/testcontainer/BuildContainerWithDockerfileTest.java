package org.apache.turbine.testcontainer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Steps to run this
 *
 * Requirements:
 * <li>Unix: (Debian stretch tested):set <code>DOCKER_HOST=unix:///var/run/docker.sock</code> in docker-java.properties
 * (find the template in conf/docker-resources/db/dj.p.template) and comment out all other environment keys.
 * <li>Windows 10: Docker Desktop should provide all required configuration by default or
 * you need to create a local machine, e.g. with <code>docker-machine -d hyperv <vmname-default></code>
 * For more information https://docs.docker.com/machine/get-started/, https://docs.docker.com/machine/drivers/hyper-v/.
 * <li>Windows 7/VirtualBox: copy DOCKER_* properties to ~/.docker-java.properties or docker-java.properties in classpath..
 * To get the environment run: <code>docker-machine env default</code>, if your default docker machine is named default.
 * Verify the name with <code>docker-machine ls</code>.
 *
 * Turbine pom.xml has folder conf/docker-resources enabled as test-resource, you may put the files there.
 * You may need to copy machines/&lt;docker-machine-name&gt;/certs from DOCKER_CERT_PATH to local path ~/.docker/machine/certs
 *
 * Note/Hints:
 * <li>Testcontainers starts docker-machine, if not started.
 * <li>Windows 7: Before running manually any docker-machine command, you must close the VirtualBox GUI if opened.
 * <li>To get results from <code>docker images</code>, you have to set the environment variables, see output from <code>docker-machine env <vmname></code>.
 *
 * Lookup of repository:
 *
 * Testcontainers checks
 * <li>~/.testcontainers.properties, then <code>classpath/testcontainers.properties</code>
 * <li>~/.docker-java.properties, then docker-java.properties -> set DOCKER_* properties,
 * may set DOCKER_CERT_PATHalways with forward slashes.
 * <li>At last also ~/.docker/config.json is checked for username/password for docker.io
 * Additional
 * More info for database init sql:
 * <li>https://www.testcontainers.org/modules/databases/mysql/
 * <li>https://www.testcontainers.org/modules/databases/#using-an-init-script-from-a-file
 *
 * Bugs: docker virtualbox vm seems to auto pause.
 * Check your docker vm with <code>docker-machine ls</code> and <code>docker-machine start <vmname></code>.
 *
 * @author gkallidis
 *
 */
@TestMethodOrder(OrderAnnotation.class)
@Testcontainers
@Tag("docker")
class BuildContainerWithDockerfileTest {

   public static final Path RESOURCE_PATH =
           FileSystems.getDefault().getPath(".").resolve("conf/docker-resources/db/");

   private static Logger log = LogManager.getLogger();

   public static int SERVICE_PORT = 3306;

   public static String DATABASE_NAME = "default";

   Connection connection;

   @Container
   public static GenericContainer MY_SQL_CONTAINER =   new GenericContainer<>(
           new ImageFromDockerfile()
            .withFileFromPath(".", RESOURCE_PATH)
        ).withExposedPorts( SERVICE_PORT ) //.withStartupAttempts( 2 )
         .withEnv(  "MYSQL_DATABASE", DATABASE_NAME )
         .withEnv( "MYSQL_USER", "userdb"  )
         .withEnv( "MYSQL_PASSWORD", "test1234" )
         .withEnv( "MYSQL_ROOT_PASSWORD","test1234" );

// reduce dependencies, but might use for debugging
//    MY_SQL_CONTAINER = new MySQLContainer<>()
//   .withDatabaseName( DATABASE_NAME).withUsername( "userdb" ).withPassword( "test1234" )
//   .withInitScript( "./db/mysql/initdb.d/data.sql" )
//   .withExposedPorts( SERVICEPORT )

   @BeforeAll
   public static void init() {

      MY_SQL_CONTAINER.setStartupAttempts( 3 );   // see MySQLContainer
   }

   @BeforeEach
   public void before() throws Exception {
      connection = getConnection();
   }

   @Test
   @Order(2)
   void createUser() throws SQLException {
      if (connection == null) return;
      try (PreparedStatement preparedStatement =
              connection.prepareStatement(
                 "INSERT INTO TURBINE_USER (USER_ID,LOGIN_NAME,PASSWORD_VALUE,FIRST_NAME,LAST_NAME) values (?,?,?,?,?)")) {
         preparedStatement.setString(1, "4");
         preparedStatement.setString(2, "kzipfel");
         preparedStatement.setString(3, "kzipfel");
         preparedStatement.setString(4, "Konrad");
         preparedStatement.setString(5, "Zipfel");
         assertFalse(preparedStatement.execute());
         Assertions.assertEquals(1, preparedStatement.getUpdateCount());
      }
   }

   @Test
   @Order(1)
   void selectExistingUser() throws SQLException {
      if (connection == null) return;
      try (PreparedStatement preparedStatement =
              connection.prepareStatement(
                 "select USER_ID, LAST_NAME, FIRST_NAME from TURBINE_USER where USER_ID=?")) {
         preparedStatement.setString(1, "1");
         ResultSet resultSet = preparedStatement.executeQuery();
         assertTrue(resultSet.next());
         assertEquals("Admin", resultSet.getString("LAST_NAME"));
         assertEquals("", resultSet.getString("FIRST_NAME"));
         resultSet.close();
      }
   }

   @Test
   @Order(3)
   void selectNewUser() throws SQLException {
      if (connection == null) return;
      try (PreparedStatement preparedStatement =
              connection.prepareStatement(
                 "select USER_ID, LAST_NAME, FIRST_NAME from TURBINE_USER where USER_ID=?")) {
         preparedStatement.setString(1, "4");
         ResultSet resultSet = preparedStatement.executeQuery();
         assertTrue(resultSet.next());
         assertEquals("Zipfel", resultSet.getString("LAST_NAME"));
         assertEquals("Konrad", resultSet.getString("FIRST_NAME"));
      }
   }

   public static Connection getConnection() throws SQLException {
     String jdbcStr = generateJdbcUrl();
     if (jdbcStr == null) {
         return null;
     }
      return DriverManager
         .getConnection(jdbcStr, "userdb", "test1234");
   }

   // https://www.testcontainers.org/modules/databases/
   // String.format("jdbc:tc:mysql:5.7.22://%s/%s", "dummy_host",
   // "test"); this will use database test, but allows e.g. custom cfg: ?TC_MY_CNF=x.cfg
   // TODO inform torque about mapped port, use overriding configuration in torque 4.1
   public static String generateJdbcUrl() {
      if (MY_SQL_CONTAINER == null) { return null; }
      if (!MY_SQL_CONTAINER.isRunning()) {
          MY_SQL_CONTAINER.start();
      }

      String serviceHost = MY_SQL_CONTAINER.getContainerIpAddress();
      Integer mappedPort = MY_SQL_CONTAINER.getMappedPort(SERVICE_PORT);// e.g. 32811
      log.info("generate jdbc url from {}, mapped Port: {}, bounded port: {}", serviceHost, mappedPort, MY_SQL_CONTAINER.getBoundPortNumbers());

//      if (MY_SQL_CONTAINER instanceof MySQLContainer) {
//          String genJDBC = ((MySQLContainer)MY_SQL_CONTAINER).getJdbcUrl();
//          log.info( "generated connect url: {}", genJDBC);
//      }
      String targetJDBC =
      String.format("jdbc:mysql://%s:%d/%s?loggerLevel=OFF", serviceHost,
                    mappedPort, DATABASE_NAME);
      // changing the jdbc string prefix to  jdbc:tc:mysql does handle the test database setup,
      // https://www.testcontainers.org/modules/databases/jdbc/
      log.info( "used connect url: {}", targetJDBC);
      return targetJDBC;
   }

}
