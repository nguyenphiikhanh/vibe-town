Source Server Game Avatar 2D là bộ mã nguồn máy chủ game online được xây dựng bằng Java Socket kết hợp MySQL, phù hợp cho người muốn nghiên cứu, phát triển hoặc vận hành server game Avatar 2D riêng. Source đã có sẵn cấu trúc server, database mẫu, dữ liệu vật phẩm, NPC, shop, tài nguyên hình ảnh HD/Medium và file .jar có thể chạy trực tiếp.

## Web game: Phố Nhỏ Avatar

Project hiện có thêm hai ứng dụng tách biệt:

- `Client/`: Next.js client tiếng Việt cho thị trấn kinh doanh, dùng sprite cửa hàng Avatar trong `res/hd/object/` và sprite nông sản trong `res/hd/bigFarm/`.
- `Server/`: Elysia API cho đăng nhập, kinh tế/cửa hàng và tài nguyên; Colyseus room cho vòng lặp nông trại; MySQL cho dữ liệu người chơi.
- `Server/drizzle/`: schema Drizzle và migration cho nông trại, số dư thị trấn, cửa hàng và hàng tồn kho; dữ liệu gắn với tài khoản trong bảng `users` hiện có.

Vòng lặp Phố Nhỏ hiện gồm nhập hàng → phục vụ khách → nhận xu/thiện cảm → mở thêm cửa tiệm. Khu nông trại gieo trồng/thu hoạch vẫn được giữ làm nền cho các tính năng phát triển tiếp theo.

### Chạy bản web cục bộ

1. Cài Node.js 22+ và npm. Khởi động MariaDB từ cấu hình hiện tại:

   ```bash
   docker compose up -d mariadb
   ```

2. Import `database/avatar.sql` vào database `avatar` nếu đây là database mới, sau đó áp dụng migration bằng Drizzle:

   ```bash
   docker compose exec -T mariadb mariadb -uroot avatar < database/avatar.sql
   ```

3. Tạo file `Server/.env` từ `Server/.env.example`. Đặt `DATABASE_URL` theo MariaDB của bạn và thay `JWT_SECRET` bằng chuỗi ngẫu nhiên đủ dài. `RESOURCE_ROOT=res` trỏ đến tài nguyên ở thư mục gốc. Cài dependency, rồi chạy migration từ thư mục `Server`:

   ```bash
   cd Server
   npm install
   npm run db:migrate
   ```

   Schema ở `Server/src/db/schema.ts`; migration được lưu trong `Server/drizzle/`. Khi cập nhật schema, tạo migration mới bằng `npm run db:generate`. `users` là bảng legacy chỉ tham chiếu khóa ngoại, không được tạo lại.

4. Chạy server:

   ```bash
   npm run dev
   ```

5. Tạo `Client/.env.local` từ `Client/.env.example`, rồi cài dependency và chạy client ở terminal khác:

   ```bash
   cd Client
   npm install
   npm run dev
   ```

6. Mở `http://localhost:3000` và đăng nhập bằng tài khoản Avatar đang hoạt động trong database. Web game không thay đổi hash mật khẩu MD5 của client Java cũ.

Server API chạy ở `http://localhost:3001`; Colyseus dùng cổng `2567`. Cấu hình CORS mặc định chỉ cho phép client ở `http://localhost:3000`.

Bộ source phù hợp cho lập trình viên, chủ server game hoặc người đang tìm hiểu cách xây dựng hệ thống game online dạng socket thời gian thực. Source có cấu trúc rõ ràng, dễ chỉnh sửa cấu hình port, database, thông báo server, tên thành phố và tài nguyên game.

---

## 1. Chức năng nổi bật trong source

### Hệ thống server
- Server Java Socket chạy qua port cấu hình, mặc định: `19128`.
- Có file chạy sẵn: `Server/avatar-server.jar`.
- Có class main: `avatar.server.Avatar`.
- Tự động nhận kết nối client qua `ServerSocket`.
- Hỗ trợ xử lý session người chơi, gửi/nhận message theo command.
- Có cơ chế handshake và mã hóa dữ liệu cơ bản giữa client và server.

### Hệ thống tài khoản và nhân vật
- Đăng nhập tài khoản từ database.
- Tạo nhân vật mới.
- Lưu thông tin người chơi trong bảng `users` và `players`.
- Quản lý xu, lượng, lượng khóa, xèng.
- Quản lý giới tính, level, kinh nghiệm, trạng thái online.
- Hỗ trợ đổi mật khẩu.
- Tự reset trạng thái online khi server khởi động.

### Hệ thống bản đồ và khu vực
- Load map từ dữ liệu server.
- Hỗ trợ nhiều khu vực/zone trong cùng một map.
- Người chơi có thể vào khu, di chuyển, chat trong map.
- Có quản lý NPC theo map.
- Có map offline/pet/premium trong source.

### Hệ thống vật phẩm
- Load vật phẩm từ database bảng `items`.
- Quản lý item đang mặc, item trong rương, item hiển thị.
- Có dữ liệu hình ảnh item, part, icon.
- Hỗ trợ mua item trong shop.
- Hỗ trợ dùng item, tháo item, xóa item.
- Có hệ thống thời hạn vật phẩm.

### Hệ thống nhà
- Mua item trang trí nhà.
- Sắp xếp vật phẩm trong nhà.
- Xóa vật phẩm nhà.
- Tạo/chỉnh map nhà.
- Có dữ liệu `house_item`, `house_player_item`, `house_buy` trong logic xử lý.

### Hệ thống nông trại
- Có service riêng cho farm.
- Load dữ liệu cây, đồ ăn, hình ảnh farm.
- Có command xử lý vào farm, lấy inventory, lấy dữ liệu hình ảnh farm.

