# Quiz Application Setup Guide

This guide provides instructions for setting up the backend and frontend of the Quiz Application. The backend is built with Spring Boot, and the frontend is developed using React.

## Prerequisites

Before you start, ensure you have the following installed:
- JDK 11 or newer
- Node.js and npm
- pgAdmin 4

## Database Setup

Follow these steps to set up your PostgreSQL database:

1. Download and install pgAdmin 4 from [pgAdmin Download](https://www.pgadmin.org/download/).
2. Open pgAdmin and connect using the username `postgres` and password `root`.
3. Create a new database named `live_quiz`.

## Backend Setup

To set up the Spring Boot backend:

1. Navigate to the backend directory:
   ```bash
   cd backend
2. Build the project using Maven:
   ```bash
   ./mvnw clean install
3. Start the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run

## Frontend Setup

To set up the React Frontend:

1. Navigate to the frontend directory:
   ```bash
   cd frontend
2. Install the required npm packages:
   ```bash
   npm install
3. Start the React application:
   ```bash
   npm start

## Usage

With both the backend and frontend running, you can now access the Quiz Application through your browser at http://localhost:3000. Here, you can register as a user, create quizzes, and participate in real-time interactions.
