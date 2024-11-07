
# 훈민정음 2.0 Kotlin 마이그레이션 프로젝트

## 훈민정음 2.0
외국인 대상 한국어 학습 및 모임 서비스
훈민정음 2.0은 세종대왕의 애민정신과 훈민정음 창제의 의미를 이어받아, 한국어를 널리 전파하고자 하는 마음으로 개발된 한국어 교육 서비스입니다. 이 프로젝트는 한국어 학습자들에게 효율적이고 즐거운 학습 경험을 제공하기 위해 다양한 기능을 제공합니다.


#### 1차 개발 기간 (Java) <br> 
2024/09/23 ~ 2024/10/10

#### 2차 개발 및 마이그레이션 기간 (Kotlin) <br> 
2024/10/18 ~ 2024/11/06

## 기능 목록



- **한국어 단어 학습**: 다양한 주제와 난이도의 단어를 학습하며 어휘력을 향상시킬 수 있습니다.
- **위치 기반 모임 찾기**: 주변에서 진행되는 한국어 학습 모임을 쉽게 찾아 참여할 수 있습니다.
- **자유로운 1:1 채팅 기능**: 다른 학습자나 튜터와 자유롭게 대화하며 실시간으로 한국어 실력을 향상시킬 수 있습니다.



## 기술 스택

### Backend <br/>
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=Kotlin&logoColor=white) 
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=flat-square&logo=spring&logoColor=white)
<img src="https://img.shields.io/badge/Spring%20Boot%203.3.4 -6DB33F?style=flat-square&logo=Spring%20Boot&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring%20Data%20JPA%203.3.2-6DB33F?style=flat-square&logo=&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square&logo=Spring Security&logoColor=white">
<img src="https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=JUnit5&logoColor=white">
<img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=JSON-Web-Tokens&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-0?style=flat-square&logo=gradle&logoColor=white&color=%2302303A">

### DB / Infra
<img src="https://img.shields.io/badge/MySQL%208.0.39-4479A1?style=flat-square&logo=MySQL&logoColor=white">  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=Redis&logoColor=white"> <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=flat-square&logo=docker&logoColor=white">


### 성능테스트
<img src="https://img.shields.io/badge/JMeter-D22128?style=flat-square&logo=Apache-JMeter&logoColor=white"> <img src="https://img.shields.io/badge/nGrinder-FFA500?style=flat-square&logo=nGrinder&logoColor=white"> 

### 문서/협업툴
<img src="https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white">
<img src="https://img.shields.io/badge/IntelliJ IDEA-4A154B?style=flat-square&logo=intellijidea&logoColor=white">
<img src="https://img.shields.io/badge/Swagger-0?style=flat-square&logo=Swagger&logoColor=white&color=%2385EA2D">

## 구조
<details>
  <summary>📦 패키지 구조</summary>

```  

├─ src
│  ├─ main
│  │  ├─ kotlin
│  │  │  └─ com
│  │  │     └─ hunmin
│  │  │        ├─ domain
│  │  │        │  ├─ config
│  │  │        │  ├─ controller
│  │  │        │  │  └─ advice
│  │  │        │  ├─ dto
│  │  │        │  │  ├─ board
│  │  │        │  │  ├─ chat
│  │  │        │  │  ├─ comment
│  │  │        │  │  ├─ follow
│  │  │        │  │  ├─ member
│  │  │        │  │  ├─ notice
│  │  │        │  │  ├─ notification
│  │  │        │  │  ├─ page
│  │  │        │  │  └─ word
│  │  │        │  ├─ entity
│  │  │        │  ├─ exception
│  │  │        │  ├─ handler
│  │  │        │  ├─ jwt
│  │  │        │  ├─ redis
│  │  │        │  │  ├─ entity
│  │  │        │  │  ├─ repository
│  │  │        │  │  ├─ sendMessage
│  │  │        │  │  └─ service
│  │  │        │  └─ repositoy
│  │  │        └─ global
│  │  │           ├─ config
│  │  │           ├─ exception
│  │  │           ├─ s3
│  │  │           └─ validate
│  │  └─ resource
│  └─ 
│     └─ kotlin
│        └─ com
│           └─ hunmin
│              └─ domain
│                 ├─ repository
│                 └─ service
└─ uploads
```

</details>

