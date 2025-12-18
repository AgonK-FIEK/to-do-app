# TaskMaster - Android To-Do App

Task management Android application for "Programimi ne Paisje Mobile" course at FIEK.

## Project Requirements

1. **Sign In** - Authentication system
2. **Sign Up** - User registration with validation
3. **Forget Password** - Email-based password reset with verification codes
4. **DB Implementation** - Room database with User, Task, and PasswordReset entities
5. **Animation** - Splash screen with fade/slide animations
6. **Input Validation** - Password complexity (8+ chars, uppercase, lowercase, digit, special char)
7. **Error Handling** - User-friendly error messages
8. **CRUD Implementation** - Create, Read, Update, Delete for tasks
9. **Minimum 3 Screens** - 8 screens implemented
10. **Responsive Design** - Phone and tablet layouts (sw600dp)
11. **Password Hashing** - BCrypt implementation with salt
12. **Notifications** - Welcome and task reminder notifications (Android 13+ support)
13. **2FA (BONUS)** - Email-based two-factor authentication

## Features

### Authentication & Security
- User Registration with email validation
- Secure Login with session management
- Password Reset with email verification codes
- Two-Factor Authentication (2FA) with email codes
- Encrypted session storage (EncryptedSharedPreferences)
- BCrypt password hashing
- Database-backed password reset with attempt limiting

### Task Management
- Create tasks with title and description
- Mark tasks as complete/incomplete
- Edit task details
- Delete tasks
- Real-time updates with Kotlin Flow
- Task persistence with Room database

### User Experience
- Custom TM logo and branding
- Splash screen with animations
- Material Design components
- Responsive layouts for phones and tablets
- Bottom navigation for easy access
- Input validation with real-time feedback
- Push notifications with runtime permissions

## Technical Stack

- **Language:** Kotlin
- **Min SDK:** API 24 (Android 7.0)
- **Target SDK:** API 35 (Android 15)
- **Database:** Room with Migrations (v3)
- **Architecture:** MVVM Pattern
- **Dependency Injection:** Hilt
- **Async:** Coroutines & Flow
- **Security:** BCrypt, EncryptedSharedPreferences
- **Email:** JavaMail SMTP
- **UI:** Material Design Components

## Installation

1. Clone repository
2. Open in Android Studio
3. Configure email credentials (see Email Setup below)
4. Sync project with Gradle files
5. Run on emulator or device

## Email Setup

The app uses email for password reset and 2FA verification. To enable these features:

1. Open `local.properties` file in the project root
2. Add your email credentials:
   ```
   EMAIL_USERNAME=your_email@gmail.com
   EMAIL_PASSWORD=your_app_password_here
   ```

### Getting a Gmail App Password

1. Go to your Google Account settings
2. Enable 2-Step Verification
3. Navigate to Security → 2-Step Verification → App passwords
4. Generate an app-specific password for "Mail"
5. Use the 16-character password (without spaces)

**Note:** The `local.properties` file is gitignored and will not be committed to version control.

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