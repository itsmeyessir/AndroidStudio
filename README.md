# SciCal - Scientific Calculator 📱

A modern, feature-rich scientific calculator app for Android with advanced mathematical functions and graphing capabilities.

## 🌟 Features

### Core Calculator
- **Basic arithmetic operations** (Addition, Subtraction, Multiplication, Division)
- **Scientific functions** (Trigonometry, Logarithms, Exponentials)
- **Advanced mathematical expressions** with MXParser integration
- **Haptic feedback** for enhanced user experience
- **Clean, intuitive UI** with Material Design

### Advanced Features
- **Scientific Calculator Mode** - Access to advanced mathematical functions
- **Graphing Functionality** - Visualize mathematical functions and equations
- **Expression Evaluation** - Support for complex mathematical expressions
- **Multiple Activity Support** - Seamless navigation between different calculator modes

## 📋 Requirements

- **Android 5.0 (API 21)** or higher
- **Target SDK**: Android 35
- **Compile SDK**: Android 35
- **Java 8** compatibility

## 🛠️ Tech Stack

- **Language**: Java
- **UI Framework**: Android SDK with Material Design Components
- **Build System**: Gradle with Kotlin DSL
- **Mathematical Engine**: [MXParser](https://mathparser.org/) v5.2.0
- **Graphing**: [GraphView](https://github.com/jjoe64/GraphView) v4.2.2

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/scical/
│   │   ├── MainActivity.java          # Main calculator interface
│   │   ├── ScientificActivity.java    # Scientific calculator features
│   │   └── GraphingActivity.java      # Graph plotting functionality
│   ├── res/                          # Resources (layouts, strings, etc.)
│   └── AndroidManifest.xml          # App configuration
├── build.gradle.kts                 # Module build configuration
└── proguard-rules.pro              # Code obfuscation rules
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or later
- JDK 8 or later
- Android SDK with API level 35

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/itsmeyessir/AndroidStudio.git
   cd SciCal
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 🎯 Usage

### Basic Calculator
1. Launch the app to access the main calculator interface
2. Input numbers and operations using the on-screen buttons
3. Tap "=" to evaluate expressions
4. Experience haptic feedback with each button press

### Scientific Mode
1. Navigate to Scientific Calculator from the main interface
2. Access advanced functions like sin, cos, tan, log, ln, etc.
3. Use parentheses for complex expressions
4. Supports mathematical constants like π and e

### Graphing Mode
1. Access the Graphing feature from the menu
2. Input mathematical functions
3. Visualize function plots and analyze graphs
4. Interactive graph manipulation and zooming

## 📚 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| AndroidX AppCompat | 1.7.0 | Backward compatibility |
| Material Components | 1.12.0 | Material Design UI |
| ConstraintLayout | 2.2.1 | Advanced layouts |
| MXParser | 5.2.0 | Mathematical expression parsing |
| GraphView | 4.2.2 | Graph plotting and visualization |
| GridLayout | 1.0.0 | Grid-based layouts |

## 🔧 Configuration

### Permissions
The app requires the following permission:
- `VIBRATE` - For haptic feedback during button interactions

### Build Configuration
- **Application ID**: `com.example.scical`
- **Version Code**: 1
- **Version Name**: 1.0
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 35 (Android 15)

## 🧪 Testing

Run the test suite:
```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
```

## 📝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 👥 Authors

- **itsmeyessir** - *Initial work* - [GitHub Profile](https://github.com/itsmeyessir)

---

**Thank you!** 🧮✨
