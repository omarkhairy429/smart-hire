# Smart Hire

A hiring management system built as a final project. It covers the full recruitment flow — from posting a job to scheduling interviews — with separate dashboards for HR, interviewers, candidates, and admins.

## What it does

- HR can create job postings, review applications, move candidates through stages, write notes, and export applicant data as CSV
- Candidates can browse open positions and apply with their resume
- Interviewers get assigned to interviews and can submit feedback
- Admins manage users and have access to audit logs
- Everyone gets in-app notifications for relevant updates

## Tech stack

**Backend** — Spring Boot, PostgreSQL, JWT auth, JavaMailSender  
**Frontend** — Angular  
**Infrastructure** — Docker + Docker Compose

## Running it locally

You need Docker installed.

1. Clone the repo
2. Copy `.env.example` to `.env` and fill in the values:

```
POSTGRES_DB=smarthire
POSTGRES_USER=smarthire
POSTGRES_PASSWORD=yourpassword
DB_PORT=5432
MAIL_USERNAME=youremail@gmail.com
MAIL_PASSWORD=your_app_password
```

> For `MAIL_PASSWORD`, use a Gmail App Password, not your actual Gmail password.

3. Start everything:

```
docker compose up --build
```

That is it. The app will be at http://localhost:4200 and the API at http://localhost:8080.

## Project structure

```
smart-hire/
├── BE/          # Spring Boot backend
├── FE/          # Angular frontend
└── docker-compose.yml
```
