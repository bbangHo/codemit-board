# 예외 처리

예외 처리는 `org.example.board.exception` 패키지에서 담당합니다. 서비스 계층은 오류 상황에서 HTTP 응답을 직접 만들지 않고, 의미가 분명한 런타임 예외를 발생시킵니다. `ApiExceptionHandler`가 이를 한 곳에서 받아 동일한 형식의 JSON 응답으로 변환합니다.

## 구성

### `NotFoundException`

요청한 리소스가 존재하지 않을 때 사용합니다.

- 존재하지 않는 회원
- 존재하지 않는 게시글
- 존재하지 않는 댓글

응답 상태 코드는 `404 Not Found`입니다.

### `ConflictException`

현재 요청이 서버의 현재 상태와 충돌할 때 사용합니다.

현재는 이미 가입된 이메일로 회원가입을 시도하는 경우에 발생합니다.

응답 상태 코드는 `409 Conflict`입니다.

### `ForbiddenException`

리소스는 존재하지만 요청한 사용자가 해당 작업을 수행할 권한이 없을 때 사용합니다.

현재는 게시글 또는 댓글의 작성자가 아닌 사용자가 수정·삭제를 시도하는 경우에 발생합니다.

응답 상태 코드는 `403 Forbidden`입니다.

## 입력값 검증

컨트롤러의 `@Valid`와 DTO의 Bean Validation 애너테이션으로 요청 본문을 검증합니다.

- `@NotBlank`: 비어 있거나 공백만 입력된 값 거절
- `@NotNull`: 필수 식별자 누락 거절
- `@Email`: 이메일 형식 검증
- `@Size`: 비밀번호, 닉네임, 제목 길이 검증

검증에 실패하면 `MethodArgumentNotValidException`을 `ApiExceptionHandler`가 처리하며, 응답 상태 코드는 `400 Bad Request`입니다.

## 공통 오류 응답

모든 현재 처리 대상 오류는 다음과 같은 동일한 구조로 응답합니다.

```json
{
  "status": 404,
  "message": "게시글을 찾을 수 없습니다.",
  "timestamp": "2026-09-27T19:00:00"
}
```

| 필드 | 설명 |
| --- | --- |
| `status` | HTTP 상태 코드 |
| `message` | 사람이 읽을 수 있는 오류 메시지 |
| `timestamp` | 오류가 발생한 서버 시각 |

## 예외 처리 흐름

```text
Controller 요청
    ↓
Service에서 도메인 예외 발생
    ↓
ApiExceptionHandler가 예외 수신
    ↓
공통 ErrorResponse와 HTTP 상태 코드 반환
```

예를 들어 존재하지 않는 게시글을 조회하면 `PostService`가 `NotFoundException`을 발생시키고, `ApiExceptionHandler`가 다음 응답을 반환합니다.

```http
HTTP/1.1 404 Not Found
Content-Type: application/json
```

```json
{
  "status": 404,
  "message": "게시글을 찾을 수 없습니다.",
  "timestamp": "2026-09-27T19:00:00"
}
```

## 현재 범위와 후속 작업

현재 `ApiExceptionHandler`는 애플리케이션에서 예상 가능한 오류를 처리합니다. 예상하지 못한 예외는 운영 환경에서 내부 정보가 노출되지 않도록 별도의 `Exception.class` 처리기를 추가하고, 로깅 시스템으로 기록하는 것이 좋습니다.

또한 현재 인증 기능은 구현 전 단계이므로, 실제 로그인 방식을 적용할 때에는 인증되지 않은 요청을 `401 Unauthorized`로 변환하는 보안 예외 처리도 추가해야 합니다.
