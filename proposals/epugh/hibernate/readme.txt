To use this code, you need to add this section to your project.xml dependencies.  Note, you may have some
already!

<dependency>
      <id>avalon-framework</id>
      <version>4.1.4</version>
      <url>http://jakarta.apache.org/avalon</url>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>      
    </dependency>
    <dependency>
      <id>excalibur-collections</id>
      <version>1.0</version>
      <url>http://jakarta.apache.org/avalon</url>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>          
    </dependency>
    <dependency>
      <id>excalibur-component</id>
      <version>1.1</version>
      <url>http://jakarta.apache.org/avalon</url>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>          
    </dependency>
    <dependency>
      <id>excalibur-instrument</id>
      <version>1.0</version>
      <url>http://jakarta.apache.org/avalon</url>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>          
    </dependency>
    <dependency>
      <id>excalibur-logger</id>
      <version>1.0.1</version>
      <url>http://jakarta.apache.org/avalon</url>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>          
    </dependency>
    <dependency>
      <id>excalibur-pool</id>
      <version>1.2</version>
      <url>http://jakarta.apache.org/avalon</url>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>          
    </dependency>    
  <dependency>
      <id>log4j</id>
      <version>1.2.7</version>
      <url>http://jakarta.apache.org/log4j/</url>
      <properties>
        <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>
    <dependency>
      <id>logkit</id>
      <version>1.0.1</version>
      <url>http://jakarta.apache.org/avalon/logkit/</url>
      <properties>
        <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>
    <!--
    <dependency>
      <id>fulcrum</id>      
      <version>SNAPSHOT</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>              
    -->
    <dependency>
      <id>hibernate</id>      
      <version>2.0-final</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>  

    <dependency>
      <id>hibernate:hibernate-avalon</id>
      <version>0.1</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>              
    </dependency>                
    <dependency>
      <id>odmg</id>      
      <version>3.0</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>                  
    <dependency>
      <id>bcel</id>      
      <version>5.0</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>                      
    <dependency>
      <id>dom4j</id>      
      <version>1.4</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>       
    <dependency>
      <id>cglib</id>      
      <version>1.0</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>                          
    <dependency>
      <id>jcs</id>      
      <version>1.0-dev</version>
      <properties>        
      <war.bundle.jar>true</war.bundle.jar>
      </properties>
    </dependency>     



You will also need to add this to your web.xml:

    <filter>
        <filter-name>Hibernate Session Filter</filter-name>
        <filter-class>org.apache.turbine.util.hibernate.HibernateFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>Hibernate Session Filter</filter-name>
    <url-pattern>/*</url-pattern>
    </filter-mapping>

I will also try and add more examples of actualing using the Hibernate code...


Thanks, Eric (epugh@upstate.com)