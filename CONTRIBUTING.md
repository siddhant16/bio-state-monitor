# Contributing to Bio-State Fermentation Monitor

Thank you for considering contributing to the Bio-State Fermentation Monitor project! We welcome contributions from the community.

## Code of Conduct

Be respectful and inclusive in all interactions. We're committed to providing a welcoming and inspiring community for all.

## How to Contribute

### Reporting Bugs

- Use the GitHub issue tracker to report bugs
- Describe the bug clearly and provide steps to reproduce it
- Include your environment (OS, browser, Java version, Node version)
- Attach screenshots or logs if applicable

### Suggesting Enhancements

- Use the GitHub issue tracker for feature requests
- Clearly describe the enhancement and the problem it solves
- Provide examples of how this enhancement would be used

### Pull Requests

1. **Fork the repository** and create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the coding standards:
   - Use consistent formatting (see `.editorconfig`)
   - Write clear, descriptive commit messages
   - Add tests for new functionality

3. **Run tests and linting**:
   ```bash
   # Frontend
   cd frontend
   npm run lint
   npm run build
   npm test

   # Backend
   cd ../backend
   mvn clean package
   ```

4. **Commit your changes**:
   ```bash
   git commit -m "type: brief description

   Detailed explanation of the changes made."
   ```

5. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request** with:
   - Clear title and description
   - Reference to related issues (if any)
   - Explanation of changes and testing performed

## Development Setup

### Quick Start

```bash
# Install dependencies
npm install

# Start development servers
npm run dev

# Backend runs on http://localhost:8080
# Frontend runs on http://localhost:5173
```

### Using Docker

```bash
docker-compose up --build
```

### Environment Variables

Copy `.env.example` to `.env` and update with your configuration:

```bash
cp .env.example .env
# Edit .env with your GEMINI_API_KEY
```

## Coding Standards

### Frontend (React/JavaScript)

- Use ES6+ syntax
- Follow functional component patterns with hooks
- Use meaningful variable names
- Add comments for complex logic
- Write unit tests using Jest and React Testing Library

### Backend (Java)

- Follow Spring Boot conventions
- Use meaningful class and method names
- Add JavaDoc comments for public APIs
- Write unit tests using JUnit 5 and Mockito
- Keep methods focused and testable

### General

- Use `.editorconfig` for consistent formatting
- Write meaningful commit messages
- Keep commits atomic and focused
- Add tests for new features or bug fixes

## Testing Requirements

- Backend tests: `mvn test`
- Frontend tests: `npm test`
- All tests must pass before submitting PR
- Aim for >80% code coverage for new code

## Documentation

- Update README.md if you change how to set up or use the project
- Update ARCHITECTURE.md if you make significant architectural changes
- Add inline comments for complex logic
- Keep documentation up-to-date with code changes

## Commit Message Convention

Use the following format:

```
<type>: <subject>

<body>

<footer>
```

Where:
- **type**: feat, fix, docs, style, refactor, perf, test, chore
- **subject**: Brief summary (imperative mood, lowercase, no period)
- **body**: Detailed explanation (optional)
- **footer**: References to issues (optional, e.g., "Closes #123")

Example:
```
feat: add culture type selection for fermentation analysis

Implemented toggle buttons to switch between sourdough and kombucha
culture types. Users can now select the correct culture type before
capturing and analyzing fermentation samples.

Closes #42
```

## Questions?

Feel free to open an issue or reach out to the maintainers. We're here to help!

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
