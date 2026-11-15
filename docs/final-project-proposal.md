# 최종 프로젝트 기획안


**1. 서비스 새로 구축 or 기존 petclinic에 기능추가해서 사용**
- **결정:** 공식 Spring PetClinic Microservices를 가져와 시나리오용 기능(진료 예약, 사진 업로드)을 추가
- **근거:** 이미 MSA로 나뉘어 있고 Prometheus·Grafana가 포함되어 있다. 반면 MID 코드(Spring 5.3.9, Java 1.8, XML 설정 기반 WAR)를 직접 쪼개는 건 4주 안에 위험하다. 같은 동물병원 도메인이라 MID 시나리오와 실측 기록도 그대로 이어 쓸 수 있다.

**2. 시나리오 구성 방안 (기존 시나리오대로 or 새로운 시나리오)**
- **결정:** 기존 "원장 방송 → 기사화 스파이크"를 이어서 3막으로 확장 (스파이크 → 예약 폭주 → 리전 장애·복구)
- **근거:** MID에 같은 부하의 실측 기록(1,300 VU, 에러율 0.48%, WAS 2→4대)이 있어 새 구조와 수치로 비교할 수 있다. 다만 MID 시나리오는 조회 위주라 백업·복구를 보여 줄 쓰기 구간을 추가한다.

**3. 노션과 깃허브 어떻게 관리할지 생각해오기**
- **결정:** 정보마다 원본 위치를 하나로 고정 (Notion = 기획·결정·회의·작업 / GitHub = 코드·IaC·매니페스트 / 구글 드라이브 = 제출물). GitHub는 `main` 보호 + PR 방식으로 전환
- **근거:** MID 때 `main`은 2022년 상태 그대로였고 팀 작업은 `web`, `web2`, `web3` 브랜치에만 있었으며 PR은 0건이었다. 무엇이 최종본인지 알 수 없었기 때문이다.

**4. 보조 CSP인 GCP를 어떻게 활용하는게 좋은지**
**백업 데이터 보관소 - aws의 데이터를 gcp에 주기적으로 복제**
- **결정:** 백업 보관소(필수) + 복구 대기 환경(Pilot Light). DB는 Cloud SQL에 연속 복제(RPO 5분 이하), S3 객체와 Terraform state는 1시간마다 GCS로 복제
- **근거:** 백업만 하면 "보관"에서 끝나 GCP를 고른 이유가 약하다. Pilot Light로 "GCP에서 다시 서비스할 수 있다"까지 보여 주되, 앱을 상시 띄우지 않아 4주 일정에서 감당할 수 있다. RPO는 MID 피크 구간(약 11분)보다 짧아야 3막이 성립한다.



------------------------------------------------------



## 1. 서비스: 새로 구축 vs 기존 PetClinic 확장

### 비교

| 기준 | A. MID PetClinic을 MSA로 쪼개기 | B. 공식 PetClinic Microservices + 기능 추가 | C. 새 서비스 구축 |
| --- | --- | --- | --- |
| 현재 구조 | 모놀리식 WAR | 이미 MSA | 없음 |
| 기술 버전 | Spring 5.3.9, Java 1.8, XML 설정, JSP | Spring Boot 4.0.1, Java 17 | 자유 |
| MSA로 만드는 작업 | 직접 분해 (가장 큼) | 필요 없음 | 처음부터 설계 |
| 도메인 연속성 | 같음 | 같음 (동물병원) | 없음 |
| 모니터링 자산 | 없음 | Prometheus 설정, Grafana 대시보드 포함 | 없음 |
| 장애 주입 도구 | 없음 | Chaos Monkey 스크립트 포함 | 없음 |
| 독창성 | 낮음 | 낮음 (기능 추가로 보완) | 높음 |

### 추천: **B. 공식 PetClinic Microservices + 기능 추가**

