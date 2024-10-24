
# 코틀린 마이그레이션

## 훈민정음 2.0
외국인 대상 한국어 학습 및 모임 서비스
훈민정음 2.0은 세종대왕의 애민정신과 훈민정음 창제의 의미를 이어받아, 한국어를 널리 전파하고자 하는 마음으로 개발된 한국어 교육 서비스입니다. 이 프로젝트는 한국어 학습자들에게 효율적이고 즐거운 학습 경험을 제공하기 위해 다양한 기능을 제공합니다.

## 기능 목록



- **한국어 단어 학습**: 다양한 주제와 난이도의 단어를 학습하며 어휘력을 향상시킬 수 있습니다.
- **위치 기반 모임 찾기**: 주변에서 진행되는 한국어 학습 모임을 쉽게 찾아 참여할 수 있습니다.
- **자유로운 1:1 채팅 기능**: 다른 학습자나 튜터와 자유롭게 대화하며 실시간으로 한국어 실력을 향상시킬 수 있습니다.



## 기술 스택

### Backend <br/>
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white) 
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=flat-square&logo=spring&logoColor=white)
<img src="https://img.shields.io/badge/Spring%20Boot%203.3.4 -6DB33F?style=flat-square&logo=Spring%20Boot&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring%20Data%20JPA%203.3.2-6DB33F?style=flat-square&logo=&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square&logo=Spring Security&logoColor=white">
<img src="https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=JUnit5&logoColor=white">
<img src="https://img.shields.io/badge/Lombok-FF0000?style=flat-square&logo=Lombok&logoColor=white">
<img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=JSON-Web-Tokens&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-0?style=flat-square&logo=gradle&logoColor=white&color=%2302303A">

### DB / Infra
<img src="https://img.shields.io/badge/MySQL%208.0.39-4479A1?style=flat-square&logo=MySQL&logoColor=white">  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=Redis&logoColor=white"> <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=flat-square&logo=docker&logoColor=white">


### 성능테스트
<img src="https://img.shields.io/badge/JMeter-D22128?style=flat-square&logo=Apache-JMeter&logoColor=white">

### 문서/협업툴
<img src="https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white">
<img src="https://img.shields.io/badge/IntelliJ IDEA-4A154B?style=flat-square&logo=intellijidea&logoColor=white">
<img src="https://img.shields.io/badge/Swagger-0?style=flat-square&logo=Swagger&logoColor=white&color=%2385EA2D">

## 구조
<details>
  <summary>📦 패키지 구조</summary>

├─ src<br/>
│  ├─ main <br/>
│  │  ├─ java <br/>
│  │  │  └─ com <br/>
│  │  │     └─ hunmin <br/>
│  │  │        └─ domain <br/>
│  │  │           ├─ config <br/>
│  │  │           ├─ controller <br/>
│  │  │           │  └─ advice     <br/>
│  │  │           ├─ dto<br/>
│  │  │           │  ├─ board <br/>
│  │  │           │  ├─ chat  <br/>
│  │  │           │  ├─ comment <br/>
│  │  │           │  ├─ member <br/>
│  │  │           │  ├─ notice <br/>
│  │  │           │  ├─ notification <br/>
│  │  │           │  ├─ page<br/>
│  │  │           │  └─ word<br/>
│  │  │           ├─ entity<br/>
│  │  │           ├─ exception<br/>
│  │  │           ├─ handler<br/>
│  │  │           ├─ json<br/>
│  │  │           ├─ jwt<br/>
│  │  │           ├─ pubsub<br/>
│  │  │           ├─ repository<br/>
│  │  │           │  └─ search<br/>
│  │  │           └─ service<br/>
│  │  └─ resources<br/>
│  │     ├─ static<br/>
│  │     │  ├─ images<br/>
│  │     └─ templates<br/>
│  │        └─ message<br/>
│  └─ test<br/>
│     └─ java<br/>
│        └─ com<br/>
│           └─ hunmin<br/>
│              └─ domain<br/>
│                 ├─ repository<br/>
│                 └─ service<br/>
└─ uploads

