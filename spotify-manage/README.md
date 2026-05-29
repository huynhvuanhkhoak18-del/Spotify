#  Spotify Tracks Manager

> **ITE23005 – Object-Oriented Programming with Java**
> Final Exam | Exam Code 2526 | Semester 2, AY 2025–2026
> The Saigon International University – Faculty of Engineering & Computer Science

---

## Project Description

**Spotify Tracks Manager** is a Java desktop application developed to manage and analyze music data from the **Spotify Tracks** dataset (Kaggle – 114,000 songs). The application allows users to log in based on roles, perform full CRUD operations, view statistical analytics through interactive charts, and export data to Excel files.

The project fully applies Object-Oriented Programming principles, GoF Design Patterns, modern Java 17 features, and MySQL database connectivity through JDBC.

### Objectives

* Allow ADMIN users to manage all data: tracks, artists, genres, and users
* Allow VIEWER users to search, filter, browse, and export data
* Display real-time visual analytics through JFreeChart charts
* Import the entire CSV dataset into MySQL via the DataImporter utility

---

## Team Members

| Full Name       | Student ID   | Role                                              |
| --------------- | ------------ | ---------------- |
| [Huỳnh Vũ Anh Khoa] | [97482503608] | Team Leader  |
| [Nguyễn Thiên Kỳ] | [77482503643] | Member –    |

---

## Technologies Used

| Component            | Technology                | Version  |
| -------------------- | ------------------------- | -------- |
| Programming Language | Java                      | 17       |
| User Interface       | Java Swing + FlatLaf Dark | 3.4      |
| Database             | MySQL                     | 8.x      |
| Database Connection  | JDBC                      | built-in |
| Charts               | JFreeChart                | 1.5.4    |
| Excel Export         | Apache POI                | 5.2.5    |
| CSV Reader           | OpenCSV                   | 5.9      |
| Password Encryption  | jBCrypt                   | 0.4      |
| Build Tool           | Maven                     | 3.8+     |
| Unit Testing         | JUnit 5                   | 5.10.2   |

---

## Mandatory Components Checklist

| # | Component                   | Status | Related Files                                      |
| - | --------------------------- | ------ | -------------------------------------------------- |
| 1 | OOP Principles              | ✅      | `BaseEntity`, `BaseDAO<T>`, `DataChangeListener`   |
| 2 | MySQL Database + JDBC       | ✅      | `schema.sql`, `TrackDAO`, `UserDAO`                |
| 3 | Design Patterns (≥2)        | ✅      | `DatabaseConnection`, `DAOFactory`, `SortStrategy` |
| 4 | Java Collections & Generics | ✅      | `BaseDAO<T>`, `TrackService`, `SessionManager`     |
| 5 | Data Visualization          | ✅      | `DashboardChartPanel` – 4 chart types              |
| 6 | User Authentication + RBAC  | ✅      | `AuthService`, `SessionManager`, `LoginView`       |

---

##  Prerequisites

Before installation, ensure your computer has the following software installed:

* **JDK 17** or later
* **Apache Maven 3.8+**
* **MySQL Server 8.x** running on `localhost:3306`
* **MySQL Workbench** (recommended for database inspection)
* **Git** for cloning the repository

Verify the installations:

```bash
java -version    # must be >= 17
mvn -version     # must be >= 3.8
mysql --version  # must be >= 8.0
```

---

##  Installation Guide

### Step 1 – Clone the Repository

```bash
git https://github.com/huynhvuanhkhoak18-del/Spotify-Tracks.git
cd spotify-manager
```

### Step 2 – Configure Database Connection

Open the following file and update your MySQL credentials:

```text
src/main/java/com/spotify/util/DatabaseConnection.java
```

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/spotify_db"
    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

