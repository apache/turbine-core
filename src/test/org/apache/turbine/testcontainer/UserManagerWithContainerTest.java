package org.apache.turbine.testcontainer;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import org.apache.fulcrum.security.entity.ExtendedUser;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.ConstraintViolationException;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.TurbineConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Steps to run this @see {@link BuildContainerWithDockerfileTest}
 *
 * TODO
 * This test might be more useful in a running turbine environment,
 * e.g. created by archetypes. Only one problem remains the mapped port, which is known only at runtime.
 *
 * @author gkallidis
 *
 */
@TestMethodOrder(OrderAnnotation.class)
@Testcontainers
@Tag("docker")
// TODO disabled. requires manual port mapping in docker-manager/TorqueTest.properties, 
// until not using Torque4.1 with xml configuration and overwriting user.settings.
@Disabled
class UserManagerWithContainerTest {

   @TurbineService
   SecurityService turbineSecurityService;

   static TurbineConfig tc;

   boolean onDeleteCascade = true;
   
   private static Logger log = LogManager.getLogger();
   
   @Container
   private static GenericContainer MY_SQL_CONTAINER = BuildContainerWithDockerfileTest.MY_SQL_CONTAINER;
   
   @BeforeAll
   public static void init() {

      MY_SQL_CONTAINER.setStartupAttempts( 3 );   // see MySQLContainer
      tc = new TurbineConfig(".",
              "/conf/test/docker-manager/CompleteTurbineResources.properties");
      try {
         // TODO get Torque component configuration and override torque.dsfactory.default.connection.url with url mapped port.
         BuildContainerWithDockerfileTest.getConnection();
         tc.initialize();
      } catch (Exception e) {
         fail();
      }

      //Torque.getInstance();
   }

   /**
    * executes as designed even if tests are disabled
    * @throws Exception
    */
   @BeforeEach
   public void before() throws Exception {
      AnnotationProcessor.process(this);
   }

   @Test
   @Order(1)
   @Tag("docker")
   @Disabled
   public void testCreateManagedUser()
           throws Exception
   {
      User user = turbineSecurityService.getUserInstance();
      user.setAccessCounter( 5 );
      user.setName( "ringo" );
      // required not null constraint
      ( (ExtendedUser) user ).setFirstName( user.getName() );
      ( (ExtendedUser) user ).setLastName( user.getName() );
      turbineSecurityService.addUser( user, "fakepassword" );
      assertTrue( turbineSecurityService.accountExists( user ) );
      //assertTrue( turbineSecurityService.getUserManager().checkExists( user ) );
   }
   
   @Test
   @Order(2)
   @Tag("docker")
   @Disabled
   void selectNewUser() {
      User ringo;
      try {
         ringo = turbineSecurityService.getUser("ringo");
         assertEquals("ringo", ringo.getFirstName());
         deleteUser(ringo);
      } catch (Exception sqle) {
          log.error( "new user error",sqle);
          fail();
      }

      try {
         ringo = turbineSecurityService.getUser("ringo");
         fail("Should throw UnknownEntity");
      } catch (UnknownEntityException sqle) {
         log.info( "correct entity unknown",sqle);
      } catch (Exception sqle) {
         log.error( "new user error",sqle);
         fail();
      }
   }

   private void deleteUser( User user )
   {
      if ( onDeleteCascade )
      {
         try
         {
            // revokeAll is called before user delete
            turbineSecurityService.removeUser( user );
            log.info( "try to delete user " + user.getName() );
         }
         catch ( Exception e )
         {
            log.error( "deleting user " + user.getName() + " failed. "
                    + e.getMessage() );
            if ( e.getCause() != null &&
                    e.getCause() instanceof ConstraintViolationException)
            {
               log.info( "error due to " + e.getCause().getMessage() );
            }
            else
            {
               log.info( "error due to " + e.getMessage() );
            }
         }
      }
      else
      {
         log.info( "onDeleteCascade false, user " + user.getName()
                 + " not deleted!" );
      }
   }
}
