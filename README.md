# 2학기 캡스톤 디자인 수업 - SSS

프톤트 배포: https://dlsss.netlify.app/   
API 서버: https://dldm.kr/swagger-ui/index.html

## 소개
SSS는 축구 4대 리그 기록을 볼 수 있는 웹 사이트입니다. 축구 데이터는 rapidApi-API-FOOTBALL에서 가져왔습니다.  
이 데이터를 통해 선수 프로필, 팀 전적, 랭킹을 조회하는 기능을 만들었습니다.  

# 팀원 
총 4명으로 이루어진 팀 입니다. 저는 백엔드와 프로젝트 통합,배포를 맡았습니다.  
(팀장) 


# 개발 기간
2024-09-10 ~ 

# 프로젝트 스택
## 프론트엔드: mui, react, typescript
*netlify

## 백엔드: spring(boot & security), jpa
*jwt, swagger, logback, gradle  
*aws(ec2 & rds & route53), iteasy, let`s encrypt  

# 설계
[스프링 시큐리티]  
SecurityFilterChain이 제공하는 기본 보안 설정을 사용했습니다.  

[화면]  
스프링 MVC(API)와 react로 설계했습니다. url을 restful하게 지었습니다.  

[디자인]  
Mui을 사용해 반응형 웹으로 만들었습니다.    

[DB]   
MySql-rds, JPA로 설계했습니다.  

## 로직
[회원가입, 로그인]  
비밀번호를 Bcrypt로 해싱하여 db에 저장합니다.  




## 디자인 미리보기
검색페이지 - 홈

![home](https://github.com/user-attachments/assets/bb847464-6c4d-435a-9b5e-f348c8b05fe3)

선수 상세보기

![player](https://github.com/user-attachments/assets/2631c496-45f2-46f2-81e3-68d3f46736be)

팀 상세보기

![team1](https://github.com/user-attachments/assets/455c18c9-117a-475c-936e-c08e0f6a4404)
![team2](https://github.com/user-attachments/assets/958fa288-a3d9-4f81-9ff8-faf999844266)

댓글

![comment](https://github.com/user-attachments/assets/3f5044f1-5616-4792-98d8-5b1e9c2a41e9)

랭킹

![선수 랭킹](https://github.com/user-attachments/assets/9599ebd9-ac84-4cf2-8915-7620ef7267ec)
![팀 랭킹](https://github.com/user-attachments/assets/3e2fef81-c90b-4cfb-8e6a-17418fbacb94)

로그인 & 회원가입

![로그인](https://github.com/user-attachments/assets/01c32c59-0028-475a-988b-c1201a79d16e)
![회원가입](https://github.com/user-attachments/assets/19939de5-11da-4755-9888-4f5d91cd5f55)