private static final String DB_USER     = "root";         // ← edit here
private static final String DB_PASSWORD = "yourpassword"; // ← edit here
```

### Step 3 – Create the Database Schema

Run the following command to create all database tables and default accounts:

```bash
mysql -u root -p < database/schema.sql
```

Or use **MySQL Workbench** → Server → Data Import → select `database/schema.sql` → Execute.

After successful execution, the database `spotify_db` will contain **5 tables**:

```text
spotify_db
├── tracks      (main table – ~114,000 rows after import)
├── artists
├── albums
├── genres
└── users       (2 default seeded accounts)
```

### Step 4 – Download the Spotify Dataset

1. Visit: https://www.kaggle.com/datasets/maharshipandya/spotify-tracks-dataset
2. Log in to Kaggle (free) → click **Download**
3. Extract the ZIP file → rename the CSV file to `spotify_tracks.csv`
4. Save the file somewhere accessible (e.g., Desktop)

> The dataset contains approximately 114,000 songs with 20 attributes such as track name, artist, album, genre, popularity, danceability, energy, tempo, valence, etc.

### Step 5 – Build the Project

```bash
# Compile and build fat JAR (skip tests for faster build)
mvn clean package -DskipTests
```

The generated JAR file will be located at:

```text
target/spotify-manager.jar
```

### Step 6 – Run the Application

```bash
# Option 1 – Run directly using Maven
mvn exec:java -Dexec.mainClass=com.spotify.Main

# Option 2 – Run the generated JAR
java -jar target/spotify-manager.jar
```

---

##  Database Import Guide (CSV Dataset)

After logging in with the **admin** account:

1. Click the **Import CSV** tab in the navigation bar
2. Click **Browse** → select the `spotify_tracks.csv` file
3. Click **Import** → the progress bar will display the import progress
4. Wait approximately **2–5 minutes** depending on system performance
5. A completion message will appear → go to the **🎵 Tracks** tab to verify the imported data

### Internal Working Mechanism

The `DataImporter.java` class reads the CSV file using OpenCSV, automatically creates genre/artist/album records if they do not exist (using `HashMap` caching to avoid duplicate queries), then performs bulk insertion into the `tracks` table using JDBC `PreparedStatement.addBatch()` with a batch size of 500 rows. The entire process runs inside a `SwingWorker` to prevent the UI from freezing.

---

##  Application User Guide

### Login

Launch the application → the login screen will appear:

| Username | Password     | Role   | Description                               |
| -------- | ------------ | ------ | ----------------------------------------- |
| `admin`  | `admin`  | ADMIN  | Full CRUD access, user management, import |
| `viewer` | `viewer` | VIEWER | View, search, and export only             |

> Passwords are stored as BCrypt hashes (12 rounds). Plain-text passwords are never stored.

---

### Dashboard – Statistical Charts

The first tab displayed after login includes:

* **KPI Strip** at the top: Total Tracks | Average Popularity | Most Popular Genre
* **Chart 1 – Bar Chart**: Top 15 genres by average popularity
* **Chart 2 – Pie Chart**: Distribution of tracks by genre (top 10)
* **Chart 3 – Scatter Plot**: Energy vs. Popularity correlation (500 random samples)
* **Chart 4 – Feature Bar Chart**: Average audio feature values (danceability, energy, valence, etc.)

A genre filter and Refresh button are located in the top-right corner. All charts automatically refresh whenever the data changes (Observer Pattern).

---

### Track Management

* **Search**: Enter keywords into the Search field → press Enter or click 
* **Filter by Genre**: Select a genre from the combo box
* **Pagination**: 50 tracks per page, use  buttons to navigate
* **Sorting**: Click table headers to sort ascending/descending
* **Add Track**: Click  Add → fill the form → Save (ADMIN only)
* **Edit Track**: Select a row → click  Edit (ADMIN only)
* **Delete Track**: Select a row → click Delete → confirm deletion (ADMIN only)
* **Export Excel**: Click  Export Excel → choose save location → generates `.xlsx` file with blue headers, auto-fit columns, and summary row

![Tracks](1780038822750_203811264528326053_2829160186199748402_d6af2f268b5e6397f490d930470c756c.jpg

---

### Artist & Genre Management

Similar to Track Management but simplified:

* View artist/genre lists with track counts
* Search by name
* Add / Edit / Delete (ADMIN only)

---

### User Management (ADMIN only)

* View all user accounts
* Create new users with ADMIN or VIEWER roles
* Edit user information (full name, email, role)
* Reset other users’ passwords
* Cannot delete the currently logged-in account

---

### User Profile

* View full name, email, role, and last login time
* Change password (requires entering the old password for verification)

---

##  System Architecture

```text
┌──────────────────────────────────────────────────────────┐
                   View Layer (Swing)                    
  LoginView  MainView  DashboardChartPanel               
  TrackManagementPanel  ArtistPanel  GenrePanel          
  UserManagementPanel   ImportPanel  ProfilePanel        
└──────────────────────┬──────────────────────────────────┘
                       │ calls