</details>

전체 시스템 구조 - 사용자
![image](https://github.com/user-attachments/assets/f215a005-0a1f-4965-a7de-cc9c0ed1e705)

전체 시스템 구조 - 관리자
![image](https://github.com/user-attachments/assets/f6289385-2c9d-4d5c-b785-269c6f55425f)


<details>
<summary>채팅</summary>
    
채팅 시스템 구조
![image](https://github.com/user-attachments/assets/2366cd7f-a491-478d-a456-7da0d1a6f932)

## 1. 채팅 구현 기술


### **WebSocket + STOMP + Redis Pub/Sub**

-  웹소켓 기술에 STOMP 기술을 곁들여 보다 편리한 메세지 전송을 구현
-  PUB/SUB 을 붙인 엔드포인트로 간단히 수신자/송신자를 구별
-  서버 확장성을 위한 redis를 이용, 서버간 데이터 전송을 구현 + 인메모리 영역 저장소 활용으로 빠른 성능을 기대

- **특징**:
    - 양방향 통신으로 편리한 서비스 구현
    - 편리한 실시간 메세지 송수신 구현
    - Redis를 이용한 빠른 성능 구현
    - 서버간 데이터 전송으로 서버 확장성 구현
      

### 2. 데이터의 흐름


 데이터 흐름: 클라이언트 -> 웹소캣 -> 컨트롤러 -> 서비스 -> stomp handler -> security filter -> stomp.send -> redis -> server -> 클라이언트 

 기술 흐름: Publisher -> 웹소캣 -> Stomp -> Redis -> data save -> Stomp -> 웹소캣 -> Subscriber
 
 </details>

<details>
<summary>로그인</summary>
  
![image](https://github.com/user-attachments/assets/96eeda40-6137-411c-9079-340c68ba8ef9)
![ezgif com-resize](https://github.com/user-attachments/assets/6a277246-c0db-4a2f-8558-216723b7968c)

</details>

<details>
<summary>게시판, 카카오맵 api</summary>

![image](https://github.com/user-attachments/assets/c2e09ee3-78ce-43b8-adac-10a4f99e22c7)
![2-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/de56f762-6238-406a-b4d1-f6c2848e99c5)

</details>

<details>
<summary>댓글</summary>

![image](https://github.com/user-attachments/assets/5eee0b1c-330b-4f47-b7ef-b3b62c6610ca)
![ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/d77f8df5-d258-4002-9cb6-491f8951a871)

</details>

<details>
<summary>단어학습</summary>

![image](https://github.com/user-attachments/assets/e69f97e1-72d0-48a5-8733-c7f03316762f)
![단어](https://github.com/user-attachments/assets/05f228b0-6642-4256-a2f3-7d3d8871cada)

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
<summary>알림</summary>

![image](https://github.com/user-attachments/assets/9a2a6f92-4cfa-49dd-8116-aeada072c9b3)

![image](https://github.com/user-attachments/assets/e94a41af-4d57-4c2a-ae2b-7aa22b46081f)

![3-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/28753421-d3b6-45e7-bb09-c3b299c94141)



</details>

## 다이어그램
<details>
<summary>ERD</summary>
    
![image](https://github.com/user-attachments/assets/7fd35a00-f121-44e2-9c33-302baefb44bd)

</details>

<details>
<summary>클래스 다이어그램</summary>

![hunmin](https://github.com/user-attachments/assets/566f6dd3-dcaf-4e60-ab9e-0138137f2aa6)

</details>

<details>
<summary>유스케이스 다이어그램</summary>
  
![USECASE  ](https://github.com/user-attachments/assets/61339a9e-571f-4370-8dbf-33f72ad329d0)

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
    <td><b>게시판, 댓글, 알림</b></td>
    <td><b>회원</b></td>
    <td><b>채팅</b></td>
    <td><b>단어학습</b></td>
    <td><b>공지사항</b></td>
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