### Hệ thống NPC và menu
- Load NPC từ bảng `npc`.
- NPC có tên, vị trí, map, item mặc.
- Có xử lý menu, giao tiếp NPC, action NPC.
- Có class `NpcHandler`, `MenuFunction`.

### Vòng quay may mắn
- Có hệ thống `DialLucky`.
- Hỗ trợ 3 loại quay: xu, lượng, miễn phí.
- Dữ liệu phần thưởng nằm trong bảng `dial_lucky`.

### Giftcode và log
- Database có bảng `giftcode`.
- Có bảng `giftcode_use`.
- Có bảng `giaodich_logs`.
- Phù hợp để phát triển thêm giftcode, giao dịch, nạp tiền, lịch sử nhận quà.

### Tài nguyên game
- Có thư mục tài nguyên:
  - `res/hd/`
  - `res/medium/`
  - `res/data/`
- Có dữ liệu `.dat`, hình ảnh `.png`, item, part, map, effect.
- Có cấu hình version tài nguyên trong `config.properties`.

---

## 2. Công nghệ sử dụng

- **Ngôn ngữ**: Java
- **Phiên bản build**: Java 11, manifest build bằng JDK 17
- **Database**: MySQL
- **Kết nối database**: HikariCP
- **Thư viện chính**:
  - MySQL Connector/J
  - HikariCP
  - json-simple
  - Log4j
  - Commons DbUtils
  - Commons IO
  - Jackson
  - Lombok

---

## 3. Cấu trúc source chính

```text
FULLSOURCEAVATAR/
├── Client/
│   └── Avatar2_127.0.0.1_19128.jar
├── Server/
│   └── avatar-server.jar
├── config.properties
├── database.properties
├── avatar.conf
├── database/
│   └── avatar.sql
├── res/
│   ├── hd/
│   ├── medium/
│   └── data/
├── src/main/java/avatar/
│   ├── server/
│   ├── network/
│   ├── message/
│   ├── service/
│   ├── model/
│   ├── play/
│   ├── item/
│   ├── db/
│   └── lucky/
└── pom.xml
```

---

## 4. Hướng dẫn chạy source server game

### Bước 1: Cài môi trường
Cần cài:
- Java JDK 11 trở lên, khuyến nghị **JDK 17**
- MySQL 5.7 / MySQL 8 / MariaDB
- Navicat hoặc phpMyAdmin để import database
- VPS/PC Windows hoặc Linux

Kiểm tra Java:
```bash
java -version
```

---

### Bước 2: Tạo database
Tạo database tên `avatar`:
```sql
CREATE DATABASE avatar CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Import file:
```bash
mysql -u root -p avatar < database/avatar.sql
```
*Hoặc dùng Navicat/phpMyAdmin import file: `database/avatar.sql`*

---

### Bước 3: Sửa file database
Mở file `database.properties` và cấu hình mẫu:
```properties
driver=com.mysql.cj.jdbc.Driver
host=127.0.0.1
port=3306
dbname=avatar
username=root
password=
max_connection=10
min_connection=5
```
> **Lưu ý**: Nếu MySQL không có mật khẩu thì có thể để trống.

---

### Bước 4: Sửa cấu hình server
Mở file `config.properties` và cấu hình mẫu:
```properties
server.port=19128
server.active=true
server.debug=false
game.notify=Chào mọi người
game.city.name=Kitakeyos
game.big.image.version=30314
game.part.version=15294
game.big.item.image.version=17343
game.itemtype.version=2
game.item.version=1
game.object.version=1
game.resources.hd.path=res/hd/
game.resources.medium.path=res/medium/
```

**Ý nghĩa các thông số:**
- `server.port`: port server game
- `server.active`: bật/tắt server
- `server.debug`: bật log debug
- `game.notify`: thông báo khi vào game
- `game.city.name`: tên thành phố/server
- `game.resources.hd.path`: đường dẫn tài nguyên HD
- `game.resources.medium.path`: đường dẫn tài nguyên thường

---

### Bước 5: Mở port server
Port mặc định: `19128`

**Trên Linux:**
```bash
sudo ufw allow 19128/tcp
sudo ufw reload
```

**Trên Windows Server:** 
- Mở port `19128` TCP trong Windows Firewall.

---

### Bước 6: Chạy server
Di chuyển vào thư mục source:
```bash
cd FULLSOURCEAVATAR
```

Chạy bằng file jar có sẵn:
```bash
java -jar Server/avatar-server.jar
```

Khuyến nghị chạy với RAM giới hạn:
```bash
java -Xms512m -Xmx2g -jar Server/avatar-server.jar
```

Chạy nền trên Linux:
```bash
nohup java -Xms512m -Xmx2g -jar Server/avatar-server.jar > logs/server.out 2>&1 &
```

Nếu chạy thành công sẽ thấy log dạng:
```text
Start socket port = 19128
Start server Success !
```

---

## 6. Cách build lại source

Source có `pom.xml`, có thể mở bằng IntelliJ IDEA, NetBeans hoặc VS Code Java.

- **Class chạy chính**: `avatar.server.Avatar`

### Nếu dùng IDE:
1. Mở thư mục `FULLSOURCEAVATAR`.
2. Chọn JDK 11 hoặc JDK 17.
3. Import Maven project.
4. Chạy class `avatar.server.Avatar`.

### Nếu muốn build bằng Maven:
```bash
mvn clean package
```

> **Lưu ý**: `pom.xml` hiện chưa có plugin tạo fat jar đầy đủ. File chạy sẵn `Server/avatar-server.jar` đã bao gồm dependency. Nếu build lại bằng Maven mặc định, jar trong `target/` có thể chưa chạy độc lập nếu thiếu thư viện.