┌──────────────────────▼──────────────────────────────────┐
                  Service Layer                          
         AuthService            TrackService             │
└──────────┬───────────────────────────┬──────────────────┘
           │ uses                      │ uses
┌──────────▼───────────────────────────▼──────────────────┐
                    DAO Layer                            
  BaseDAO<T>  TrackDAO  UserDAO  ArtistDAO               
              AlbumDAO  GenreDAO                         
└──────────────────────┬──────────────────────────────────┘
                       │ JDBC PreparedStatement
┌──────────────────────▼──────────────────────────────────┐
             MySQL 8 – spotify_db                      
  tracks │ artists │ albums │ genres │ users           
└──────────────────────────────────────────────────────────┘
```

### Applied Design Patterns

| Pattern       | Class                | Description                                 |
| ------------- | -------------------- | ------------------------------------------- |
| **Singleton** | `DatabaseConnection` | Thread-safe single JDBC connection          |
| **Factory**   | `DAOFactory`         | Centralized DAO creation                    |
| **Observer**  | `DataChangeListener` | Auto-refresh charts when data changes       |
| **Strategy**  | `SortStrategy`       | Flexible interchangeable sorting algorithms |

### Modern Java 17 Features

| Feature                  | Usage                                          |
| ------------------------ | ---------------------------------------------- |
| `var` – type inference   | DAO classes, View panels                       |
| `record` – immutable DTO  | `TrackDTO`, `UserDTO`, `GenreStatDTO`          |
| Switch expressions       | `SortStrategy`, `AuthService`                  |
| `Optional<T>`            | All `findById()` methods                       |
| Stream API + Lambda      | Filtering, grouping, sorting in `TrackService` |
| Text blocks              | SQL queries in DAO classes                     |

---

##  Running Unit Tests

```bash
mvn test
```

| Test File            | Number of Tests | Description                                 |
| ------------------ -- | --------------- | ------------------------------------------- |
| `PasswordUtilTest`   | 5                | BCrypt hashing and verification             |
| `ValidationUtilTest`   | 8               | Track, user, email, and password validation |
| `TrackServiceTest`    | 5               | Stream API operations and statistics        |

All tests run completely **in-memory** without requiring a database connection.

---

##  Project Structure

```text
spotify-manager/
├── pom.xml
├── README.md
├── database/
│   └── schema.sql
├── src/
│   ├── main/java/com/spotify/
│   │   ├── Main.java
│   │   ├── dao/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── pattern/
│   │   ├── service/
│   │   ├── util/
│   │   └── view/
│   └── test/java/com/spotify/
└── target/
    └── spotify-manager.jar
```

---

### Conventional Commit Messages

```text
feat: add BCrypt authentication feature
feat: complete CRUD panel for Track
feat: add 4 JFreeChart visualizations to Dashboard
fix: resolve incorrect column name in UserDAO
fix: resolve ChartPanel naming conflict with JFreeChart
refactor: separate DataImporter from ImportPanel
docs: update README installation guide
test: add unit tests for PasswordUtil and ValidationUtil
```

---

##  Known Limitations

* The Spotify dataset does not include a `release_date` column, so filtering by release date is not supported
* The scatter chart uses only 500 random samples for performance reasons; rendering all 114,000 points would significantly slow down the application
* `ArtistPanel`, `GenrePanel`, `UserManagementPanel`, `ImportPanel`, and `ProfilePanel` are currently skeleton implementations and require additional advanced validation
* Internationalization (i18n) is not yet supported
* macOS Apple Silicon is not supported when using Intel x86 JDK builds

---

## References

* Maharshi Pandya. (2022). *Spotify Tracks Dataset*. Kaggle. https://www.kaggle.com/datasets/maharshipandya/spotify-tracks-dataset
* Oracle. (2024). *Java SE 17 Documentation*. https://docs.oracle.com/en/java/javase/17/
* MySQL AB. (2024). *MySQL 8.0 Reference Manual*. https://dev.mysql.com/doc/refman/8.0/en/
* David Gilbert. (2023). *JFreeChart Developer Guide v1.5.4*. https://www.jfree.org/jfreechart/
* Apache Software Foundation. (2024). *Apache POI*. https://poi.apache.org/
* FormDev Software. (2024). *FlatLaf – Flat Look and Feel*. https://www.formdev.com/flatlaf/
* Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.

---

*ITE23005 OOP Final Exam | Exam Code 2526 | The Saigon International University*
