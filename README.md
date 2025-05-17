# Auth Service for GuessingGame

This service is dedicated to generating and validating JSON Web Tokens (JWT) for seamless user authentication.

## Features

- **Responsibility for User Authentication:** Our Auth service is exclusively responsible for handling the generation
  and validation of JWT tokens, ensuring that only authenticated users can access our game's features.
- **Token Types:** We use both access tokens and refresh tokens to provide a robust authentication mechanism. The
  refresh token acts as a means to obtain new access and refresh tokens, enhancing user convenience without requiring
  re-authentication.
- **Token Integrity:** The integrity of both access and refresh tokens is ensured through validation by our service.
  This includes checking the token's signature, ensuring it was issued by us, and hasn't been tampered with.
- **Preventing Abuse:** To safeguard against abuse, rate limiting is implemented on our service to limit the number of
  token requests for a specific client.

