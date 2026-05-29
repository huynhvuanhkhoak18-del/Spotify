# 🎵 Spotify Tracks Manager

> **ITE23005 – Object-Oriented Programming with Java**
> Final Exam | Exam Code 2526 | Semester 2, AY 2025–2026
> The Saigon International University – Faculty of Engineering & Computer Science

---

##  Mô tả dự án

**Spotify Tracks Manager** là ứng dụng desktop Java được xây dựng nhằm quản lý và phân tích dữ liệu âm nhạc từ bộ dataset **Spotify Tracks** (Kaggle – 114.000 bài hát). Ứng dụng cho phép người dùng đăng nhập theo vai trò, thực hiện đầy đủ các thao tác CRUD, xem thống kê qua biểu đồ tương tác và xuất dữ liệu ra Excel.

Dự án áp dụng đầy đủ các nguyên lý OOP, Design Patterns (GoF), tính năng hiện đại của Java 17 và kết nối cơ sở dữ liệu MySQL thông qua JDBC.

### Mục tiêu

- Cho phép ADMIN quản lý toàn bộ dữ liệu: bài hát, nghệ sĩ, thể loại, người dùng
- Cho phép VIEWER tra cứu, lọc, tìm kiếm và xuất dữ liệu
- Hiển thị thống kê trực quan bằng biểu đồ JFreeChart cập nhật theo thời gian thực
- Nhập toàn bộ dataset CSV vào MySQL qua tiện ích DataImporter

---

## Thành viên nhóm

| Họ và tên | MSSV | Vai trò |
|---|---|---|
| [Tên thành viên 1] | [MSSV] | Trưởng nhóm – Database, DAO layer, Authentication |
| [Tên thành viên 2] | [MSSV] | UI/UX – Swing panels, JFreeChart, Excel export |

---

##  Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| Ngôn ngữ | Java | 17 |
| Giao diện | Java Swing + FlatLaf Dark | 3.4 |
| Database | MySQL | 8.x |
| Kết nối DB | JDBC | built-in |
| Biểu đồ | JFreeChart | 1.5.4 |
| Xuất Excel | Apache POI | 5.2.5 |
| Đọc CSV | OpenCSV | 5.9 |
| Mã hoá mật khẩu | jBCrypt | 0.4 |
| Build tool | Maven | 3.8+ |
| Unit test | JUnit 5 | 5.10.2 |

---

##  Checklist các thành phần bắt buộc

| # | Thành phần | Trạng thái | File liên quan |
|---|---|---|---|
| 1 | OOP Principles | ✅ | `BaseEntity`, `BaseDAO<T>`, `DataChangeListener` |
| 2 | MySQL Database + JDBC | ✅ | `schema.sql`, `TrackDAO`, `UserDAO` |
| 3 | Design Patterns (≥2) | ✅ | `DatabaseConnection`, `DAOFactory`, `SortStrategy` |
| 4 | Java Collections & Generics | ✅ | `BaseDAO<T>`, `TrackService`, `SessionManager` |
| 5 | Data Visualisation | ✅ | `DashboardChartPanel` – 4 loại biểu đồ |
| 6 | User Auth + RBAC | ✅ | `AuthService`, `SessionManager`, `LoginView` |

---

##  Điều kiện tiên quyết

Trước khi cài đặt, hãy đảm bảo máy đã có:

