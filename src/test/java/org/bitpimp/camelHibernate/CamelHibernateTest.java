package org.bitpimp.camelHibernate;

import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apacheextras.camel.component.hibernate.HibernateEndpoint;
import org.apacheextras.camel.component.hibernate.TransactionStrategy;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/META-INF/spring/spring-context.xml")
public class CamelHibernateTest extends CamelTestSupport {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private TransactionStrategy transactionStrategy;

	@Produce(uri = "direct:start")
	protected ProducerTemplate template;

	@EndpointInject(uri = "mock:to")
	protected MockEndpoint toEndpoint;

	@EndpointInject(uri = "hibernate:org.bitpimp.camelHibernate.Person")
	protected HibernateEndpoint hibernateEndpoint;

	private final RouteBuilder routeBuilder = new RouteBuilder() {
		private final DataFormat jaxb = new JaxbDataFormat(
				"org.bitpimp.camelHibernate");

		@Override
		public void configure() {
			// As HibernateEndpoint is passed down directly into the route
			// setup Hibernate specifics on this instance before it is used
			hibernateEndpoint.setTransactionStrategy(transactionStrategy);

			// Send a persisted XML bean to JAXB unmarshaller then
			// onto mock and hibernate endpoints
			from("direct:start")
				.tracing()
				.unmarshal(jaxb)
				.multicast().to(toEndpoint, hibernateEndpoint);
		}
	};

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return routeBuilder;
	}

	@Test
	public void testRoute() throws Exception {

		// send an xml string which should be unmarshalled into a POJO and
		// serialized to hibernate
		final Person expectedPOJO = new Person("James", "Strachan", "London");
		toEndpoint.expectedBodiesReceived(expectedPOJO);

		// Send XML payload and check mock endpoint gets the POJO back
		template.sendBodyAndHeader(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<person user=\"james\">\r\n"
						+ "  <firstName>James</firstName>\r\n"
						+ "  <lastName>Strachan</lastName>\r\n"
						+ "  <city>London</city>\r\n" + "</person>",
						"foo", "bar");

		toEndpoint.assertIsSatisfied();

		// Verify real endpoint has the persisted data from POJO serialization
		Session session = sessionFactory.openSession();
		SQLQuery q = session.createSQLQuery("SELECT * from People").addEntity(
				Person.class);
		List<?> results = q.list();
		Assert.assertNotNull(results);
		Assert.assertTrue(results.size() == 1);
		Assert.assertEquals(expectedPOJO, results.get(0));
		session.close();
	}
}