- **MSA 작업이 필요 없다.** `customers`, `vets`, `visits`, `api-gateway`, `config-server`, `discovery-server`로 이미 나뉘어 있다.
- **A는 4주 안에 위험하다.** MID 코드는 XML 설정 기반 WAR라 서비스별로 쪼개려면 사실상 다시 써야 한다.
- **C의 강점인 독창성은 평가 비중이 낮다.** 차별성은 시나리오로 확보한다.
- **모니터링 요구사항에 바로 맞는다.** Prometheus 설정과 Grafana 대시보드가 저장소에 들어 있다.

정할 것:

| 항목 | 제안 | 이유 |
| --- | --- | --- |
| GenAI 서비스 | 제외 | OpenAI나 Azure OpenAI 키가 필요한 외부 의존 |
| DB | RDS for MySQL | 공식 저장소가 MySQL 설정을 제공하고, MID에서도 MySQL을 사용 |
| Eureka, Config Server | 일단 그대로 사용, 2주차에 재검토 | 처음부터 쿠버네티스 방식으로 바꾸면 작업이 늘어남 |
| ElastiCache 세션 저장소 | 필요성 재검토 | MID에서는 Tomcat 세션 공유용이었음. 새 구조에 로그인 세션이 있는지 확인 필요 |

추가할 기능 (우선순위 순):

1. **방송 특별 진료 예약** (`visits` 확장) — 기사를 본 시청자가 예약을 넣는다. DB 쓰기 데이터가 생겨 DB 복제의 RPO를 실측할 수 있다.
2. **수의사·반려동물 사진 업로드** — S3에 저장하고 GCS로 복제하면 객체 백업의 시연 대상이 생긴다.
3. (선택) **실시간 대기 현황** — 예약 폭주 상황을 화면으로 보여 준다.

> 기능을 고른 기준: 시나리오와 이어지고, **쓰기 데이터를 만들 것.** 1번과 2번이 4번의 두 백업 대상(DB, S3)과 정확히 대응한다.

---

## 2. 시나리오 구성

### 선택지

| 방안 | 장점 | 단점 |
| --- | --- | --- |
| A. 기존 시나리오 유지 | 바로 사용 가능 | 조회 위주, 오토스케일링만 보여 줌 → 백업·복구를 설명하지 못함 |
| B. 완전히 새로운 시나리오 | 자유도 높음 | MID와 연결이 끊기고, 비교할 실측 기준이 없음 |
| **C. 기존 시나리오 확장 (추천)** | MID 실측과 수치 비교 가능 + 복구 장면 추가 | 쓰기 구간 설계와 부하 계획 수정 필요 |

### 추천 시나리오 (C안)

> **"원장 방송 시즌 2 — 기사로 몰린 예약, 그리고 서울 리전 장애"**

배경 서사:

1. MID 때처럼 원장이 방송에 나오고, 기사가 퍼지며 접속이 급증한다.
2. 이번에는 시청자들이 조회에 그치지 않고 **특별 진료 예약**을 넣고, 원장 사진이 업로드된다.
3. 그 와중에 서울 리전 장애가 발생한다. 원장: "방송 보고 들어온 예약, 살아 있나요?"
4. 인프라팀이 GCP에 복제된 데이터로 서비스를 복구한다.

배역: 원장, 접수 직원, 인프라팀(팀원별 담당 역할)

| 막 | 내용 | 검증 포인트 | MID와의 관계 |
| --- | --- | --- | --- |
| 1막. 스파이크 | 방송 → 기사화로 트래픽 급증 | 쿠버네티스 오토스케일링 | **MID와 같은 부하 형태로 실행해 EC2 Auto Scaling과 수치 비교** |
| 2막. 예약 폭주 | 예약 생성, 사진 업로드 | 쓰기 경로, 복제 지연 | 신규 |
| 3막. 리전 장애 | 장애 모사 → GCP에서 복구 | 실제 RTO, 실제 RPO (장애 직전 예약이 남았는지) | 신규 |

