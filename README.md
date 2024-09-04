# Smart Garage TI

Smart Garage TI is a web application designed to manage a car service shop. It allows employees to manage customer profiles, vehicle information, and service records. The application also provides functionalities for user authentication, password management, and role-based access control.

## Features

- **User Management**: Create, update, and delete user profiles.
- **Role-Based Access Control**: Different functionalities for employees and customers.
- **Vehicle Management**: Manage vehicle information and service records.
- **Authentication**: Secure login and registration system.
- **Password Management**: Reset and change passwords.
- **Email Notifications**: Send email notifications for various actions.

## Technologies Used

- **Backend**: Java, Spring Boot
- **Frontend**: HTML, CSS, JavaScript
- **Database**: SQL
- **Build Tool**: Gradle
- **Testing**: JUnit, Mockito

## Project Structure

- `src/main/java/com/telerikacademy/web/smartgarageti` - Main application code
  - `controllers` - MVC controllers
  - `models` - Data models and DTOs
  - `repositories` - Data access layer
  - `services` - Business logic
  - `helpers` - Utility classes
  - `exceptions` - Custom exceptions
- `src/main/resources/templates` - HTML templates
- `src/main/resources/static` - Static resources (CSS, JS)
- `src/test/java/com/telerikacademy/web/smartgarageti` - Unit tests

## Getting Started

### Prerequisites

- Java 11 or higher
- Gradle
- A SQL database (e.g., MySQL, PostgreSQL)

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/IHKaragyozov19/smart-garage-ti.git
   cd smart-garage-ti
