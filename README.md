# backend

# 키오패스 (KioPass) - Frontend

> 바코드 스캔 기반 무인 쇼핑 시스템 프론트엔드

---

## 🚀 시작하기

### 요구사항
- Flutter 3.x 이상
- Dart 3.x 이상
- iOS 14+ / Android 8.0 (API 26)+
- Xcode 15+ (iOS 빌드)
- Android Studio (Android 빌드)

### 설치 및 실행
```bash
git clone https://github.com/KioPass/frontend.git
cd frontend
flutter pub get
cd ios && pod install && cd ..
flutter run
```

---

## 📁 프로젝트 구조

```
lib/
├── main.dart                   # 앱 진입점, 테마 설정
├── app_theme.dart              # 디자인 토큰 (KColors, lightTheme, darkTheme)
├── services/
│   └── auth_service.dart       # 인증 서비스 (현재 SharedPreferences → JWT 교체 필요)
└── screens/
    ├── login_screen.dart       # 로그인 (카카오/네이버 OAuth)
    ├── signup_screen.dart      # 회원가입
    ├── role_select_screen.dart # 역할 선택 (구매자/판매자)
    ├── seller_verify_screen.dart # 판매자 서류 인증
    ├── main_screen.dart        # 메인 (앱바, 매장 선택, 구매자/판매자 전환)
    ├── buyer_home_screen.dart  # 구매자 홈 (상품 목록, 바코드 스캔, 장바구니)
    ├── seller_home_screen.dart # 판매자 홈 (대시보드, 재고관리, QR)
    ├── barcode_scanner_screen.dart # 바코드/QR 스캔
    ├── payment_complete_screen.dart # 결제 완료
    ├── payment_history_screen.dart  # 결제 내역
    ├── payment_method_screen.dart   # 결제 수단 관리
    ├── sales_detail_screen.dart     # 매출 상세 (판매자)
    ├── my_page_screen.dart     # 마이페이지
    ├── settings_screen.dart    # 설정
    ├── support_screen.dart     # 고객센터
    └── terms_screen.dart       # 이용약관
```

---

## 🔗 백엔드 연동 가이드

### Base URL 설정
`lib/services/api_service.dart` 파일 생성 후 base URL 설정:
```dart
const String baseUrl = 'https://api.kiopass.com'; // 실제 서버 주소로 변경
```

---

### 1. 인증 (Authentication)

**연동 파일:** `lib/screens/login_screen.dart`, `lib/services/auth_service.dart`

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/auth/kakao` | 카카오 OAuth 로그인 |
| POST | `/auth/naver` | 네이버 OAuth 로그인 |
| POST | `/auth/logout` | 로그아웃 |
| POST | `/auth/refresh` | 토큰 갱신 |

**Request (카카오/네이버 공통)**
```json
{
  "accessToken": "카카오/네이버에서 받은 액세스 토큰"
}
```

**Response**
```json
{
  "accessToken": "JWT 액세스 토큰",
  "refreshToken": "JWT 리프레시 토큰",
  "user": {
    "id": "유저 ID",
    "name": "홍길동",
    "email": "hong@example.com",
    "role": "buyer" // "buyer" | "seller"
  }
}
```

**현재 코드 위치:** `login_screen.dart` → 카카오/네이버 버튼 `onPressed`
```dart
// login_screen.dart 약 55번째 줄
onPressed: () async {
  // TODO: 카카오 OAuth 토큰 받아서 서버로 전송
  // final response = await ApiService.post('/auth/kakao', {'accessToken': token});
  // await AuthService.saveToken(response['accessToken']);
  Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const RoleSelectScreen()));
}
```

**AuthService JWT 교체 필요:**
```dart
// 현재: SharedPreferences에 단순 저장
// 변경: JWT 토큰 저장 및 헤더 자동 주입
static Future<void> saveToken(String accessToken, String refreshToken) async {
  final prefs = await SharedPreferences.getInstance();
  await prefs.setString('accessToken', accessToken);
  await prefs.setString('refreshToken', refreshToken);
}
```

---

### 2. 회원가입 / 판매자 인증

**연동 파일:** `lib/screens/role_select_screen.dart`, `lib/screens/seller_verify_screen.dart`

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/users/register` | 회원가입 |
| POST | `/sellers/verify` | 판매자 서류 제출 |
| GET | `/sellers/verify/status` | 인증 상태 조회 |