MID 비교 기준 (Notion "시나리오 (상세)", 2026-09-28 실행):
- 1,300 VU, 150 RPS로 95,121건을 처리했고 에러율은 0.48%였다.
- WAS는 2대에서 4대로, WEB은 2대에서 3대로 늘었다.

> MID JMeter 계획은 기존 페이지 경로를 대상으로 하므로 새 구조의 API 경로로 다시 써야 한다. 부하 형태(VU, RPS, 구간)는 그대로 쓴다. 실제 리전 장애는 만들 수 없으므로 모사 방법을 미리 정하고 발표에서도 명시한다.

---

## 3. Notion & GitHub 관리 방안

### 역할 분리 원칙

| 정보 | 원본 위치 | 다른 곳에서는 |
| --- | --- | --- |
| 기획, 결정, 회의, 멘토링 | Notion | 링크만 |
| 작업과 일정 | Notion 작업 DB (`project=final`) | PR 본문에 Notion 작업 링크 |
| 코드, Terraform, Ansible, 매니페스트 | GitHub | Notion에는 저장소 링크 |
| 제출물 (PPT, 시연 영상) | 구글 드라이브 | Notion에 링크 |

- 같은 내용을 두 곳에 쓰지 않는다. 진행 상황도 Notion 작업 DB 한 곳에서만 관리한다.
- 제출물은 평가자가 확인하는 구글 드라이브 폴더가 원본이다.

### Notion 구조

기존 팀 페이지 구조를 그대로 쓰고 Final용 속성만 추가한다.

```
AWS 2Team (CORE)
├─ FIN-Project (허브)
├─ 인프라 최종 구성안 — 결정 문서 (결정 1건마다 이유와 출처를 기록)
├─ Tasks DB — 기존 속성(project, 담당자, 마감일, 상태, 회의) + "PR" URL 속성 추가
├─ Meeting minutes — 결정 사항은 "인프라 최종 구성안"으로 옮김
├─ Mentoring
└─ Settings — GitHub, 구글 드라이브, Meet 링크
```

### GitHub 구조

**레포 구성 (3개)**

```
bespin-CORE/
├─ app/      # PetClinic Microservices fork + 추가 기능, GitHub Actions(CI)
├─ infra/    # terraform/ (aws, gcp), ansible/
└─ gitops/   # 쿠버네티스 매니페스트 — ArgoCD가 읽는 유일한 저장소
```

> 설정 저장소와 소스 저장소 분리는 Argo CD 공식 문서의 권장 사항이다.

**규칙**

| 항목 | 규칙 |
| --- | --- |
| 브랜치 | `main`(보호, 배포 기준) / 짧은 기능 브랜치 `feat/...`, `fix/...`, `infra/...` |
| `main` 보호 | 직접 push 금지, PR 필수. 공개 저장소는 무료 요금제에서도 가능 |
| 커밋 | `feat:`, `fix:`, `infra:`, `docs:`, `test:` (MID 때 일부 사용하던 형식) |
| PR | 1명 이상 리뷰 후 머지, 본문에 Notion 작업 링크, Terraform은 `plan` 결과 첨부 |
| 빌드 결과물 | jar, war, 이미지 파일은 커밋하지 않음 (이미지는 ECR에 저장) |
| 보안 | 키·비밀번호 커밋 금지 → GitHub Secrets, AWS Secrets Manager / GCP Secret Manager |
| Terraform state | AWS는 S3(`use_lockfile` 잠금), GCP는 GCS에 분리. AWS 장애 중에도 GCP 쪽을 다룰 수 있어야 함 |
| CI/CD | PR 시 빌드·테스트, `main` 머지 시 이미지를 ECR과 Artifact Registry에 푸시 → ArgoCD가 `gitops` 반영 |

---

## 4. 보조 CSP(GCP) 활용 방안

### 왜 Azure가 아니라 GCP인가

