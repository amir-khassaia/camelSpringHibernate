package org.bitpimp.camelHibernate;

import org.apache.camel.spring.Main;

public class MainApp {
	 
	/**
	 * Start Spring and Camel contexts
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		  Main main = new Main();
		  main.setApplicationContextUri("META-INF/spring/camel-context.xml");
		  main.enableHangupSupport();
		  main.enableTrace();
		  main.run(args);
	}
}
