# EchoJournal - Modern Android Audio Journal App

EchoJournal is a colorful and intuitive audio journaling application developed as part of the [Mobile Dev Campus Monthly Challenge](https://pl-coding.com/campus/) by Philipp Lackner for January of 2025. It demonstrates modern Android development practices and architecture while providing a seamless voice memo journaling experience.

## Features

- 🎙️ Quick and easy voice memo recording
- 🎨 Mood-based color coding for entries
- 🏷️ Customizable topic tagging system
- 📅 Smart date-based organization
- 📝 Optional text descriptions for voice memos
- 🔍 Advanced filtering by mood and topics
- 🤖 AI-powered voice transcription
- 📱 Home screen widget for quick recording
- ⚡ Gesture-based recording interface

## Technical Highlights

This project showcases modern Android development practices and technologies:

### Architecture & Design Patterns
- Clean Architecture principles
- MVVM pattern with UI States
- Repository pattern for data management
- Single Activity architecture

### Android Jetpack
- **Compose**: Modern declarative UI with gesture support
- **Room**: Local database for memo storage
- **Hilt**: Dependency injection
- **DataStore**: User preferences storage
- **Navigation**: Single Activity navigation
- **Media3**: Audio playback and recording
- **Glance**: Home screen widget implementation

### Other Technologies & Libraries
- **Kotlin**: 100% Kotlin codebase with coroutines and flows
- **Material Design 3**: Modern and consistent UI/UX
- **Version Catalog**: Dependency management
- **AudioVisualizer**: Real-time audio amplitude visualization

## Implementation Details

### Audio Recording & Playback
- Efficient audio recording with amplitude tracking
- Format preservation for high-quality audio
- Smart handling of audio focus and interruptions

### UI Features
- Bottom sheet implementation for quick recording
- Gesture-based recording with haptic feedback
- Dynamic audio visualization
- Smart date grouping for entries
- Material Design 3 theming
- Expandable text descriptions

### Data Management
- Room database for memo storage
- Efficient audio file management
- User preferences for default mood and topics
- Content provider integration for audio files

## Building The Project

1. Clone the repository
```bash
git clone https://github.com/BLTuckerDev/EchoJournal.git
```

2. Open the project in Android Studio (latest version recommended)

3. Build and run the project

## Requirements
- Minimum SDK: 27 (Android 8.1)
- Target SDK: 35 (Android 15)
- Kotlin 2.0.21
- RECORD_AUDIO permission required for voice recording

## App Variants

### MVP Version
- Basic audio recording and playback
- Mood and topic tagging
- History view with filtering
- Basic settings for defaults

### Extended Version
- AI-powered transcription
- Gesture-based quick recording
- Enhanced audio visualization
- Additional UI refinements

## Credits

This project was developed as part of the Mobile Dev Campus Monthly Challenge by [Philipp Lackner](https://pl-coding.com/campus/). The challenge provided an opportunity to demonstrate Android development expertise while building a creative, user-focused application.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.