**판매자 서류 제출 Request (multipart/form-data)**
```
storeName: "상호명"
address: "매장 주소"
bizImage: File (사업자등록증)
licenseImage: File (영업신고증)
```

**현재 코드 위치:** `seller_verify_screen.dart` → 제출 버튼 `onPressed` (약 215번째 줄)
```dart
// 현재: storeName만 로컬 저장 후 메인으로 이동
// 변경: multipart 요청으로 서류 업로드
onPressed: () async {
  await AuthService.saveStoreName(_nameController.text);
  // TODO: API 연동
  // await ApiService.postMultipart('/sellers/verify', {...});
  Navigator.pushReplacement(...);
}
```

---

### 3. 매장

**연동 파일:** `lib/screens/main_screen.dart`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/stores?lat=&lng=&radius=5000` | 주변 매장 목록 |
| GET | `/stores/:id` | 매장 상세 |

**Response (매장 목록)**
```json
{
  "stores": [
    {
      "id": "store_001",
      "name": "키오패스 편의점 강남점",
      "address": "서울시 강남구 테헤란로 123",
      "lat": 37.5010,
      "lng": 127.0396
    }
  ]
}
```

**현재 코드 위치:** `main_screen.dart` → `_StoreSelectSheetState._allStores` (약 395번째 줄)
```dart
// 현재: 더미 데이터 하드코딩
final List<_StoreData> _allStores = [
  _StoreData(name: '키오패스 편의점 강남점', ...),
  // TODO: API로 교체
  // final response = await ApiService.get('/stores?lat=$lat&lng=$lng&radius=5000');
];
```

---

### 4. 상품 (구매자)

**연동 파일:** `lib/screens/buyer_home_screen.dart`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/stores/:id/products` | 매장 상품 목록 |
| GET | `/stores/:id/products/:barcode` | 바코드로 상품 조회 |
| GET | `/stores/:id/categories` | 카테고리 목록 |

**Response (상품 목록)**
```json
{
  "products": [
    {
      "id": "prod_001",
      "barcode": "8801234567890",
      "name": "코카콜라 500ml",
      "price": 1500,
      "category": "음료",
      "imageUrl": "https://..."
    }
  ]
}
```

**현재 코드 위치:** `buyer_home_screen.dart` → `_products` 더미 리스트 (약 30번째 줄)
```dart
// 현재: 더미 데이터
final List<Product> _products = [...];
// TODO: API로 교체
// final response = await ApiService.get('/stores/$storeId/products');
```

---

### 5. 재고 관리 (판매자)

**연동 파일:** `lib/screens/seller_home_screen.dart` → `_InventoryTabState`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/stores/:id/products` | 상품 목록 |
| POST | `/stores/:id/products` | 상품 추가 |
| PUT | `/stores/:id/products/:pid` | 상품 수정 |
| DELETE | `/stores/:id/products/:pid` | 상품 삭제 |

**현재 코드 위치:** `seller_home_screen.dart` → `_InventoryTabState._products` (약 420번째 줄)
```dart
// 현재: 더미 데이터
final List<Map<String, dynamic>> _products = [...];
// TODO: 각 CRUD 버튼에 API 연동
```

---

### 6. 결제

**연동 파일:** `lib/screens/buyer_home_screen.dart` → 결제 다이얼로그

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/payments` | 결제 요청 |
| GET | `/payments` | 결제 내역 조회 |
| GET | `/payments/:id` | 결제 상세 |
| POST | `/payments/:id/cancel` | 결제 취소 |

**Request (결제 요청)**
```json
{
  "storeId": "store_001",
  "paymentMethod": "kakao",
  "items": [
    {
      "productId": "prod_001",
      "barcode": "8801234567890",
      "name": "코카콜라 500ml",
      "quantity": 2,
      "price": 1500
    }
  ],
  "totalAmount": 3000
}
```

**Response**
```json
{
  "paymentId": "pay_001",
  "status": "success",
  "paidAt": "2026-04-20T14:23:00Z"
}
```

