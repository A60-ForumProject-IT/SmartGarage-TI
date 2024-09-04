# SmartGarage-TI

## Description
**SmartGarage-TI** is a web application designed to manage a car repair garage, built with Java and Spring Boot. The project offers functionalities for managing vehicles, clients, services, and scheduling, aiming to streamline daily operations and improve the efficiency of automotive service centers.

## Features
- Manage clients and their vehicles.
- Add, edit, and delete services offered by the garage.
- Create and manage schedules and appointments.
- **User Registration and Password Recovery**: Automatically send an email upon user registration and when a user requests a password reset.
- **Profile Picture Management**: Integrates with Cloudinary to allow users to change their profile picture.
- **Service Summary PDF**: After a service is completed, a PDF file containing the services performed and the amount due (in the user's preferred currency) is sent to the user via email.
- **Contact Us Form**: Allows users to send inquiries via email by submitting their contact information and message through a contact form.
- Integration with a database to store all relevant data.
- Currency exchange rate integration using a third-party API.

## Installation

To successfully run the application, follow these steps:

1. **Clone the repository:**
    ```bash
    git clone https://github.com/IHKaragyozov19/SmartGarage-TI.git
    cd SmartGarage-TI
    ```

2. **Ensure Java and Gradle are installed:**
    - Make sure you have **Java 17** installed and set as the default JDK.
    - Install **Gradle** if you haven't already. You can check if Gradle is installed by running:
    ```bash
    gradle -v
    ```

    If Gradle is not installed, you can follow the [official Gradle installation guide](https://gradle.org/install/).

3. **Set up the database:**
    - Install **MariaDB** and create a new database named `smart_garage`.
    - Update the `application.properties` file with your local MariaDB credentials:
    ```properties
    database.url=jdbc:mariadb://localhost:3306/smart_garage
    database.username=yourDatabaseUsername
    database.password=yourDatabasePassword
    ```

4. **Configure the application:**
    - Make sure the necessary configurations in the `application.properties` file are correct (e.g., email settings, Cloudinary credentials, and API keys).

5. **Build the application:**
    - Navigate to the root directory of the project and run:
    ```bash
    ./gradlew clean build
    ```

6. **Run the application:**
    - After a successful build, run the following command to start the application:
    ```bash
    ./gradlew bootRun
    ```

7. **Access the application:**
    - Open your web browser and go to `http://localhost:8080/ti` to start using the application.

## Configuration

- **Email Configuration:**
    Make sure to set up your SMTP settings in `application.properties` to enable email functionalities:
    ```properties
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=ikaragyozov19@gmail.com
    spring.mail.password=xpve hqtn hfcc llwg
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
    ```

- **Cloudinary Configuration:**
    Ensure Cloudinary credentials are correctly set in the `CloudinaryConfig` class:
    ```java
    @Configuration
    public class CloudinaryConfig {
        @Bean
        public Cloudinary cloudinary() {
            Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "dd0gbtqw9",
                "api_key", "977948938517315",
                "api_secret", "5tIa3dIcyTn2vn5_kpOOSyFAFb4",
                "secure", true
            );
            return new Cloudinary(config);
        }
    }
    ```

- **Exchange Rate API Configuration:**
    Set up the API URL and access key for currency conversion:
    ```properties
    exchangerate.api.url=https://api.exchangeratesapi.io/latest
    exchangerate.api.key=15af8b7a60feabd0ba0a9e0106d57147
    ```

## Usage
Once the application is running, access it in your web browser at `http://localhost:8080/ti`. You can start managing clients, vehicles, and services directly from the user interface based on the role of the User (Employee or Customer).

## Technologies
- **Java 17**
- **Spring Boot 3.3.2**
- **Spring MVC**
- **Hibernate**
- **Java Persistence API (JPA)**
- **Thymeleaf**
- **MariaDB**
- **RESTful API**
- **HTML/CSS**
- **Mockito** for unit testing
- **Cloudinary API** for image management
- **Exchange rates API** for currency conversion
- **Gradle** for build automation

## Contributing
Contributions are welcome! Please fork this repository, create a feature branch, and submit a pull request for review.

## License
This project is licensed under the [MIT License](LICENSE).

## Contact
For any questions or issues, feel free to reach out at [ikaragyozov19@gmail.com](mailto:ikaragyozov19@gmail.com).
