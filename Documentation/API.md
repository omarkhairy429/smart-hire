# SmartHire — API Reference

Base URL (local): `http://localhost:8080`



## Authentication

Every endpoint needs a JWT **except** the ones marked _public_ below.

Get a token from `POST /api/auth/login`, then send it on every request:

```
Authorization: Bearer <token>
```

A missing or invalid token gives **403**. A valid token whose role isn't allowed
also gives **403**.

## Common status codes

| Code | Meaning |
|---|---|
| 200 | OK |
| 201 | Created |
| 204 | Done, nothing to return (used by delete) |
| 400 | Validation failed — a required field is missing or out of range |
| 403 | Not logged in, or your role isn't allowed to do this |
| 404 | The thing you asked for doesn't exist |
| 409 | Conflict — e.g. applying twice to the same posting |

Errors come back as:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Interview not found",
  "timestamp": "2026-09-12T14:30:00"
}
```

## Enums

| Enum | Values |
|---|---|
| `UserRole` | SUPER_ADMIN, HR_MANAGER, INTERVIEWER, CANDIDATE |
| `PostingStatus` | DRAFT, PUBLISHED, CLOSED |
| `LocationType` | REMOTE, HYBRID, ON_SITE |
| `EmploymentType` | FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE |
| `ApplicationStage` | APPLIED, SCREENING, INTERVIEW, OFFERED, HIRED, REJECTED |
| `ApplicationStatus` | IN_REVIEW, HIRED, REJECTED |
| `InterviewFormat` | IN_PERSON, VIDEO, PHONE |
| `FeedbackRecommendation` | PROCEED, HOLD, REJECT |
| `NotificationType` | APPLICATION_SUBMITTED, APPLICATION_STAGE_CHANGED, INTERVIEW_SCHEDULED, INTERVIEW_CANCELLED, FEEDBACK_SUBMITTED |

---

# Auth — `/api/auth`

_All public._

### `POST /api/auth/register`
Register a candidate account.

```json
{ "firstName": "Youssef", "lastName": "Ibrahim",
  "email": "youssef@example.com", "password": "Passw0rd@1", "role": "CANDIDATE" }
```

Returns an `AuthResponse`: `{ token, role, email, firstName }`

### `POST /api/auth/login`
```json
{ "email": "youssef@example.com", "password": "Passw0rd@1" }
```
Returns the same `AuthResponse`.

### `POST /api/auth/forgot-password`
`{ "email": "..." }` — emails a reset link. Always returns 200 so the response
can't be used to discover which emails are registered.

### `POST /api/auth/reset-password`
Takes the token from the email plus the new password.

---

# Job postings — `/api/postings`

Reading postings is public. Creating and editing requires HR_MANAGER or SUPER_ADMIN.

| Method | Path | Role | Description |
|---|---|---|---|
| GET | `/api/postings` | public | All postings |
| GET | `/api/postings/published` | public | Published postings only |
| GET | `/api/postings/{id}` | public | One posting |
| GET | `/api/postings/published/{id}` | public | One published posting |
| GET | `/api/postings/company` | authenticated | Postings for the caller's company |
| GET | `/api/postings/mine` | HR, Admin | Postings the caller created |
| POST | `/api/postings` | HR, Admin | Create a published posting |
| PUT | `/api/postings/{id}` | HR, Admin | Update a posting |
| DELETE | `/api/postings/{id}` | HR, Admin | Delete a posting |
| POST | `/api/postings/drafts` | HR, Admin | Save a draft (fields may be incomplete) |
| PUT | `/api/postings/drafts/{id}` | HR | Update a draft |
| PATCH | `/api/postings/{id}/publish` | HR, Admin | Publish a draft |
| PATCH | `/api/postings/{id}/close` | HR, Admin | Close an active posting |

**Create / update body**

```json
{ "title": "Backend Developer",
  "company": "Orange Digital Center",
  "description": "Build and maintain Spring Boot services.",
  "skillsRequired": ["Java", "Spring Boot", "PostgreSQL"],
  "locationType": "HYBRID",
  "location": "Cairo",
  "department": "Engineering",
  "employmentType": "FULL_TIME",
  "deadline": "2026-12-31" }
```

### Applications for a posting

| Method | Path | Role |
|---|---|---|
| GET | `/api/postings/{postingId}/applications` | HR, Admin |
| GET | `/api/postings/{postingId}/applications/export` | HR, Admin |
| GET | `/api/postings/{postingId}/pipeline` | HR, Admin |

Both application endpoints accept the same query parameters:

- `stage` — filter by `ApplicationStage` (optional)
- `sort` — field to sort by (default `createdAt`)
- `dir` — `asc` or `desc` (default `asc`)

`/export` returns **CSV** (`text/csv`) instead of JSON.
`/pipeline` returns applications grouped by stage, for the Kanban board.

---

# Public job search — `/api/public/postings`

### `GET /api/public/postings` — _public_

Search published postings. All parameters optional:

`keyword`, `location`, `locationType`, `company`

---

# Applications — `/api/applications`

| Method | Path | Role | Description |
|---|---|---|---|
| POST | `/api/applications` | CANDIDATE | Apply to a posting |
| GET | `/api/applications` | CANDIDATE | The caller's own applications |
| GET | `/api/applications/{id}` | HR, Admin | One application |
| GET | `/api/applications/posting/{postingId}` | HR, Admin | Applications for a posting |
| PATCH | `/api/applications/{applicationId}/stage` | HR, Admin | Move to another stage |

**Apply**

```json
{ "postingId": "uuid", "coverLetter": "...", "experienceSummary": "...",
  "resumeUrl": "https://drive.example.com/cv.pdf" }
