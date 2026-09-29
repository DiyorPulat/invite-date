# Invite Date API

Spring Boot 4 / Java 21 backend for interactive date invitations. PostgreSQL stores invitations and Liquibase creates the schema; ZXing creates PNG QR codes.

## Requirements

- Java 21
- Docker and Docker Compose

## Run locally

```bash
cp .env.example .env
docker compose up -d
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`. The repository does not contain
real credentials; keep local values in `.env` or environment variables.

Useful environment variables: `DB_URL`, `PG_HOST`, `PG_PORT`, `PG_DATABASE`, `PG_USERNAME`, `PG_PASSWORD`, `PUBLIC_BASE_URL`, and `CORS_ALLOWED_ORIGINS` (comma-separated or `*`). Set `PUBLIC_BASE_URL` to the frontend domain in production, e.g. `https://example.uz`; QR codes point to `{PUBLIC_BASE_URL}/invite/{id}`. CORS credentials are disabled, so all origins can be allowed with `CORS_ALLOWED_ORIGINS=*`.

## Email + PDF notifications

The creator enters the email address that should receive a response in the invitation form. When the invitee answers, the backend sends the selected date/activity details and attaches a PDF ticket. Configure an SMTP account before enabling it:

```bash
MAIL_ENABLED=true
MAIL_FROM=your@gmail.com
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your@gmail.com
SPRING_MAIL_PASSWORD=your-google-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

For Gmail, use a Google **App Password** (not your ordinary Gmail password). With `MAIL_ENABLED=false` the rest of the application still works, but no email is sent.

## API

- `POST /api/v1/invitations` creates an invitation and returns its public URL plus a PNG base64 data URL.
- `GET /api/v1/invitations/{id}` returns public invitation data and atomically increments `viewCount` in the transaction.
- `GET /api/v1/invitations/{id}/qr` returns a downloadable `image/png` QR code.
- `POST /api/v1/invitations/{id}/respond` accepts only `ACCEPTED` or `DECLINED`; an answered invitation cannot be changed.
- `GET /api/v1/invitations/{id}/status` returns status, response details, views and update time for polling.

Example creation request:

```json
{
  "senderName": "Aziz",
  "receiverName": "Madina",
  "dateType": "coffee",
  "proposedDate": "2026-10-04T19:00:00+05:00",
  "customMessage": "Kechqurun birga qahva ichamizmi?"
}
```

Example response request:

```json
{
  "status": "ACCEPTED",
  "responseDetails": {
    "preferredTime": "19:00",
    "preference": "Cappuccino",
    "music": "Jazz"
  }
}
```

`dateType` values: `coffee`, `dinner`, `walk`, `movie`. Invalid or absent IDs return `404`; invalid request data returns `400`; a second answer returns `409`.

For a public launch, protect the `/status` endpoint with sender authentication or a separately stored owner token. A UUID prevents guessing but does not by itself establish sender identity.
# invite-date