전체 시스템 구조 - 사용자
![image](https://github.com/user-attachments/assets/f215a005-0a1f-4965-a7de-cc9c0ed1e705)

전체 시스템 구조 - 관리자
![image](https://github.com/user-attachments/assets/97f0242b-c178-48c3-ae68-a95105b4fd52)  


<details>
<summary>채팅</summary>

![image](https://github.com/user-attachments/assets/2366cd7f-a491-478d-a456-7da0d1a6f932)


![-Clipchamp5-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/87ad4939-9a2b-410b-a833-0c95081fa962)



 </details>

<details>
<summary>로그인</summary>
  
![스크린샷 2024-11-06 172728](https://github.com/user-attachments/assets/da745a1a-01ad-4923-8802-e958e5bf33cd)
![2 0--Canva-Chrome2024-11-0617-34-58-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/97159c2c-2e1b-41c3-bcbb-ad0804cf1ef1)

</details>

<details>
<summary>게시판, 카카오맵 api, 북마크</summary>

![스크린샷 2024-11-07 오전 9 18 44](https://github.com/user-attachments/assets/2b00f919-5f15-4c66-8174-64ad945f8a24)
- 게시판, 카카오맵 api
  
![ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/15708390-9154-4098-9921-8d624b809e53)

- 북마크
  
![ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/58f058dd-1eb6-4572-9df5-687b50acf23c)


</details>

<details>
<summary>댓글, 좋아요</summary>

![스크린샷 2024-11-07 오전 9 18 57](https://github.com/user-attachments/assets/6640cd5a-b30f-4570-bc82-d89d6b60403c)
![ezgif com-video-to-gif-converter (1)](https://github.com/user-attachments/assets/a1493b5b-b845-4bf9-9a04-da6f9fc3c848)


</details>

<details>
<summary>단어</summary>
  
# 1. 단어사전
<img src="https://github.com/user-attachments/assets/17370713-6124-4fe1-b04e-89bdffc729dc" alt="단어사전 시스템" width="1200">

<img src="https://github.com/user-attachments/assets/5cc3267d-258e-45b8-9884-8b478f703a85" alt="단어사전" width="1200">

### 단어 조회 및 단어 검색

* 5개의 언어가 지원됩니다. (영어, 일본어, 중국어, 베트남어, 프랑스어)
* 사용자는 선택한 언어의 단어 목록을 조회하고 단어의 상세 정보를 조회할 수 있습니다.
* 검색 기능이 있으며 검색하고자하는 단어를 입력하고 언어를 선택해 조회합니다.

### 단어 등록, 수정, 삭제

* 단어 등록, 수정, 삭제는 관리자 권한입니다.
* 관리자 권한이 없다면 403 FORBIDDEN 에러 메세지를 반환합니다.

# 2. 단어학습
<img src="https://github.com/user-attachments/assets/4551e012-75cd-4640-bf5b-05f799ebd70b" alt="단어사전 시스템" width="1200">

<img src="https://github.com/user-attachments/assets/268c43e0-83c0-40f8-ade9-1cb290bb90cb" alt="단어학습" width="1200">

* 5개의 언어가 지원됩니다. (영어, 일본어, 중국어, 베트남어, 프랑스어)
* 사용자가 언어와 레벨을 선택하고, 선택한 언어로 단어 30개가 주어집니다. 또한 선택한 레벨에 따라 학습 시간이 다르게 주어집니다.
* 학습 시간이 다 지나면 사용자는 주어진 단어의 뜻과 의미를 확인할 수 있습니다.

# 3. 단어시험
<img src="https://github.com/user-attachments/assets/4551e012-75cd-4640-bf5b-05f799ebd70b" alt="단어사전 시스템" width="1200">

<img src="https://github.com/user-attachments/assets/a37c3375-0d0d-4c40-8ee6-413054c53158" alt="단어시험" width="1200">

* 5개의 언어가 지원됩니다. (영어, 일본어, 중국어, 베트남어, 프랑스어)
* 사용자가 언어와 레벨을 선택합니다. 선택한 언어로 단어가 제공되며, 선택한 레벨에 따라 문제 수가 다르게 주어집니다.
  
  Level 1 = 25문제
  
  Level 2 = 50문제
  
  Level 3 = 100문제
  
* 시험을 제출하거나 시간이 다 지나면 사용자는 시험 점수와, 랭킹 점수, 정답 개수, 오답 개수를 확인할 수 있습니다.
* 랭킹 점수는 선택한 레벨에 따라 패널티가 반영되어 계산된 후의 점수입니다.

  Level 1 : 패널티 20%

  Level 2 : 패널티 10%

  Level 3 : 패널티 없음
  
# 4. 단어시험 랭킹 조회 및 개인 기록 조회
<img src="https://github.com/user-attachments/assets/4551e012-75cd-4640-bf5b-05f799ebd70b" alt="단어사전 시스템" width="1200">

<img src="https://github.com/user-attachments/assets/8214e379-c382-425e-9e6b-0c2b7b04e3e1" alt="단어시험 랭킹 및 개인 기록 조회" width="1200">

* 랭킹 점수를 바탕으로 언어별 순위가 주어집니다. 또한 전체 및 언어별 TOP100 랭킹 순위를 확인할 수 있습니다.
* 사용자 개인 시험 기록을 확인할 수 있습니다.
  
</details>

<details>
<summary>공지사항</summary>

![image](https://github.com/user-attachments/assets/2775734c-64c1-4ad9-a127-706fef970f7f)

![공지사항 ](https://github.com/user-attachments/assets/56c49dad-a15b-429e-a922-87e42392db4e)


1. 공지사항 작업(생성, 수정, 삭제)을 수행하기 위한 요청이 들어옵니다.<br/>
2. 컨트롤러는 이 요청을 수신하고 SecurityContext에서 인증된 사용자 세부 정보를 추출합니다.<br/>
3. 컨트롤러는 해당 사용자 정보를 서비스 메서드에 전달합니다.<br/>
4. 서비스 메서드는 데이터베이스에서 해당 사용자의 정보를 조회합니다. <br/>
5. 서비스 메서드는 사용자가 관리자인지 확인하고 관리자가 아닐경우 예외를 발생시켜 작업을 중단하고 관리자일 경우 요청된 작업을 진행합니다.<br/>

</details>

<details>
<summary>회원 관리</summary> 
  
![image](https://github.com/user-attachments/assets/46abbfea-a1ae-45e1-bc2a-9eeaca92690b)

![ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/e48cae45-dac0-4523-8480-c247bf16c88a)  



권한 검증 단계

사용자가 AdminController의 메서드를 호출하면 @PreAuthorize("hasAuthority('ADMIN')") 애노테이션이 먼저 동작하여 Spring Security가 현재 사용자의 권한을 확인합니다 <br>
ADMIN 권한이 있으면 -> AdminService 로직 실행 <br>
ADMIN 권한이 없으면 -> 403 Forbidden + 에러 메시지 반환합니다.


</details>


<details>
<summary>알림</summary>

![스크린샷 2024-11-07 오전 9 20 04](https://github.com/user-attachments/assets/c882515e-017c-41ef-8e46-1a30e9d27a69)
![ezgif com-video-to-gif-converter (2)](https://github.com/user-attachments/assets/24be16cc-8efb-4761-a6c6-0d5c06d4e73c)

- 단방향 통신으로 이루어지는 단순 알림이므로 SSE 선택


</details>

<details>
<summary>배포</summary>

![스크린샷 2024-11-07 오전 9 20 38](https://github.com/user-attachments/assets/6b8357ed-a1dd-4591-99e2-323f22c45294)


</details>
<details>
<summary>팔로우</summary>

![image](https://github.com/user-attachments/assets/dc7c6c71-9212-4f00-a8d0-615c91ba867a)
![-Clipchamp4-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/22df1905-5424-469f-a486-abd869831ffb)

- 요청/수락으로 관계형성
- 알림과 차단 기능으로 알림 차단가능


</details>
<details>
<summary>검색</summary>

  ![화면 캡처 2024-11-07 194021](https://github.com/user-attachments/assets/82166345-da12-4a45-a5ec-8e7755951320)
![-Clipchamp2-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/b0c3a43d-81c7-4e65-b462-a02e6b351263)
- 게시글/제목검색

</details>

## 다이어그램
<details>
<summary>ERD</summary>
    
![image](https://github.com/user-attachments/assets/6ed5405c-aecc-4bc7-82c7-c4a0a69ab0c8)


</details>

<details>
<summary>클래스 다이어그램</summary>

![image](https://github.com/user-attachments/assets/41022f7d-af6f-4ce7-9511-10209a371daa)


</details>

<details>
<summary>유스케이스 다이어그램</summary>
  
![USECASE  ](https://github.com/user-attachments/assets/61339a9e-571f-4370-8dbf-33f72ad329d0)

</details>

<details>
<summary>플로우 차트</summary> 
  
![image](https://github.com/user-attachments/assets/48daf714-1a3d-44e5-8570-f0a49b066460)  


</details>



## 팀원 소개

<table>
  <tr>
    <td>
        <a href="https://github.com/kang-ye-jin">
            <img src="https://avatars.githubusercontent.com/u/143896003?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/iam52">
            <img src="https://avatars.githubusercontent.com/u/131854898?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/Dom1046">
            <img src="https://avatars.githubusercontent.com/u/173169283?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/HanJae-Jae">
            <img src="https://avatars.githubusercontent.com/u/177859651?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/DongWooKim4343">
            <img src="https://avatars.githubusercontent.com/u/106728608?v=4" width="100px" />
        </a>
    </td>
  </tr>
  <tr>
    <td><b>강예진</b></td>
    <td><b>오익수</b></td>
    <td><b>김동현</b></td>
    <td><b>한재재</b></td>
    <td><b>김동우</b></td>
  </tr>
  <tr>
    <td><b>게시판, 댓글, <br />알림, CI/CD</b></td>
    <td><b>회원, <br />S3 이미지</b></td>
    <td><b>채팅, 팔로잉, <br />게시글 검색</b></td>
    <td><b>단어 사전, <br />단어 학습</b></td>
    <td><b>공지사항, <br />회원 관리</b></td>
  </tr>
</table>


## 프로젝트 협업 규칙

<details>
<summary>Convention </summary>

💡 이슈 생성 → 브랜치 생성 → 해당 브랜치로 이동 → develop pull → 작업 중간중간 커밋 → 해당 이슈에 대한 작업이 다 완료되면 pr 생성

🚨 커밋 메시지도 템플릿을 지켜주세요 (커밋 메시지 push 전까지는 수정할 수 있어요)

🚨 헷갈리면 push를 멈춰 주세요

🚨 merge 시 충돌을 주의해 주세요 ❗️

🚨 main은 배포중인 브랜치이므로 pr은 develop 으로 부탁드려요

🚨 궁금한 점이 있다면 언제든 같이 해결해요 😊


### [type]

- feat : 새로운 기능 구현
- mod : 코드 및 내부 파일 수정
- add : feat 이외의 부수적인 코드, 파일, 라이브러리 추가
- del : 불필요한 코드나 파일 삭제
- fix : 버그 및 오류 해결
- ui : UI 관련 작업
- chore : 버전 코드, 패키지 구조, 함수 및 변수명 변경 등의 작은 작업
- hotfix : 배포된 버전에 이슈 발생 시, 긴급하게 수정 작업
- rename : 파일이나 폴더명 수정
- docs : README나 Wiki 등의 문서 작업
- refactor : 코드 리팩토링
- merge : 서로 다른 브랜치 간의 병합
- comment : 필요한 주석 추가 및 변경

---

### issue

- 제목
    
    ```java
    [type] 작업 내용 간단히
    ```
    

### branch
  
💡issue 안에서 바로 branch를 만들어주세요 ❗️


```java
feature/#(이슈번호 앞에 붙여주세요!

ex) feature/#1-add-ipa-엔티티-설계
```

### commit message

```java
[type] 작업 내용 간단히

ex) 
[feat] ~~~한 기능 구현 
```

### PR

- 제목
    
    ```java
    [type] 작업 내용 간단히
    ```

### 패키지명

- 소문자

### 패키지 구조

com>도메인 명>www>

- config
- controller>advice
- dto
- entity
- exception
- repository>search
- security
- service
- jwt

= <Test 패키지는 main과 대칭>

- ctrl+shift+t
- 자바 컨벤션 준수

### 기타

- if 중괄호 필수
    
    ```java
    if (condition) {
        // 한 줄이라도
    }
    ```
    
- 클래스, 메서드 주석
    
    ```java
    //간단한 설명
    ```
    

- 로그 : 필요 시 규칙없이 작성
</details>

