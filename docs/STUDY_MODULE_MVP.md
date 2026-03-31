# Study Module MVP

## Endpoints

- `POST /study/start`
- `POST /study/generate-questions`
- `GET /study/{id}`

## Request examples

```json
POST /study/start
{
  "topic": "Spring Security",
  "audioFile": "optional-audio-reference"
}
```

```json
POST /study/generate-questions
{
  "studySessionId": "00000000-0000-0000-0000-000000000000"
}
```

## Notes

- The current implementation uses deterministic stub generation.
- The endpoint requires JWT authentication.
- Questions are regenerated each time `generate-questions` is called for the same study session.

