# 🍎 ShareEatIt - backend 
'쉐어릿 - 잉여 식량 매칭 서비스' 백엔드 레포지토리입니다.

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FShareEatIt%2FShareEatIt-backend.git&count_bg=%23F2C24E&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)


## 🍎 프로젝트 소개
**쉐어릿으로 남은 음식을 나누고, 나눔을 받아보세요!** <br>
> 쉐어릿(ShareEatIt)은 국내 음식물 쓰레기 문제로 인한 온실가스 배출을 줄이기 위해 설계된 **기부 기반 잉여 식량 매칭 서비스**입니다. <br>
쉐어릿은 ‘나누다’(share)와 ‘먹다’(eat), 그리고 ‘그것’(it)을 결합한 단어로, 잉여 음식 및 식료품을 필요한 사람들에게 나눔으로써 음식물 쓰레기 배출을 줄이는 것을 목표로 합니다.

## 🚀 배포 주소
> **주소** : [https://shareeatit.netlify.app/ ](https://shareeatit.netlify.app/ )<br>


## ⏰ 개발 기간
- Version 1️⃣: 2024.09.03 ~ 2024.12.03
- Version 2️⃣: 2025.01.10 ~ 2025.03.

 
## 📌 주요기능
#### ✔️ 나눔글 생성하기
나눔하려는 음식의 정보와 나눔할 장소 및 시간을 입력하여 나눔글을 작성할 수 있어요.

#### ✔️ 나눔에 참여하기
내 위치를 기반으로 조회된 근처 나눔글 중 원하는 나눔에 채팅하기로 약속을 잡고 나눔을 받을 수 있어요.

#### ✔️ 내 위치 근처 나눔 둘러보기
지도상에서 내 위치를 중심으로 반경 ?km 내 등록된 나눔을 둘러볼 수 있어요.

#### ✔️ 알림받기
내가 지정한 키워드의 나눔글이 등록된 경우, 내 나눔글에 누군가 참여하기를 원하는 경우, 나눔 상태가 변경된 경우 등의 알림을 받을 수 있어요. 이를 통해 나눔을 주고 받는 것 모두 놓치지 않도록 도와드려요.  


## 🛠 기술 스택

### 📌 Backend  
![Java](https://img.shields.io/badge/java%20-007396?style=for-the-badge&logo=java&logoColor=white) 
![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) 
![JPA](https://img.shields.io/badge/JPA-000000?style=for-the-badge&logo=&logoColor=white) 
![QueryDSL](https://img.shields.io/badge/queryDSL-005571?style=for-the-badge&logo=hibernate&logoColor=white)  

### 📌 Database 
![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white) 
![Redis](https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white) 
![MongoDB](https://img.shields.io/badge/mongoDB-47A248?style=for-the-badge&logo=MongoDB&logoColor=white)  

### 📌 DevOps & Infrastructure  
![Docker](https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) 
![Nginx](https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white) 
![AWS](https://img.shields.io/badge/aws-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)  

### 📌 Monitoring & Logging  
![Sentry](https://img.shields.io/badge/sentry-362D59?style=for-the-badge&logo=sentry&logoColor=white)  

### 📌 Version Control  
![GitHub](https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white) 
![Git](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)  

## 시스템 아키텍쳐



## 💫 업데이트 내역

#### 1️⃣ version1
- 카카오 소셜 로그인 제공
- 나눔글, 참여, 마이페이지 등 기본 기능 완성
- 웹소켓 통신을 이용한 채팅 기능 완성
- 지도 및 나의 위치 받아오기 등 위치 기반 기능 완성
  
#### 2️⃣ version2
- 자체 로그인 구현 및 네이버, 구글 소셜 로그인 추가
- 모든 도메인 코드 리팩토링
- 테스트 코드 추가
- QueryDSL 도입 및 DB 최적화
- 로드밸런싱 도입
- 로그 수집 및 모니터링을 위한 Sentry 도입
- 악성 접근 방지를 위한 Nginx 및 Fail2Ban 보안 설정과 AWS WAF 설정


## 🌟 팀 소개
|      정유진        |          이여진         |                                                                                                                  
| :-----------------------------------------------------------------: | :----------------------------------------------------: | 
|   <img width="160px" src="https://github.com/user-attachments/assets/c0e29d87-3bef-4389-b773-239bac550d01" />    |   <img width="160px" src="https://github.com/user-attachments/assets/b356477a-e527-4620-960c-b7c27023453d" />    |  
|   [@sophie_lavender](https://github.com/yujinjeo)   |    [@yeojinLee1020](https://github.com/yeojinLee1020)  | 
| 이화여자대학교 컴퓨터공학과 4학년 | 이화여자대학교 컴퓨터공학과 4학년 | ![KakaoTalk_20240219_195136864](https://github.com/user-attachments/assets/4413ebac-7642-415e-a9e1-199b3ec4c5af)


## 주요 기능 및 트러블 슈팅 기록 


