package edu.upc.eetac.dsa.ajimenezherrero.beeter.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.ajimenezherrero.beeter.api.model.Sting;
import edu.upc.eetac.dsa.ajimenezherrero.beeter.api.model.StingCollection;
import edu.upc.eetac.dsa.ajimenezherrero.beeter.api.model.User;

@Path("/user/{username}")
public class UserResource {
	@Context
	private SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	private boolean owner;

	@GET
	@Produces(MediaType.BEETER_API_USER)
	public User getUser(@PathParam("username") String username) {
		owner = username.equals(security.getUserPrincipal().getName());
		User user = new User();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetUserByUsername());
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return user;
	}

	private String buildGetUserByUsername() {
		return "select * from users where username=?";
	}

	@GET
	@Path("/stings")
	@Produces(MediaType.BEETER_API_STING_COLLECTION)
	public StingCollection getStings(@PathParam("username") String username) {
		StingCollection stings = new StingCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetStingsQuery());
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Sting sting = new Sting();
				sting.setId(rs.getString("stingid"));
				sting.setUsername(rs.getString("username"));
				sting.setSubject(rs.getString("subject"));
				stings.addSting(sting);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return stings;
	}

	private String buildGetStingsQuery() {
		return "select * from stings where username = ?";
	}

	@PUT
	@Consumes(MediaType.BEETER_API_USER)
	@Produces(MediaType.BEETER_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {
		validateUser(username);
		validateUpdateUser(user);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;

		try {
			String sql = buildUpdateUser();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, security.getUserPrincipal().getName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return user;
	}

	private void validateUpdateUser(User user) {
		if (user.getName() != null & user.getName().length() > 70)
			throw new BadRequestException(
					"Name can't be greater than 70 characters.");
		if (user.getEmail() != null & user.getEmail().length() > 255)
			throw new BadRequestException(
					"Email can't be greater than 255 characters.");
	}

	private String buildUpdateUser() {
		return "update users set name=ifnull(?, name), email=ifnull(?, email) where username=?";
	}

	private void validateUser(String username) {
		if (!security.getUserPrincipal().getName().equals(username))
			throw new ForbiddenException(
					"You are not allowed to modify this sting.");
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean isOwner) {
		this.owner = isOwner;
	}
}
