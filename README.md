## 1단계 - 예외 처리와 응답

### 요구사항 분석

- [x] 어드민 페이지 기능 복원
  - [x] 시간 관리 API 응답 수정
  - [x] 예약 관리 API 응답 수정

- [x] 예외 상황 처리
  - [x] 시간 생성 시 시작 시간에 유효하지 않은 값이 입력되었을 때
  - [x] 예약 생성 시 예약자명, 날짜, 시간에 유효하지 않은 값이 입력 되었을 때
  - [x] 특정 시간에 대한 예약이 존재하는데, 그 시간을 삭제하려 할 때

- [x] 서비스 정책
  - [x] 지나간 날짜와 시간에 대한 예약 생성은 불가능
  - [x] 중복 예약은 불가능
