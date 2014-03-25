package edu.upc.eetac.dsa.ajimenezherrero.beeter.api;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
 
public class BeeterApplication extends ResourceConfig {
	public BeeterApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
	}
}
