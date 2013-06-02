Camel Router Spring Project
===========================

To build this project use

    mvn install

To run this project with Maven use

    mvn camel:run

For more help see the Apache Camel documentation

    http://camel.apache.org/

 A sample project showing a POJO that can be unmarshalled from XML via JAXB and persisted to
 Hibernate DB using a Camel Route.
 
 The project is based on the Camel Maven archetype and reuses the XML objects:
<?xml version="1.0" encoding="UTF-8"?>
<person user="james">
  <firstName>James</firstName>
  <lastName>Strachan</lastName>
  <city>London</city>
</person>
 
 There is a POJO defined that is using both JAXB and JPA/Hibernate annotations due to the
 duality of its nature (belonging to both JAXB and JPA realms).
 eg: 
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(factoryClass=ObjectFactory.class, factoryMethod="createPerson")
@Entity
public class Person {
    String firstName;
    String lastName;
    String city;
}

The data source is configured in datasource.xml which uses Spring 3.1 profiles to provide a testing
and production sample profiles. Maven java:exec is set to run with testing profile by default. 
 