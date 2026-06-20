🏨 Hotel Reservation System

A full-stack Hotel Reservation System built using Spring Boot, MySQL, HTML, CSS, and JavaScript. This project allows users to browse rooms, make reservations, and simulate a payment process.

🚀 Features
User room booking system
Room availability management
Booking confirmation system
Mock payment integration (bKash/Card simulation)
Admin backend support (Spring Boot)
Responsive frontend UI
🛠️ Tech Stack

Backend:

Java Spring Boot
Spring Data JPA
MySQL
Maven

Frontend:

HTML
CSS
JavaScript
📂 Project Structure
hotel-reservation-system/
├── src/
├── frontend/
├── pom.xml
├── mvnw
├── Dockerfile
└── README.md
⚙️ How to Run Locally
1. Clone the repository
git clone https://github.com/ProTiger24/hotel-reservation-system.git
2. Move to project directory
cd hotel-reservation-system
3. Configure Database

Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db
spring.datasource.username=root
spring.datasource.password=your_password
server.port=8080
4. Run the project
./mvnw spring-boot:run
💳 Payment System

This project includes a mock payment system for:

bKash simulation
Card payment simulation

(No real transaction is processed)


👨‍💻 Developer
Developed by: Abdul Alim
Role: Full Stack Developer 
📌 Note
This is a student project for learning full-stack web development using Spring Boot and frontend technologies.
