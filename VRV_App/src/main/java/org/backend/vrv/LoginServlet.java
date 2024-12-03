package org.backend.vrv;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Database connection
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vrv", "root", "admin");

            // Query to get user information
            String sql = "SELECT * FROM users WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // Check if the password matches the stored hashed password
                if (BCrypt.checkpw(password, hashedPassword)) {
                    // Password is correct, retrieve user roles
                    int userId = rs.getInt("id");
                    String roleSql = "SELECT r.role_name FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?";
                    ps = conn.prepareStatement(roleSql);
                    ps.setInt(1, userId);
                    rs = ps.executeQuery();

                    // Store roles in session
                    Set<String> roles = new HashSet<>();
                    while (rs.next()) {
                        roles.add(rs.getString("role_name"));
                    }
                    request.getSession().setAttribute("roles", roles);

                    // Redirection based on role
                    if (roles.contains("ADMIN")) {
                        response.sendRedirect("admin/dashboard.jsp");
                    } else if (roles.contains("USER")) {
                        response.sendRedirect("user/dashboard.jsp");
                    } else {
                        response.sendRedirect("index.jsp");
                    }
                } else {
                    response.sendRedirect("login.jsp?error=invalid");
                }
            } else {
                response.sendRedirect("login.jsp?error=invalid");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