**현재 코드 위치:** `buyer_home_screen.dart` → `_showCartDialog` 내 결제 버튼 (약 870번째 줄)
```dart
// 현재: 결제 완료 화면으로 바로 이동
// TODO: 실제 결제 API 연동
// final response = await ApiService.post('/payments', {...});
```

**연동 파일:** `lib/screens/payment_history_screen.dart`
```dart
// 현재: _history 더미 데이터 (약 30번째 줄)
// TODO: API로 교체
// final response = await ApiService.get('/payments');
```

---

### 7. 매출 (판매자)

**연동 파일:** `lib/screens/sales_detail_screen.dart`, `lib/screens/seller_home_screen.dart` → `DashboardTab`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/stores/:id/sales?period=today` | 매출 조회 |
| GET | `/stores/:id/sales/top-products` | TOP 5 상품 |
| GET | `/stores/:id/sales/transactions` | 거래 내역 |

**period 파라미터:** `today` / `week` / `month` / `3month`

**현재 코드 위치:** `sales_detail_screen.dart` → `_transactions` 더미 (약 25번째 줄)
```dart
// TODO: API로 교체
// final response = await ApiService.get('/stores/$storeId/sales?period=$_selectedPeriod');
```

---

### 8. 결제 수단

**연동 파일:** `lib/screens/payment_method_screen.dart`

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/users/payment-methods` | 연결된 수단 조회 |
| POST | `/users/payment-methods/link` | 수단 연결 |
| DELETE | `/users/payment-methods/:id` | 수단 해제 |
| PUT | `/users/payment-methods/default` | 기본 수단 변경 |

**현재 코드 위치:** `payment_method_screen.dart` → `_methods` 더미 (약 15번째 줄)
```dart
// TODO: API로 교체
// final response = await ApiService.get('/users/payment-methods');
```

---

## 🎨 디자인 시스템

### 색상 토큰 (`app_theme.dart`)
```dart
KColors.primary    // #FF6B4A (오렌지)
KColors.navy       // #122A42 (네이비)
KColors.lightBg    // #EFEFED
KColors.darkBg     // #122A42
KColors.darkSurface  // #1A3248
KColors.darkSurface2 // #1E3A52
```

### 테마 전환
```dart
// main.dart - 전역 테마 노티파이어
themeNotifier.value = ThemeMode.dark;   // 다크
themeNotifier.value = ThemeMode.light;  // 라이트
themeNotifier.value = ThemeMode.system; // 시스템 따라가기
```

---

## 📦 주요 패키지

```yaml
mobile_scanner: ^5.2.3      # 바코드/QR 스캔
geolocator: ^13.0.1         # 위치 기반 매장 탐색
image_picker: ^1.1.2        # 서류 사진 업로드
qr_flutter: ^4.1.0          # QR 코드 생성
shared_preferences: ^2.2.3  # 로컬 저장소
flutter_animate: ^4.5.0     # 애니메이션
image_gallery_saver: ^2.0.3 # QR 이미지 갤러리 저장
```

---

## ⚠️ 연동 전 필수 작업

1. **AuthService JWT 교체** - `lib/services/auth_service.dart`
   - 현재 SharedPreferences 단순 저장 → JWT 액세스/리프레시 토큰 방식으로 교체
   - API 요청 시 `Authorization: Bearer {token}` 헤더 자동 주입

2. **ApiService 클래스 생성** - `lib/services/api_service.dart`
   - base URL 설정
   - 공통 헤더 (Authorization, Content-Type)
   - 토큰 만료 시 자동 갱신 로직
   - 에러 핸들링

3. **더미 데이터 교체** - 각 화면별 TODO 주석 참고

---

## 📱 권한 설정

### iOS (`ios/Runner/Info.plist`)
```xml
NSCameraUsageDescription
NSLocationWhenInUseUsageDescription
NSPhotoLibraryUsageDescription
NSPhotoLibraryAddUsageDescription
```

### Android (`android/app/src/main/AndroidManifest.xml`)
```xml
android.permission.CAMERA
android.permission.ACCESS_FINE_LOCATION
android.permission.ACCESS_COARSE_LOCATION
android.permission.READ_MEDIA_IMAGES
android.permission.READ_EXTERNAL_STORAGE (maxSdkVersion: 32)
android.permission.WRITE_EXTERNAL_STORAGE (maxSdkVersion: 29)
```
