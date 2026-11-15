# 작업 인계 메모

> 작업 중인 에이전트가 갱신합니다. 세션이 끊기면 이 파일부터 읽고 이어서 진행하세요.

## 규칙

- 새 팀은 모든 결정에 근거를 요구합니다. 확인 못 한 것은 "미검증"으로 적습니다.
- 화면 문구는 템플릿에 직접 쓰지 않고 `app/src/main/resources/messages/messages*.properties` 키로 넣습니다 (`I18nPropertiesSyncTest`가 검사). 키는 모든 언어 파일에 넣어야 합니다.
- Java 코드는 `app/`에서 `./mvnw spring-javaformat:apply` 후 `./mvnw verify`로 확인합니다.
- 템플릿에 새 Tailwind 클래스를 쓰면 CSS를 다시 빌드합니다 (`app/src/main/tailwind/README.md`).
- 화면은 팀 Stitch 묶음의 한국어 페이지만 참고합니다.

## 완료 (2026-11-15)

- 로그인·회원가입, 협약 병원, 치료 일정·잔여 정원, 예약·조회·취소·수정
- 보호자 화면(`/owners/**`) 직원 전용, 직원 계정은 환경 변수로 생성
- 관리 화면(`/admin/**`): 병원·일정 등록과 수정
- 디자인 적용: 공통 레이아웃, 홈, 로그인, 나머지 화면은 같은 디자인 값으로 임시 제작
- `./mvnw verify` 통과 (103건, H2 기준)
- 문서: `docs/feature-spec.md`, `docs/db-design.md`

## 남은 일

- MySQL 8.0에서 기동과 예약 흐름 확인 (미검증, 최우선)
- 한글 화면을 실제 브라우저에서 확인
- 사용자가 추가 디자인과 리소스(로고 등)를 주면 교체: 목록은 `docs/feature-spec.md` 9장
- 컨테이너 이미지, 쿠버네티스 매니페스트, Terraform, CI/CD는 시작 전