| 근거 | 내용 |
| --- | --- |
| AWS 공식 멀티클라우드 연결의 첫 파트너 | AWS Interconnect - multicloud가 2026-04에 Google Cloud와 GA로 출시됨. Azure는 퍼블릭 프리뷰 단계로 보도됨 |
| AWS 데이터를 원본으로 바로 받는 GCP 공식 도구 | DMS가 RDS for MySQL(5.6~9.7)에서 Cloud SQL로의 연속 복제를 지원. Storage Transfer Service가 S3를 원본으로 지원 |
| 과정 내 GCP 경험 | 이 PetClinic은 "GCP Cloud 과제 수행을 위한" 앱이고, Cloud Build로 WAR를 GCS에 올리는 설정이 있음 |

> Azure에도 비슷한 도구가 있을 수 있으며 이번에는 조사하지 않았다. "이 조건에서 GCP 쪽 공식 경로가 확인됐다"는 뜻이다.

### 활용 옵션 비교

| 방안 | 설명 | 복구 시간 (AWS DR 분류) | 4주 일정 |
| --- | --- | --- | --- |
| ① 백업 보관소 | DB, S3 객체, Terraform state를 GCS에 주기 복제 | 수 시간 | 쉬움 |
| **② Pilot Light** | ① + Cloud SQL에 DB 상시 복제, 장애 시 GKE에 앱 기동 | 수십 분 | 가능 |
| ③ Warm Standby | ② + GKE에 앱 상시 가동 | 수 분 | 부담 큼 |
| ④ Active-Active | AWS·GCP 동시 서비스 | 거의 즉시 | 양방향 쓰기 충돌로 범위 초과 |

### 추천: **① 백업 보관소 + ② Pilot Light**

- ①만 하면 GCP는 "보관하는 곳"에서 끝난다.
- ②를 더하면 "GCP에서 다시 서비스한다"까지 보여 줄 수 있고, 3막 시나리오와 바로 이어진다.
- 앱을 상시 띄우지 않으므로 ③보다 구현과 운영 부담이 작다.

### 구성안 (AWS Primary / GCP Pilot Light)

```
                 사용자
                   │
        Route 53 (Failover 라우팅 + Health Check)
           │ Primary                     │ Secondary (복구 후 전환)
           ▼                             ▼
   ┌──── AWS (서울) ────┐        ┌──── GCP (서울 asia-northeast3) ────┐
   │ ALB → EKS (App)    │        │ GKE — 평소 앱 없음, 장애 시 기동    │
   │ RDS MySQL (Primary)│ ─DMS 연속 복제─▶ Cloud SQL MySQL (상시)     │
   │ S3 (사진)          │ ─STS 1시간─▶ GCS (Standard)                 │
   │ S3 (tfstate)       │ ─STS 1시간─▶ GCS                            │
   │ ECR                │ ─CI 동시 푸시─▶ Artifact Registry           │
   └────────────────────┘        └────────────────────────────────────┘
        (AWS Site-to-Site VPN ↔ GCP HA VPN 사설 연결)
```

### 백업 대상과 RPO

| 대상 | 방식 | RPO 목표 | 이유 |
| --- | --- | --- | --- |
| DB | DMS 연속 복제 → Cloud SQL | **5분 이하** | MID 피크 구간이 약 11분(램프업 1분 + 유지 10분). RPO가 이보다 길면 "방송 직후 예약"이 손실 범위에 들어가 3막이 성립하지 않음 |
| DB (대체안) | `mysqldump`를 1시간마다 GCS로 | 1시간 | DMS 검증이 실패할 때 사용 |
| S3 객체 | Storage Transfer Service 정기 실행 | 1시간 | STS 정기 실행의 최소 간격이 1시간 |
| Terraform state | S3 → GCS 복제 (같은 STS 작업) | 1시간 | 실제 리소스 매핑 정보는 state에만 있음 |
| Terraform 코드 | 별도 백업 없음 | — | 이미 GitHub에 있어 AWS와 독립적 |

