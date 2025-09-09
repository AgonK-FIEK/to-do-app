# To-Do Android App

Android task management application.

## Features

### Authentication
- User Registration
- Login System  
- Password Recovery
- Two-Factor Authentication (Email)

### Core Functionality
- Create, Read, Update, Delete Tasks
- Task Completion Tracking
- Real-time Task Updates

### Technical Features
- Secure Password Hashing (BCrypt)
- Input Validation (Email & Password Complexity)
- Error Handling with User Feedback
- Welcome Notifications on Login/Register
- Splash Screen Animations
- Responsive Design for Multiple Screen Sizes

## Technical Stack

- **Language:** Kotlin
- **Min SDK:** API 24 (Android 7.0)
- **Target SDK:** API 35 (Android 15)
- **Database:** Room
- **Architecture:** MVVM Pattern
- **Dependency Injection:** Hilt
- **Async:** Coroutines

## Installation

1. Clone repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on emulator or device

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/taskmaster/
│       │   ├── data/
│       │   ├── domain/
│       │   ├── presentation/
│       │   └── utils/
│       └── res/
└── build.gradle.kts
```

## Contributors

University Project (Programimi ne paisje mobile) - FIEK

## License

Educational Purpose Only