package vttp2023.batch4.paf.assessment.repositories;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.models.Bookings;
import vttp2023.batch4.paf.assessment.models.User;

@Repository
public class BookingsRepository {
	
	// You may add additional dependency injections

	public static final String SQL_SELECT_USER_BY_EMAIL = "select * from users where email like ?";

	@Autowired
	private JdbcTemplate template;

	// You may use this method in your task
	public Optional<User> userExists(String email) {
		System.out.println("Check user exists");
		System.out.println(email);
		SqlRowSet rs = template.queryForRowSet(SQL_SELECT_USER_BY_EMAIL, email);
		if (!rs.next())
			return Optional.empty();

		return Optional.of(new User(rs.getString("email"), rs.getString("name")));
	}

	// TODO: Task 6
	// IMPORTANT: DO NOT MODIFY THE SIGNATURE OF THIS METHOD.
	// You may only add throw exceptions to this method
	public void newUser(User user) throws SQLException {
		System.out.println("Adding user to SQL 'user'");
		String query = """
				INSERT INTO users VALUES (?, ?)
				""";
		int updated = template.update(query, user.email(), user.name());
		if (!(updated > 0)) {
			throw new SQLException("Failed to add user");
		}
	}

	// TODO: Task 6
	// IMPORTANT: DO NOT MODIFY THE SIGNATURE OF THIS METHOD.
	// You may only add throw exceptions to this method
	public void newBookings(Bookings bookings) throws SQLException {
		System.out.println("Adding booking to SQL 'bookings'");
		String query = """
				INSERT INTO bookings VALUES (?, ?, ?, ?)
				""";
		int updated = template.update(query,
						bookings.getBookingId(),
						bookings.getListingId(),
						bookings.getDuration(),
						bookings.getEmail());
		if (!(updated > 0)) {
			throw new SQLException("Failed to add user");
		}
	}
}
