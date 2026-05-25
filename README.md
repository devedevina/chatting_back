# 실시간 채팅 애플리케이션 (Spring Boot)

Spring Boot, JPA, Spring Security, STOMP를 이용한 실시간 채팅 애플리케이션 백엔드입니다.

## 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Database**: JPA/Hibernate (H2 for development, MySQL for production)
- **Security**: Spring Security + JWT
- **Real-time Chat**: STOMP WebSocket
- **Build Tool**: Maven
- **Java Version**: 17+

## 프로젝트 구조

```
src/main/
├── java/com/chatting/
│   ├── ChattingApplication.java          # 애플리케이션 시작점
│   ├── config/
│   │   ├── SecurityConfig.java           # Spring Security 설정
│   │   └── WebSocketConfig.java          # WebSocket STOMP 설정
│   ├── controller/
│   │   ├── AuthController.java           # 회원 인증 (가입, 로그인)
│   │   ├── ChatRoomController.java       # 채팅방 관리
│   │   ├── ChatController.java           # WebSocket 메시지 처리
│   │   ├── ReportController.java         # 신고 기능
│   │   └── AdminController.java          # 관리자 기능
│   ├── domain/
│   │   ├── User.java                     # 사용자 엔티티
│   │   ├── ChatRoom.java                 # 채팅방 엔티티
│   │   ├── ChatMessage.java              # 채팅 메시지 엔티티
│   │   ├── ChatRoomMember.java           # 채팅방 멤버 엔티티
│   │   ├── Report.java                   # 신고 엔티티
│   │   └── Warning.java                  # 경고 엔티티
│   ├── dto/
│   │   ├── AuthRequestDto.java           # 로그인 요청
│   │   ├── AuthResponseDto.java          # 인증 응답 (토큰 포함)
│   │   ├── RegisterRequestDto.java       # 회원가입 요청
│   │   ├── ChatRoomRequestDto.java       # 채팅방 생성 요청
│   │   ├── ChatRoomResponseDto.java      # 채팅방 응답
│   │   ├── ChatMessageDto.java           # 채팅 메시지 DTO
│   │   ├── ReportRequestDto.java         # 신고 요청
│   │   └── WebSocketMessageDto.java      # WebSocket 메시지 DTO
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ChatRoomRepository.java
│   │   ├── ChatMessageRepository.java
│   │   ├── ChatRoomMemberRepository.java
│   │   ├── ReportRepository.java
│   │   └── WarningRepository.java
│   ├── security/
│   │   ├── JwtTokenProvider.java         # JWT 토큰 생성/검증
│   │   └── JwtAuthFilter.java            # JWT 인증 필터
│   └── service/
│       ├── AuthService.java              # 인증 로직
│       ├── CustomUserDetailsService.java # 사용자 상세 조회
│       ├── ChatRoomService.java          # 채팅방 로직
│       ├── ChatMessageService.java       # 메시지 로직
│       ├── UserService.java              # 사용자 관리 (경고, 제재)
│       └── ReportService.java            # 신고 로직
└── resources/
    └── application.yml                   # 애플리케이션 설정
```

## 설치 및 실행

### 요구사항
- Java 17+
- Maven 3.6+
- MySQL 8.0+ (프로덕션 환경)

### 빌드

```bash
mvn clean install
```

### 실행

```bash
mvn spring-boot:run
```

서버는 `http://localhost:8080`에서 실행됩니다.

### Swagger UI 접속

서버 실행 후 아래 URL에서 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/api/swagger-ui.html
```

또는 OpenAPI JSON 문서:

```
http://localhost:8080/api/v3/api-docs
```

Swagger UI에서는 모든 API 엔드포인트를 시각적으로 확인하고 직접 테스트할 수 있습니다.

## API 문서

### 인증 관련 API

#### 회원가입
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "password": "password123",
  "nickname": "닉네임",
  "email": "user@example.com"
}
```

**응답:**
```json
{
  "id": 1,
  "username": "user123",
  "nickname": "닉네임",
  "token": "eyJhbGc...",
  "role": "USER"
}
```

#### 로그인
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}
```

**응답:** (회원가입과 동일)

#### 로그아웃
```
POST /api/auth/logout
Authorization: Bearer <token>
```

### 채팅방 관련 API

#### 공개 채팅방 목록 조회
```
GET /api/chat-rooms/public
```

**응답:**
```json
[
  {
    "id": 1,
    "title": "일반 채팅방",
    "description": "설명",
    "creatorNickname": "크리에이터",
    "isPublic": true,
    "isPasswordProtected": false,
    "currentMembers": 3,
    "maxMembers": 10,
    "createdAt": "2024-05-23T10:30:00"
  }
]
```

#### 모든 채팅방 조회 (인증 필요)
```
GET /api/chat-rooms
Authorization: Bearer <token>
```

#### 채팅방 생성 (인증 필요)
```
POST /api/chat-rooms
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "채팅방 제목",
  "description": "설명",
  "isPublic": true,
  "password": "비밀번호 (선택)",
  "maxMembers": 10
}
```

#### 채팅방 입장 (인증 필요)
```
POST /api/chat-rooms/{roomId}/join?password=비밀번호
Authorization: Bearer <token>
```

#### 채팅방 퇴장 (인증 필요)
```
POST /api/chat-rooms/{roomId}/leave
Authorization: Bearer <token>
```

#### 채팅방 삭제 (인증 필요, 방장만)
```
DELETE /api/chat-rooms/{roomId}
Authorization: Bearer <token>
```

### WebSocket API

#### 연결
```
ws://localhost:8080/api/ws-chat
```

#### 메시지 전송
```javascript
// 클라이언트 예시 (JavaScript)
const message = {
  roomId: 1,
  senderNickname: "닉네임",
  content: "메시지 내용"
};