```

A candidate can only apply once per posting — a second attempt is rejected.
New applications start at stage `APPLIED` and status `IN_REVIEW`.

**Change stage** — `{ "stage": "SCREENING" }`

---

# Interviews

Scheduling and cancelling is done by HR.

| Method | Path | Role | Description |
|---|---|---|---|
| GET | `/api/users/interviewers` | HR, Admin | Interviewers who can be assigned (scoped to the caller's company) |
| POST | `/api/applications/{applicationId}/interviews` | HR, Admin | Schedule an interview |
| GET | `/api/applications/{applicationId}/interviews` | HR, Admin | Interviews for an application |
| DELETE | `/api/interviews/{interviewId}` | HR, Admin | Cancel an interview |
| GET | `/api/interviews/{interviewId}/feedback` | HR, Admin | All feedback left on an interview |

**Schedule an interview**

```json
{ "interviewerId": "uuid",
  "scheduledAt": "2026-09-15T14:30:00",
  "format": "VIDEO",
  "meetingLink": "https://meet.google.com/abc-defg-hij",
  "location": null }
```

- `format` is required.
- `meetingLink` is **required** for `VIDEO` and `PHONE`, and ignored for `IN_PERSON`.
- `location` is used for `IN_PERSON` interviews.
- The chosen user must have the `INTERVIEWER` role, otherwise **400**.
- Scheduling moves the application to stage `INTERVIEW` — unless it has already
  reached `OFFERED`, `HIRED` or `REJECTED`, which are left alone.
- The candidate and the interviewer both get a notification.

**Cancel** returns **204**. The interview row is deleted, and both the candidate
and the interviewer are notified. The application's stage is not changed.

---

# Interviewer portal — `/api/interviewer`

_INTERVIEWER only._ An interviewer can only see and touch interviews assigned to
them — anything else returns **403**, even with a valid interviewer token.

| Method | Path | Description |
|---|---|---|
| GET | `/api/interviewer/my-interviews` | Upcoming interviews assigned to the caller, earliest first |
| GET | `/api/interviewer/interviews/{id}/dossier` | Candidate info, CV link and job requirements |
| POST | `/api/interviewer/interviews/{id}/feedback` | Submit or update feedback |
| GET | `/api/interviewer/interviews/{id}/feedback` | The caller's own feedback for that interview |

Past interviews are not returned by `my-interviews`.

**Dossier** returns the interview time, meeting link or location, the candidate's
name, email, CV link, cover letter and experience summary, plus the job title,
description and required skills.

**Submit feedback**

```json
{ "rating": 4,
  "technicalScore": 5,
  "communicationScore": 3,
  "recommendation": "PROCEED",
  "comments": "Strong Spring Boot knowledge." }
```

- `rating` and `recommendation` are required. All three scores must be **1–5**.
- One feedback per interviewer per interview. Submitting again **updates** the
  existing feedback rather than failing.
- The HR manager who owns the posting is notified. The candidate is **not** —
  candidates never see interview feedback.
- `GET` returns **404** if the caller hasn't left feedback yet.

---

# Candidate — `/api/candidate`

### `GET /api/candidate/my-interviews` — _CANDIDATE_

The caller's own interviews.

---

# Candidate notes — `/api/hr/notes`

_HR_MANAGER or SUPER_ADMIN._ Internal notes about a candidate; candidates cannot see them.

| Method | Path | Description |
|---|---|---|
| POST | `/api/hr/notes` | Add a note — `{ "candidateId": "uuid", "content": "..." }` |
| GET | `/api/hr/notes/candidate/{candidateId}` | All notes for a candidate |

---

# Notifications — `/api/notifications`

_Any logged-in user._ Everyone sees only their own notifications.

| Method | Path | Description |
|---|---|---|
| GET | `/api/notifications` | The caller's notifications |
| GET | `/api/notifications/unread-count` | Number of unread ones — used for the bell badge |
| PATCH | `/api/notifications/{id}/read` | Mark one as read |
| PATCH | `/api/notifications/read-all` | Mark everything as read |

---

# Super admin — `/api/super-admin`

_SUPER_ADMIN only._

| Method | Path | Description |
|---|---|---|
| POST | `/api/super-admin/staff` | Create an HR manager or interviewer |
| GET | `/api/super-admin/staff` | List all staff |
| PATCH | `/api/super-admin/staff/{id}/deactivate` | Deactivate a staff account |
| PATCH | `/api/super-admin/staff/{id}/reactivate` | Reactivate a staff account |
| GET | `/api/super-admin/audit-logs` | Audit log, paginated |
| GET | `/api/super-admin/stats` | Platform totals |

**Create staff**

```json
{ "firstName": "Ahmed", "lastName": "Hassan",
  "email": "ahmed@example.com", "password": "Passw0rd@1",
  "role": "INTERVIEWER", "companyName": "Orange Digital Center" }
```

Only `HR_MANAGER` and `INTERVIEWER` can be created here. The temporary password
is emailed to the new member.

**Audit logs** accept `action` (optional filter), `page` (default 0) and `size`
(default 20), and return a paginated result.
