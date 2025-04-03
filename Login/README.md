# Login and Registration System

This project is a simple **Login and Registration System** built using **PHP**, **MySQL**, **HTML**, **CSS**, and **JavaScript**. It allows users to register with their details and log in to access a protected homepage.

---

## Features

- **User Registration**:
  - Users can register by providing their first name, last name, email, and password.
  - Passwords are securely hashed before being stored in the database.

- **User Login**:
  - Registered users can log in using their email and password.
  - Sessions are used to maintain user authentication.

- **Protected Homepage**:
  - Only logged-in users can access the homepage.
  - If a user is not logged in, they are redirected to the login page.

- **Responsive Design**:
  - The interface is styled with CSS and is mobile-friendly.

---

## Technologies Used

- **Frontend**:
  - HTML5
  - CSS3
  - JavaScript (for form toggling and interactivity)

- **Backend**:
  - PHP (for server-side logic)

- **Database**:
  - MySQL (for storing user data)

---

## File Structure
Login/ 
├── index.php # Main page for registration and login 
├── register.php # Handles user registration and login logic 
├── homepage.php # Protected page for logged-in users 
├── connect.php # Database connection file 
├── logout.php # Handles user logout 
├── style.css # Stylesheet for the project 
├── script.js # JavaScript for form toggling 
└── README.md # Project documentation

---

## Setup Instructions

1. **Install XAMPP**:
   - Download and install [XAMPP](https://www.apachefriends.org/index.html) to set up a local PHP and MySQL environment.

2. **Clone the Repository**:
   - Clone this project into the `htdocs` folder of your XAMPP installation:
     ```
     C:\xampp\htdocs\Login
     ```

3. **Set Up the Database**:
   - Open phpMyAdmin (`http://localhost/phpmyadmin`).
   - Create a new database named `login`.
   - Import the following SQL to create the `users` table:
     ```sql
     CREATE TABLE users (
         id INT AUTO_INCREMENT PRIMARY KEY,
         firstName VARCHAR(50) NOT NULL,
         lastName VARCHAR(50) NOT NULL,
         email VARCHAR(50) NOT NULL UNIQUE,
         password VARCHAR(255) NOT NULL
     );
     ```

4. **Configure the Database Connection**:
   - Open `connect.php` and update the database credentials if necessary:
     ```php
     <?php
     $servername = "localhost";
     $username = "root";
     $password = "";
     $dbname = "login";

     $conn = new mysqli($servername, $username, $password, $dbname);

     if ($conn->connect_error) {
         die("Connection failed: " . $conn->connect_error);
     }
     ?>
     ```

5. **Start the Server**:
   - Open the XAMPP control panel and start **Apache** and **MySQL**.

6. **Access the Application**:
   - Open your browser and navigate to:
     ```
     http://localhost/Login/index.php
     ```

---

## How to Use

1. **Register**:
   - Fill in the registration form with your details and click "Sign Up".
   - Your details will be saved in the database.

2. **Login**:
   - Use the login form to enter your email and password.
   - If the credentials are correct, you will be redirected to the homepage.

3. **Logout**:
   - Click the "Logout" button on the homepage to end your session.

---

## Troubleshooting

- **"Not Found" Error**:
  - Ensure all files are in the `c:\xampp\htdocs\Login` directory.
  - Verify that `homepage.php` exists in the same directory as `index.php`.

- **Database Connection Issues**:
  - Check the database credentials in `connect.php`.
  - Ensure the MySQL server is running in XAMPP.

- **Session Issues**:
  - Ensure `session_start()` is called at the top of every PHP file that uses sessions.
  - Verify that the `session.save_path` in `php.ini` is correctly set (e.g., `C:\xampp\tmp`).

---

## Future Improvements

- Add email validation during registration.
- Implement password recovery functionality.
- Use `PDO` or `MySQLi` prepared statements to prevent SQL injection.
- Improve the UI/UX with modern frameworks like Bootstrap.

---

## License

This project is open-source and free to use. Feel free to modify and distribute it as needed.

---

## Author

- Oliveira Hambaena
- Contact: [ollymeansoliveira@gmail.com]

