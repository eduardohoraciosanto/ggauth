# ggauth Project

This is a simple game server for a Guessing Game.

Rules of the game are as follows:

- 2 Players needed
- Both players chose a 2-4-6 digit number
- Players take turn and guess the opponent's 2-4-6 digit number
    - Opponent has to say how many numbers are in the correct position, but not which
- Game continues until one Player correctly guesses the opponent's 2-4-6 digit number

Amount of digits depends on the difficulty

- EASY -> 2 Digit
- NORMAL -> 4 Digit
- HARD -> 6 Digit

Players will connect to the server using the Client Application.

Players can choose to play against the server.

Server will use random numbers when playing against the player at first, we can improve it's intelligence later.

## Technologies Used

- **Java**: Programming language used for the project.
- **Spring Boot**: Framework used to build the application, providing auto-configuration and simplifying development.
- **Maven/Gradle**: Build management tools for managing dependencies and building the project.

## Dependencies

- Spring Boot Starter Web: For building web applications, including RESTful APIs.
- Other dependencies may be included based on project requirements.

This README provides a brief overview of the project and the technologies used. Additional details can be added as
needed.
