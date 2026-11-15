# VetCore 최종 프로젝트

AWS2팀 CORE 최종 프로젝트 저장소입니다. 협약 병원에서만 받을 수 있는 희귀질환 치료를 예약하는 서비스이고, 기존 PetClinic을 확장했습니다.

| 경로 | 내용 |
| --- | --- |
| `app/` | 애플리케이션. [spring-projects/spring-petclinic](https://github.com/spring-projects/spring-petclinic) `500158f` (Apache-2.0)에 예약 기능을 추가 |
| `docs/feature-spec.md` | 기능 명세 |
| `docs/db-design.md` | DB 설계 |
| `docs/handoff.md` | 작업 인계 메모 |

## 실행

Java 17 이상이 필요합니다.

```
cd app
PETCLINIC_STAFF_USERNAME=staff PETCLINIC_STAFF_PASSWORD='<직접 정한 비밀번호>' ./mvnw spring-boot:run
```

- 주소는 `localhost:8080`입니다. 기본은 메모리 DB(H2)라서 끄면 데이터가 사라집니다.
- 직원 계정 환경 변수를 주지 않으면 보호자 화면과 관리 화면을 열 수 없습니다.
- MySQL로 실행하려면 `SPRING_PROFILES_ACTIVE=mysql`과 `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASS`를 줍니다. MySQL 8.0 실행은 아직 검증하지 못했습니다.

## 테스트

```
cd app
./mvnw verify
```

## 참조용 브랜치

`reference/previous-proposals` 브랜치에 이전 기획 문서가 있습니다. 지금 방향과 다른 내용이라 `main`에 병합하지 않습니다.
