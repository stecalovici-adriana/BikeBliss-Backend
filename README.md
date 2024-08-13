# BikeBliss-Backend
The backend for the BikeBliss application, built using Java Spring Boot, manages all essential functionalities for the bike and equipment rental platform, catering to both urban environments and hiking adventures. Below are the main features provided by the backend:

Key Features
User Management: Supports user registration, login, password reset, and role management (Admin and User).
Bike and Equipment Management: Allows the addition, updating, and deletion of bike and equipment models, along with inventory management.
Rental Functionality: Handles creating, approving, canceling, and managing reservations for bikes and equipment. It also supports tracking the status of rentals (active, completed, etc.).
Reviews and Feedback: Enables users to provide reviews and feedback for equipment and bikes, which are visible to other users.
Security: Implements JWT-based authentication to protect API endpoints, with specific roles for users and administrators.
REST API: Exposes a RESTful API for frontend interaction, allowing complete CRUD operations for all entities managed by the application.

Technologies Used
Java Spring Boot: The primary framework used for backend development.
Spring Security: Provides authentication and authorization using JWT.
JPA/Hibernate: For object-relational mapping and database management.
MySQL: The database management system used to store application data.
