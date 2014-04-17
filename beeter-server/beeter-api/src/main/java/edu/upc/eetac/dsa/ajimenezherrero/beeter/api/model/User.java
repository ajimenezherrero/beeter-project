package edu.upc.eetac.dsa.ajimenezherrero.beeter.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.ajimenezherrero.beeter.api.MediaType;
import edu.upc.eetac.dsa.ajimenezherrero.beeter.api.UserResource;

public class User {
	@InjectLinks({
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "self", title = "User profile", type = MediaType.BEETER_API_USER),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "edit", title = "Edit user profile", type = MediaType.BEETER_API_USER, condition = "${resource.owner}"),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "stings", title = "User stings collection", type = MediaType.BEETER_API_STING_COLLECTION, method="getStings")})
	private List<Link> links;
	private String username;
	private String name;
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}
