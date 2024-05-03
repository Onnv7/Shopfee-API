# 1. Introduction
The Shopfee API is a backend system that handles functionalities/requests to serve the Shopfee food ordering software system using Spring Boot.

# 2. System Requirements
- Java version 17, using Maven for package management
- Docker is used to run other service applications such as database, message queue, cache, etc.
- application-dev.yml or application-prod.yml file with the required parameters in the application.yml file, both files are located in the /src/main/resources directory
- Firebase admin authentication json file from Firebase service, the file is located in the /src/main/resources directory
- Note: The project uses webhooks from third parties such as ZaloPay, VNPay. So if you want to use that function, you need to configure the webhook url (https) for ZaloPay in the application file and for VNPay in the VNPay portal provided for testing.
# 3. How to Use
- Step 1: Start docker and open a terminal in the project root directory.
- Step 2: Run the command "docker compose up -d" to start the service applications.
- Step 3: Run the terminal command "mvn install -DskipTests" to load the packages defined in the pom.xml file.
- Step 4: Run the terminal command "mvn spring-boot:run" or press Shift + F10 on IntelliJ to run the project.

# 4. Usage
Access http://localhost:8080/swagger-ui/index.html to test the API on localhost.
Some APIs require a token, so you need to log in to get a token.

# 5. Other related information
- Mobile project using Shopfee API: [application for employees](https://github.com/ducdevday/Shopfee_For_Employee_Flutter), [application for users](https://github.com/ducdevday/Shopfee_Flutter)
- Web project using Shopfee API:  [website for users](https://github.com/nguyendinhhieu12345/Drink_Store), [website for administrators](https://github.com/nguyendinhhieu12345/Drinks-Frontend)

# 6. Contact Information
Email: nva6112002@gmail.com