- **GCS 등급은 Standard로 한다.** Nearline(30일), Coldline(90일)에는 최소 보관 기간이 있어 일찍 지우면 요금이 붙는다. 프로젝트 후에는 데이터를 보존하지 않는다.
- GCP 비용은 주최사가 부담하지만, S3에서 읽어 가는 전송 요금은 AWS 쪽에 청구된다.
- RPO 5분은 목표치다. 실제 값은 리허설에서 측정해 발표 자료에 쓴다.

### 복구 절차 (런북 초안)

1. 장애를 감지하면 담당자가 상황과 복제 지연 값을 확인한다.
2. Cloud SQL을 Primary로 승격한다. 승인 절차를 거친다.
   - 이유: 오탐으로 승격하면 양쪽 DB가 모두 쓰기 가능해진다.
3. GKE에 Artifact Registry의 이미지로 앱을 기동한다.
4. Route 53을 GCP로 전환한다.
5. 장애 직전 예약이 남았는지 확인하고 실제 RTO/RPO를 기록한다.
6. 원복: AWS가 복구되면 점검 시간에 GCP 데이터를 덤프·복원으로 RDS에 옮긴다. DMS는 Cloud SQL로 보내는 단방향이라 역방향에는 쓸 수 없다. 이후 DMS를 다시 구성하고 DNS를 원복한다.

### 1주차 검증

| 항목 | 성공 기준 | 실패 시 |
| --- | --- | --- |
| RDS MySQL → Cloud SQL DMS 복제 | 변경이 5분 안에 반영 | `mysqldump` 1시간 방식 |
| AWS↔GCP 사설 연결 (VPN) | DMS가 사설 경로로 연결 | 공인 IP와 TLS 연결 검토 |
| S3 → GCS STS 복제 | 1시간 주기 복제 확인 | 수동 동기화 스크립트 |



------------------------------------------------------



## 출처

**팀 자료**
- [bespin-CORE/petclinic_btc](https://github.com/bespin-CORE/petclinic_btc): 브랜치별 `pom.xml`, 커밋 이력, PR·이슈 수
- [bespin-nova/WAS](https://github.com/bespin-nova/WAS): README, `cloudbuild.yaml`
- Notion AWS 2Team (CORE): 시나리오 (상세), 인프라 최종 구성안, 작업 DB, Settings

**공식 문서**
- [spring-petclinic-microservices](https://github.com/spring-petclinic/spring-petclinic-microservices): 모듈 구성, `pom.xml`, README, `docker/`, `scripts/chaos`
- [AWS Interconnect - multicloud GA](https://aws.amazon.com/about-aws/whats-new/2026/04/aws-announces-ga-AWS-interconnect-multicloud/)
- [DMS for MySQL 지원 원본](https://docs.cloud.google.com/database-migration/docs/mysql/migration-src-and-dest), [DMS for MySQL 개요](https://docs.cloud.google.com/database-migration/docs/mysql)
- [Storage Transfer Service: S3](https://docs.cloud.google.com/storage-transfer/docs/create-transfers/agentless/s3), [일정](https://docs.cloud.google.com/storage-transfer/docs/schedule-transfer-jobs)
- [GCS 가격 (최소 보관 기간)](https://cloud.google.com/storage/pricing)
- [AWS DR 백서](https://docs.aws.amazon.com/id_id/whitepapers/latest/disaster-recovery-workloads-on-aws/disaster-recovery-workloads-on-aws.pdf), [Well-Architected REL13-BP02](https://docs.aws.amazon.com/wellarchitected/2022-03-31/framework/rel_planning_for_recovery_disaster_recovery.html)
- [GitHub 보호 브랜치](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [Terraform S3 backend](https://developer.hashicorp.com/terraform/language/backend/s3)
- [Argo CD Best Practices](https://argo-cd.readthedocs.io/en/stable/user-guide/best_practices/)

**2차 자료 (재확인 필요)**
- [Azure Interconnect 프리뷰 보도](https://gcn.com/?p=21684)