- **JDK 17** trở lên – [Tải tại đây](https://www.oracle.com/java/technologies/downloads/)
- **Apache Maven 3.8+** – [Tải tại đây](https://maven.apache.org/download.cgi)
- **MySQL Server 8.x** – đang chạy tại `localhost:3306`
- **MySQL Workbench** (khuyến nghị, để kiểm tra dữ liệu)
- **Git** – để clone repository

---

## 📥 Các bước cài đặt

### Bước 1 – Clone repository

```bash
git clone https://github.com/huynhvuanhkhoak18-del/Spotify-Tracks.git
cd spotify-manager
```

### Bước 2 – Cấu hình kết nối database

Mở file sau và cập nhật thông tin đăng nhập MySQL của bạn:

```
src/main/java/com/spotify/util/DatabaseConnection.java
```

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/spotify_db"
    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

private static final String DB_USER     = "root";        // ← sửa tại đây
private static final String DB_PASSWORD = "yourpassword"; // ← sửa tại đây
```

### Bước 3 – Tạo schema database

Chạy lệnh sau để tạo toàn bộ bảng và tài khoản mặc định:

```bash
mysql -u root -p < database/schema.sql
```

Hoặc mở **MySQL Workbench** → Server → Data Import → chọn file `database/schema.sql` → Execute.

Sau khi chạy thành công, database `spotify_db` sẽ có **5 bảng**:

```
spotify_db
├── tracks      (bảng chính – ~114.000 dòng sau khi import)
├── artists
├── albums
├── genres
└── users       (2 tài khoản mặc định đã được seed)
```

### Bước 4 – Tải dataset Spotify

1. Truy cập: https://www.kaggle.com/datasets/maharshipandya/spotify-tracks-dataset
2. Đăng nhập Kaggle (miễn phí) → nhấn **Download**
3. Giải nén → đổi tên file thành `spotify_tracks.csv`
4. Lưu file ở vị trí dễ tìm (ví dụ: Desktop)

> Dataset bao gồm ~114.000 bài hát với 20 thuộc tính: tên, nghệ sĩ, album, thể loại, độ phổ biến, danceability, energy, tempo, valence…

### Bước 5 – Build dự án

```bash
# Compile và build fat JAR (bỏ qua test để build nhanh)
mvn clean package -DskipTests
```

File JAR được tạo tại: `target/spotify-manager.jar`

### Bước 6 – Chạy ứng dụng

```bash
# Cách 1 – chạy trực tiếp qua Maven
mvn exec:java -Dexec.mainClass=com.spotify.Main

# Cách 2 – chạy JAR (sau bước mvn package)
java -jar target/spotify-manager.jar
```

---

##  Hướng dẫn Import Database (Dataset CSV)

Sau khi đăng nhập ứng dụng bằng tài khoản **admin**:

1. Nhấn vào tab **Import CSV** trên thanh điều hướng
2. Nhấn nút **Browse** → chọn file `spotify_tracks.csv` vừa tải
3. Nhấn nút **Import** → thanh tiến trình hiển thị quá trình nhập dữ liệu
4. Chờ khoảng **2–5 phút** tuỳ tốc độ máy (114.000 dòng, batch 500 rows/lần)
5. Thông báo hoàn thành hiện ra → chuyển sang tab **Tracks** để kiểm tra

**Cơ chế hoạt động bên trong:**

Lớp `DataImporter.java` đọc CSV bằng OpenCSV, tự động tạo bản ghi genre/artist/album nếu chưa tồn tại (dùng `HashMap` cache để tránh query trùng), sau đó bulk insert vào bảng `tracks` qua JDBC `PreparedStatement.addBatch()` với kích thước batch 500 dòng. Toàn bộ quá trình chạy trên `SwingWorker` để không đóng băng giao diện.

---

##  Hướng dẫn sử dụng ứng dụng

### Đăng nhập

Khởi động ứng dụng → màn hình đăng nhập hiện ra:

| Tài khoản | Mật khẩu   | Quyền  | Mô tả |
|-----------|------------|--------|-------|
| `admin`   | `admin`  | ADMIN  | Toàn quyền CRUD, quản lý users, import |
| `viewer`  | `viewer` | VIEWER | Chỉ xem, tìm kiếm, xuất Excel |

> Mật khẩu được lưu dưới dạng BCrypt hash (12 rounds). Không bao giờ lưu plain text.

---

### Dashboard – Biểu đồ thống kê

Tab đầu tiên sau khi đăng nhập. Bao gồm:

- **KPI Strip** ở trên cùng: Tổng số bài hát | Độ phổ biến trung bình | Thể loại phổ biến nhất
- **Biểu đồ 1 – Bar Chart**: Top 15 thể loại theo độ phổ biến trung bình
- **Biểu đồ 2 – Pie Chart**: Phân phối số bài hát theo thể loại (top 10)
- **Biểu đồ 3 – Scatter Plot**: Tương quan Energy vs. Popularity (500 mẫu ngẫu nhiên)
- **Biểu đồ 4 – Feature Bar**: Giá trị trung bình các thuộc tính âm thanh (danceability, energy, valence…)

Bộ lọc thể loại và nút Refresh ở góc trên phải. Tất cả biểu đồ tự động cập nhật khi dữ liệu thay đổi (Observer Pattern).

---

### Quản lý bài hát (Tracks)

- **Tìm kiếm**: nhập từ khoá vào ô Search → Enter hoặc nhấn nút 
- **Lọc theo thể loại**: chọn genre từ combobox
- **Phân trang**: 50 bài/trang, dùng nút  để chuyển trang
- **Sắp xếp**: nhấn vào tiêu đề cột để sắp xếp tăng/giảm dần
- **Thêm bài hát**: nhấn  Add → điền form → Save (chỉ ADMIN)
- **Sửa**: chọn dòng → nhấn  Edit (chỉ ADMIN)
- **Xoá**: chọn dòng → nhấn  Delete → xác nhận (chỉ ADMIN)
- **Xuất Excel**: nhấn  Export Excel → chọn nơi lưu → file .xlsx với header màu xanh, auto-fit cột, dòng summary ở cuối

![Tracks](c:\Users\ASUS\eclipse-workspace\spotify-manage\project\imagies\1780039209509_203811264528326053_2829160186199748402_71c18058781c678ed36a17c5c4138fa1.jpg)

![Tên ảnh](c:\Users\ASUS\eclipse-workspace\spotify-manage\project\imagies\1780038822750_203811264528326053_2829160186199748402_d6af2f268b5e6397f490d930470c756c.jpg)

![Tên ảnh](c:\Users\ASUS\eclipse-workspace\spotify-manage\project\imagies\1780039080591_203811264528326053_2829160186199748402_3bbd073627a84321d255581e3ce35046.jpg)

![Tên ảnh](c:\Users\ASUS\eclipse-workspace\spotify-manage\project\imagies\1780040018207_203811264528326053_2829160186199748402_aa44acff0ba69b64d7411ea9336a2731.jpg)


![Tên ảnh](c:\Users\ASUS\eclipse-workspace\spotify-manage\project\imagies\1780039104582_203811264528326053_2829160186199748402_7390ead27f9e61855d22f832374e45ad.jpg)


### Quản lý nghệ sĩ & thể loại

Tương tự Track Management nhưng đơn giản hơn:

- Xem danh sách nghệ sĩ/thể loại kèm số lượng bài hát
- Tìm kiếm theo tên
- Thêm / Sửa / Xoá (chỉ ADMIN)

---

### Quản lý người dùng (chỉ ADMIN)

- Xem danh sách tất cả tài khoản
- Thêm tài khoản mới với phân quyền ADMIN hoặc VIEWER
- Sửa thông tin (họ tên, email, role)
- Đặt lại mật khẩu cho người dùng khác
- Không thể xoá tài khoản đang đăng nhập

---

### Hồ sơ cá nhân

- Xem tên, email, role, thời gian đăng nhập lần cuối
- Đổi mật khẩu (nhập mật khẩu cũ để xác thực trước)

---

## 🏗️ Kiến trúc hệ thống

```
┌──────────────────────────────────────────────────────────┐
│                   View Layer (Swing)                      │
│  LoginView  MainView  DashboardChartPanel                 │
│  TrackManagementPanel  ArtistPanel  GenrePanel            │
│  UserManagementPanel   ImportPanel  ProfilePanel          │
└──────────────────────┬───────────────────────────────────┘
                       │ gọi
┌──────────────────────▼───────────────────────────────────┐
│                  Service Layer                            │
│         AuthService            TrackService               │
└──────────┬───────────────────────────┬────────────────────┘
           │ dùng                      │ dùng
┌──────────▼───────────────────────────▼────────────────────┐
│                    DAO Layer                               │
│  BaseDAO<T>  TrackDAO  UserDAO  ArtistDAO                  │
│              AlbumDAO  GenreDAO                            │
└──────────────────────┬────────────────────────────────────┘
                       │ JDBC PreparedStatement
┌──────────────────────▼────────────────────────────────────┐
│               MySQL 8 – spotify_db                        │
│    tracks │ artists │ albums │ genres │ users             │
└───────────────────────────────────────────────────────────┘
```

### Design Patterns áp dụng

| Pattern | Lớp | Mô tả |
|---|---|---|
| **Singleton** | `DatabaseConnection` | Một kết nối JDBC duy nhất, thread-safe |
| **Factory** | `DAOFactory` | Tập trung tạo DAO, tách rời logic khỏi constructor |
| **Observer** | `DataChangeListener` | Biểu đồ tự refresh khi dữ liệu thay đổi |
| **Strategy** | `SortStrategy` | Thuật toán sắp xếp có thể hoán đổi linh hoạt |

### Tính năng Java 17 hiện đại

| Tính năng | Nơi sử dụng |
|---|---|
| `var` – type inference | Toàn bộ DAO, View panels |
| `record` – immutable DTO | `TrackDTO`, `UserDTO`, `GenreStatDTO` |
| Switch expression | `SortStrategy`, `AuthService` |
| `Optional<T>` | Tất cả `findById()` trong DAO |
| Stream API + Lambda | `TrackService` – filter, groupBy, sort |
| Text blocks | SQL query trong DAO |

---

## 🧪 Chạy Unit Test

```bash
mvn test
```

| File Test | Số test | Nội dung kiểm tra |
|---|---|---|
| `PasswordUtilTest` | 5 | BCrypt hash, verify đúng/sai, null, blank |
| `ValidationUtilTest` | 8 | Validate track, user, email, password |
| `TrackServiceTest` | 5 | Stream API: top popular, countByGenre, filter, stats, groupBy |

Tất cả test đều chạy **hoàn toàn in-memory**, không cần kết nối database.

---

## 📁 Cấu trúc thư mục

```
spotify-manager/
├── pom.xml
├── README.md
├── database/
│   └── schema.sql                       ← Chạy file này đầu tiên
├── src/
│   ├── main/java/com/spotify/
│   │   ├── Main.java                    ← Entry point
│   │   ├── dao/                         ← Tầng truy xuất dữ liệu
│   │   │   ├── BaseDAO.java             ← Generic interface
│   │   │   ├── TrackDAO.java
│   │   │   ├── UserDAO.java
│   │   │   ├── ArtistDAO.java
│   │   │   ├── AlbumDAO.java
│   │   │   └── GenreDAO.java
│   │   ├── dto/                         ← Java Records (DTO)
│   │   │   ├── TrackDTO.java
│   │   │   ├── UserDTO.java
│   │   │   └── GenreStatDTO.java
│   │   ├── model/                       ← Domain entities
│   │   │   ├── BaseEntity.java          ← Abstract class
│   │   │   ├── Track.java
│   │   │   ├── User.java
│   │   │   ├── Artist.java
│   │   │   ├── Album.java
│   │   │   └── Genre.java
│   │   ├── pattern/                     ← Design patterns
│   │   │   ├── DAOFactory.java          ← Factory
│   │   │   ├── DataChangeListener.java  ← Observer interface
│   │   │   └── SortStrategy.java        ← Strategy
│   │   ├── service/                     ← Business logic
│   │   │   ├── AuthService.java
│   │   │   └── TrackService.java
│   │   ├── util/                        ← Tiện ích
│   │   │   ├── DatabaseConnection.java  ← Singleton
│   │   │   ├── PasswordUtil.java        ← BCrypt
│   │   │   ├── SessionManager.java      ← HashMap session
│   │   │   ├── ValidationUtil.java
│   │   │   ├── ExcelExporter.java       ← Apache POI
│   │   │   └── DataImporter.java        ← OpenCSV + batch JDBC
│   │   └── view/                        ← Giao diện Swing
│   │       ├── LoginView.java
│   │       ├── MainView.java
│   │       ├── DashboardChartPanel.java
│   │       ├── TrackManagementPanel.java
│   │       ├── ArtistPanel.java
│   │       ├── GenrePanel.java
│   │       ├── UserManagementPanel.java
│   │       ├── ImportPanel.java
│   │       └── ProfilePanel.java
│   └── test/java/com/spotify/
│       ├── PasswordUtilTest.java
│       ├── ValidationUtilTest.java
│       └── TrackServiceTest.java
└── target/
    └── spotify-manager.jar              ← Sau khi mvn package
```

---

## 🌿 Git Branching

Dự án sử dụng cấu trúc branch theo chuẩn:

```
main          ← code ổn định, đã test
feature/*     ← phát triển tính năng mới
```

Ví dụ các branch đã dùng:

```
feature/login-auth
feature/track-crud
feature/dashboard-charts
feature/excel-export
feature/csv-import
feature/unit-tests
```

### Quy ước Commit Message (Conventional Commits)

```
feat: thêm chức năng đăng nhập với BCrypt
feat: hoàn thành CRUD panel cho Track
feat: thêm 4 biểu đồ JFreeChart vào Dashboard
fix: sửa lỗi UserDAO sai tên cột user_id
fix: sửa xung đột tên class ChartPanel với JFreeChart
refactor: tách DataImporter ra khỏi ImportPanel
docs: cập nhật README với hướng dẫn cài đặt
test: thêm unit test cho PasswordUtil và ValidationUtil
```

---

## ⚠️ Hạn chế đã biết

- Dataset Spotify không có cột `release_date` nên không hỗ trợ lọc theo khoảng thời gian phát hành
- Scatter chart chỉ lấy mẫu 500 bản ghi ngẫu nhiên để đảm bảo hiệu năng; vẽ toàn bộ 114.000 điểm sẽ rất chậm
- Các panel ArtistPanel, GenrePanel, UserManagementPanel, ImportPanel, ProfilePanel hiện là bản skeleton; cần hoàn thiện thêm các validation phức tạp
- Chưa hỗ trợ đa ngôn ngữ (i18n)
- Không hỗ trợ chạy trên macOS Apple Silicon nếu dùng JDK Intel x86

---

## 🔗 Tài liệu tham khảo

- Maharshi Pandya. (2022). *Spotify Tracks Dataset*. Kaggle. https://www.kaggle.com/datasets/maharshipandya/spotify-tracks-dataset
- Oracle. (2024). *Java SE 17 Documentation*. https://docs.oracle.com/en/java/javase/17/
- MySQL AB. (2024). *MySQL 8.0 Reference Manual*. https://dev.mysql.com/doc/refman/8.0/en/
- David Gilbert. (2023). *JFreeChart Developer Guide v1.5.4*. https://www.jfree.org/jfreechart/
- Apache Software Foundation. (2024). *Apache POI*. https://poi.apache.org/
- FormDev Software. (2024). *FlatLaf – Flat Look and Feel*. https://www.formdev.com/flatlaf/
- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.

---

*ITE23005 OOP Final Exam | Exam Code 2526 | The Saigon International University*