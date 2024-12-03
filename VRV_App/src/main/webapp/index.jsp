<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f3f3f3;
            transition: background-color 1s, color 1s;
        }
        .container {
            text-align: center;
            margin-top: 100px;
        }
        h1 {
            font-size: 3rem;
            color: #333;
        }
        p {
            font-size: 1.2rem;
            color: #666;
        }
        a {
            text-decoration: none;
            margin: 10px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        a:hover {
            background-color: #0056b3;
        }
        button {
            font-size: 20px;
            padding: 10px 20px;
            margin-top: 20px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        button:hover {
            background-color: #218838;
        }
        .night-mode {
            background-color: #333;
            color: white;
        }
        .day-mode {
            background-color: #f3f3f3;
            color: #333;
        }
    </style>
</head>
<body class="day-mode">
    <div class="container">
        <h1>Welcome to Our Application</h1>
        <p>Please log in or register to continue.</p>
        <a href="login.jsp">Login</a>
        <a href="register.jsp">Register</a>
       

    </div>

</body>
</html>
