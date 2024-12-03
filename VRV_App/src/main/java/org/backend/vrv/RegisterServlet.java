package org.backend.vrv;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Hash password using BCrypt
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

        // Database connection
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vrv", "root", "admin");

            // Insert user into the database
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.executeUpdate();

            // Get user ID after insert
            String getUserIdSql = "SELECT id FROM users WHERE username = ?";
            ps = conn.prepareStatement(getUserIdSql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");

                // Check if role ID 1 exists in the roles table
                String checkRoleSql = "SELECT COUNT(*) FROM roles WHERE id = 1";
                ps = conn.prepareStatement(checkRoleSql);
                ResultSet roleResult = ps.executeQuery();

                if (roleResult.next() && roleResult.getInt(1) > 0) {
                    // Role exists, assign it to the user
                    String assignRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
                    ps = conn.prepareStatement(assignRoleSql);
                    ps.setInt(1, userId);
                    ps.setInt(2, 1);  // Default USER role ID
                    ps.executeUpdate();
                } else {
                    System.out.println("Role ID 1 does not exist in the roles table.");
                    // Handle the error (e.g., display a message to the user or insert a default role)
                }
            }

            response.sendRedirect("login.jsp");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
