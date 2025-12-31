# 01Blog - Social Learning Platform

A fullstack social blogging platform where students can share their learning experiences, follow others, interact with posts, and engage in meaningful discussions. Admins can moderate content and manage users.

---

## Table of Contents

* [Features](#features)
* [Technologies Used](#technologies-used)
* [Project Structure](#project-structure)
* [Setup Instructions](#setup-instructions)

  * [Backend](#backend)
  * [Frontend](#frontend)
* [Usage](#usage)
* [Evaluation](#evaluation)

---

## Features

### User Features

* User registration, login, and secure password handling
* Role-based access control (user vs admin)
* Personal profile ("block") with list of posts
* Subscribe/unsubscribe to other users
* Create, edit, delete posts with media (images/videos) and text
* Like and comment on posts
* Report inappropriate content with reason and timestamp

### Admin Features

* View and manage all users
* Manage posts and remove inappropriate content
* Handle user reports (ban/delete user or post)
* Protected routes based on role access

### Frontend Features

* Homepage feed with posts from subscribed users
* Personal block page with full post CRUD
* Real-time or refresh-based comments
* Media uploads with previews
* Notification system (mark read/unread)
* Reporting UI with confirmation modal
* Admin dashboard with clean moderation interface

---

## Technologies Used

### Backend

* Java 17
* Spring Boot
* Spring Security / JWT
* Spring Data JPA
* PostgreSQL or MySQL
* REST API design

### Frontend

* Angular 17+
* Angular Material or Bootstrap (responsive UI)
* RxJS for reactive programming

### Other

* Git & GitHub for version control
* Maven or Gradle for dependency management

---

## Project Structure

```
blogtest/             # Spring Boot backend
my-angular-app/       # Angular frontend
uploads/              # Uploaded media files
README.md             # Project README
```

---

## Setup Instructions

### Backend

1. Navigate to backend folder:

   ```bash
   cd blogtest
   ```
2. Install dependencies:

   ```bash
   mvn install
   ```
3. Configure database in `src/main/resources/application.properties`
4. Run the Spring Boot server:

   ```bash
   mvn spring-boot:run
   ```

   * Default port: `http://localhost:8080`

### Frontend

1. Navigate to frontend folder:

   ```bash
   cd my-angular-app
   ```
2. Install dependencies:

   ```bash
   npm install
   ```
3. Run Angular dev server:

   ```bash
   ng serve
   ```

   * Default port: `http://localhost:4200`

---

## Usage

* Open the frontend in your browser: `http://localhost:4200`
* Register a new user or login
* Explore features: create posts, like/comment, subscribe to users
* Admins can manage users and moderate content through the dashboard

---

## Evaluation

The project is evaluated based on:

* 💡 **Functionality**: All features implemented and working as expected
* 🔐 **Security**: Proper role-based access and secure user data handling
* 🎨 **UI/UX**: Responsive, intuitive, and clean interface
