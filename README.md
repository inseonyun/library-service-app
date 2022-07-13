## Library-service-app - 도서 대출 서비스 앱 (관리자)

## 개요
+ 이 앱은 관리자를 위한 앱으로 DB에 책 상태 정보(책명, ISBN, 저자, 수량), 사용자의 상태 정보(대출 현황)등을 조회하고, 사용자의 책 대출/반납 처리를 할 수 있다.
---

## 사용 기술
+ Kotlin, Python, PHP, MySQL, Server(ubuntu-Apache2), Kakao Vision API
---

## 개발 목표
+ Android Studio 플랫폼에서 Kotlin 언어를 주로 사용하며, Kotlin 언어에 대한 이해, 개발 경험을 목표로 한다.
+ Kakao Vision API를 사용하며, Open API 사용 경험을 익힌다.
+ Github Action을 이용하여, 필요한 데이터를 주기적으로 DB에 업로드하며, Github Action 사용 경험을 익힌다.
+ Python requests와 BeautifulSoup를 이용하여 데이터 크롤링 사용 경험을 익힌다.
+ Naver, Kakao와 같은 SNS 로그인 기능 구현을 통해 사용자가 보다 쉽게 앱에 접근할 수 있도록 한다.
---

## 개발 현황
  ### App
    1. 카메라 기능 구현
    2. ISBN 입력 정보 검색을 통해 책 정보 조회
    3. Retrofit2를 이용한 서버와의 통신 기능 구현
    4. Activity로 구현되어 있는 메인 화면 Fragment로 변환 
    5. Navigation Drawer를 이용하여 책 정보 조회, 대출/반납 처리 화면 전환하는 기능 구현
    
  ### Server
    1. ubuntu - Apache2 서버 구축
    2. 앱과의 원활한 통신을 위한 필요 라이브러리, MySQL, PHP, Python 등 설치
    3. PHP, 업로드 Directory 등 접근 권한 수정
    
  ### Data
    1. Python을 이용하여 필요한 데이터(책명, ISBN, 저자) 정보 크롤링
    2. Github Action을 이용하여, 필요한 데이터를 주기적으로 DB에 업로드
    
  ### DataBase
    1. BooStatus 테이블 생성 - 책명, ISBN, 저자, 수량
---

## 현재 개발 단계
### 도서 대출 / 반납 Fragment에서 ISBN 인식 기능 구현
