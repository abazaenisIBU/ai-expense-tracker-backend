# AI Expense Tracker

**AI Expense Tracker** is a comprehensive solution for tracking and analyzing expenses using AI-powered categorization and reporting. This backend application provides APIs for managing users, expenses, categories, reports, and statistics with features like email notifications, global error handling, and asynchronous request processing.

## Features

- **User Management:** CRUD operations for users.
- **Expense Tracking:** Add, update, delete, and retrieve expenses by user or date range.
- **Category Management:** Manage categories for expense classification.
- **AI Integration:** Suggest categories based on expense descriptions using OpenAI.
- **Statistics & Reports:** Generate statistics by category and month and send periodic reports via email.
- **Global Error Handling:** Unified error responses with meaningful messages and HTTP status codes.
- **Asynchronous APIs:** Use of `CompletableFuture` for improved API performance.
- **Email Notifications:** Send automated reports using SendGrid.

---

## Technologies Used

- **Language:** Java 
- **Framework:** Spring Boot
- **Database:** PostgreSQL
- **API:** OpenAI for AI categorization
- **Email Service:** SendGrid
- **Dependency Management:** Maven
- **Build Tool:** Gradle (optional)
- **Version Control:** Git/GitHub

---

## Setup and Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/abazaenisIBU/ai-expense-tracker.git
   cd ai-expense-tracker
   ```

2. **Configure the database:**
    - Create a PostgreSQL database named `ai_expense_tracker`.
    - Update connection details in `application.properties`.

3. **Set environment variables:**
   Define the required environment variables (see below).

4. **Build the project:**
   ```bash
   mvn clean install
   ```

5. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

6. **Access the API:**
   Navigate to `http://localhost:8080` to access the API.

---

## Environment Variables

| Variable                | Description                              |
|-------------------------|------------------------------------------|
| `SPRING_DATASOURCE_URL` | Database connection URL                 |
| `SPRING_DATASOURCE_USER`| Database username                       |
| `SPRING_DATASOURCE_PASS`| Database password                       |
| `API_KEY`               | API key for secure endpoints            |
| `SENDGRID_API_KEY`      | API key for SendGrid email integration   |

---

## Endpoints

### User Management
- **Create User:** `POST /api/users`
- **Update User:** `PUT /api/users/{id}`
- **Delete User:** `DELETE /api/users/{id}`
- **Get User:** `GET /api/users/{id}`

### Expense Management
- **Create Expense:** `POST /api/expenses/user/{email}`
- **Update Expense:** `PUT /api/expenses/user/{email}/{id}`
- **Delete Expense:** `DELETE /api/expenses/user/{email}/{id}`
- **Get Expenses:** `GET /api/expenses/user/{email}`

### Category Management
- **Create Category:** `POST /api/categories/user/{email}`
- **Update Category:** `PUT /api/categories/user/{email}/{id}`
- **Delete Category:** `DELETE /api/categories/user/{email}/{id}`
- **Get Categories:** `GET /api/categories/user/{email}`

### Reports
- **Monthly Reports:** `GET /api/reports/monthly`
- **Weekly Reports:** `GET /api/reports/weekly`

### Statistics
- **Get Statistics:** `GET /api/statistics?email={email}`

---

## Architecture

- **Controllers:** Handle HTTP requests and return responses.
- **Services:** Business logic for the application.
- **Repositories:** Data access layer for interacting with the database.
- **DTOs:** Data Transfer Objects for request/response payloads.
- **Global Exception Handling:** Centralized error handling for all exceptions.

---

## Error Handling

**Global Error Handling** ensures consistent error responses. Example response:
```json
{
  "status": 404,
  "error": "User Not Found",
  "details": [
    {
      "field": null,
      "message": "User with email enis.abaza@example.com not found."
    }
  ],
  "timestamp": "2024-12-26T01:33:14.4400561"
}
```

**Custom Exceptions:**
- `UserNotFoundException`
- `CategoryNotFoundException`
- `ExpenseNotFoundException`
- `ExpenseOwnershipException`

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

