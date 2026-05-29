# NovaFlix 🎬

NovaFlix is a modern, feature-rich Android application for movie and TV show enthusiasts. It provides real-time data on popular, upcoming, and trending content using the TMDB API, integrated with Firebase for secure user authentication.

## 🚀 Features

- **User Authentication**: Secure Sign-up, Login, and Logout functionality powered by Firebase Auth.
- **Cinematic Hero Banner**: Large, beautiful cards featuring the most popular movies at the top of your feed.
- **Dynamic Content Sections**:
  - **Latest Releases**: Browse through the newest additions to the cinematic world.
  - **Upcoming Movies**: Stay ahead with a dedicated section for future releases.
  - **Trending TV Shows**: A dedicated grid-based view for the most popular series of the week.
- **Advanced Search**: Instantly find any movie in the TMDB database.
- **Detailed Information**: 
  - Full movie/show descriptions.
  - Interactive **Cast & Crew** lists with actor photos and roles.
  - High-quality backdrop and poster imagery.
- **Seamless Navigation**: Smooth transitions using a modern Bottom Navigation Bar and intuitive tab switching.
- **Polished UI**: Material 3 design with smooth edges, consistent spacing, and dark mode optimization.

## 🛠 Tech Stack

- **Language**: Java
- **UI Framework**: Android XML with Material Components
- **Networking**: Retrofit 2 & GSON
- **Image Loading**: Glide
- **Backend**: Firebase Authentication & Firestore
- **Data Source**: [The Movie Database (TMDB) API](https://www.themoviedb.org/documentation/api)

## 📦 Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/NovaFlix.git
   ```

2. **Firebase Setup**:
   - Create a project on the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android App with the package name `com.movies`.
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Enable **Email/Password** authentication in the Firebase Auth settings.

3. **TMDB API Key**:
   - Get an API key from [TMDB](https://www.themoviedb.org/settings/api).
   - Open `MainActivity.java` and `TvShowsActivity.java`.
   - Replace the `API_KEY` constant with your personal key.

4. **Build & Run**:
   - Open the project in Android Studio.
   - Sync Gradle.
   - Press **Run** to launch NovaFlix on your emulator or physical device.

## 📸 Screenshots

| Splash Screen | Login | Home Feed | TV Shows |
| :---: | :---: | :---: | :---: |
| ![Splash](app/src/main/res/drawable/movie_poster.png) | (Login UI) | (Hero & Lists) | (TV Grid) |

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Developed with ❤️ by the NovaFlix Team.*
