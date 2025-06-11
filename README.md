# 소개
soccer-statistics는 축구 4대 리그 플랫폼입니다. 선수와 팀의 전적, 통계, 랭킹을 제공합니다.  
리그 데이터는 rapid-api에서 가져왔습니다. 


본인 역할 (팀원 4명)  
저는 혼자 백엔드를 맡고, 프론트는 홈, 회원가입/로그인을 맡았습니다.  

개발 기간  
2024-09-10 ~ 12-10

프로젝트 스택  
<ul>
  <li>react18 / typeScript / mui  </li>
  <li>spring (boot3 / security6) / hibernate 6 / swagger</li>
</ul>

### 어필
<ul>
  <li>
    매일 rapid api 호출하여 데이터 갱신 (@Schedule)<br>
    → EC2 시간이 UTC로 돼있어서 의도치 않은 시간에 @Schedule 동작<br>
    → KST로 변경
  </li>
    <li>연합뉴스 크롤링하여 db에 저장</li>
</ul>

### 개선
<ul>
  <li>
    지금 코드는 bindingResult 사용 중 → 공통 예외처리로 바꾸자
  </li>
</ul>


### UI/UX
<h4 align="center">검색 페이지</h4>
<p align="center">
<img src="https://github.com/user-attachments/assets/bb847464-6c4d-435a-9b5e-f348c8b05fe3" width="70%" height="70%"/>
</p>

<h4 align="center">선수 상세보기</h4>
<p align="center">
<img src="https://github.com/user-attachments/assets/2631c496-45f2-46f2-81e3-68d3f46736be" width="70%" height="70%"/>
</p>

<h4 align="center">팀 상세보기</h4>
<p align="center">
<img src="https://github.com/user-attachments/assets/455c18c9-117a-475c-936e-c08e0f6a4404" width="70%" height="70%"/>
</p>
<p align="center">
<img src="https://github.com/user-attachments/assets/958fa288-a3d9-4f81-9ff8-faf999844266" width="70%" height="70%"/>
</p>
<p align="center">
<img src="https://github.com/user-attachments/assets/3f5044f1-5616-4792-98d8-5b1e9c2a41e9" width="70%" height="70%"/>
</p>

<h4 align="center">랭킹</h4>
<p align="center">
<img src="https://github.com/user-attachments/assets/9599ebd9-ac84-4cf2-8915-7620ef7267ec" width="70%" height="70%"/>
</p>
<p align="center">
<img src="https://github.com/user-attachments/assets/3e2fef81-c90b-4cfb-8e6a-17418fbacb94" width="70%" height="70%"/>
</p>