client.send("/app/chat/1/send", {}, JSON.stringify(message));

// 메시지 구독
client.subscribe("/topic/chat/1", function(message) {
  console.log(JSON.parse(message.body));
});
```

### 신고 API (인증 필요)

#### 채팅방 신고
```
POST /api/reports
Authorization: Bearer <token>
Content-Type: application/json

{
  "chatRoomId": 1,
  "reason": "PROFANITY",
  "description": "상세 설명"
}
```

**신고 사유:**
- PROFANITY: 욕설
- ADULT_CONTENT: 음란물
- SPAM: 도배
- ILLEGAL_PROMOTION: 불법 홍보
- FRAUD_SUSPICIOUS: 사기 의심

### 관리자 API

#### 관리자 로그인
- 관리자는 별도로 생성된 admin 계정으로 로그인합니다.
- 권한: `ROLE_ADMIN`

#### 모든 채팅방 조회
```
GET /api/admin/chat-rooms
Authorization: Bearer <admin_token>
```

#### 채팅방 강제 삭제
```
DELETE /api/admin/chat-rooms/{roomId}
Authorization: Bearer <admin_token>
```

#### 대기 중인 신고 조회
```
GET /api/admin/reports
Authorization: Bearer <admin_token>
```

#### 신고 승인
```
POST /api/admin/reports/{reportId}/approve
Authorization: Bearer <admin_token>
```

#### 신고 거절
```
POST /api/admin/reports/{reportId}/reject
Authorization: Bearer <admin_token>
```

#### 사용자 경고
```
POST /api/admin/users/{userId}/warn
Authorization: Bearer <admin_token>

?reason=욕설&description=추가설명
```

#### 사용자 정지 (임시)
```
POST /api/admin/users/{userId}/suspend
Authorization: Bearer <admin_token>

?days=7
```

#### 사용자 영구 정지
```
POST /api/admin/users/{userId}/ban
Authorization: Bearer <admin_token>
```

#### 사용자 정지 해제
```
POST /api/admin/users/{userId}/unsuspend
Authorization: Bearer <admin_token>
```

#### 사용자 경고 이력 조회
```
GET /api/admin/users/{userId}/warnings
Authorization: Bearer <admin_token>
```

## 기능 요구사항 구현 현황

### ✅ 회원 기능
- [x] 회원가입 (아이디, 비밀번호, 닉네임, 이메일)
- [x] 로그인 (JWT 토큰 발급)
- [x] 로그아웃

### ✅ 비회원 기능
- [x] 공개 채팅방 목록 조회
- [x] 채팅방 제목 확인
- [x] 현재 접속 인원 확인
- [x] 공개 여부 확인

### ✅ 채팅방 기능
- [x] 채팅방 생성 (제목, 설명, 공개 여부, 비밀번호, 최대 인원)
- [x] 비밀번호 보호 채팅방
- [x] 채팅방 입장
- [x] 채팅방 퇴장
- [x] 채팅방 삭제 (방장)

### ✅ 실시간 채팅 기능
- [x] 메시지 전송 (STOMP)
- [x] 메시지 수신 (STOMP)
- [x] 시스템 메시지 (입장, 퇴장, 강제 퇴장, 경고)

### ✅ 신고 기능
- [x] 채팅방 신고 (욕설, 음란물, 도배, 불법 홍보, 사기 의심)
- [x] 신고 저장

### ✅ 관리자 기능
- [x] 관리자 로그인
- [x] 전체 채팅방 조회
- [x] 강제 입장 (비밀번호 무시)
- [x] 채팅방 강제 삭제
- [x] 회원 경고 (횟수, 사유, 일시 저장)
- [x] 회원 제재 (채팅 제한, 일정 기간 정지, 영구 이용 제한)

## 추가 구현 예정

### 향후 개선사항
- [ ] 클라이언트 WebSocket 재연결 로직
- [ ] 메시지 읽음 상태 추적
- [ ] 채팅방 검색 기능
- [ ] 친구 추가 / 차단 기능
- [ ] 프로필 이미지
- [ ] 파일 공유 기능
- [ ] 채팅 메시지 저장 정책 (자동 삭제)

## 보안

- JWT 토큰 기반 인증
- BCrypt를 이용한 비밀번호 암호화
- CSRF 보호 비활성화 (REST API용)
- CORS 허용 설정 가능
- 역할 기반 접근 제어 (RBAC)

## 주의사항

### 개발 환경
- H2 인메모리 데이터베이스 사용
- 디버그 로깅 활성화

### 프로덕션 환경
1. `application-prod.yml` 파일 생성 필요
2. JWT Secret Key 변경 필요 (`app.jwtSecret`)
3. MySQL 데이터베이스 설정 필요
4. CORS 설정 변경 필요
5. WebSocket CORS 설정 변경 필요

## 예시 설정 (production)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chatting_db
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.MySQL8Dialect
  
app:
  jwtSecret: your-very-secure-secret-key-here
  jwtExpiration: 86400000
```

## 라이선스

MIT License
