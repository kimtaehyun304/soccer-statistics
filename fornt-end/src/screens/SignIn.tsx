import React, { useContext, useState } from 'react';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import MyHeader from '../my-component/MyHeader';

import { useLocation, useNavigate } from 'react-router-dom'; // useNavigate 가져오기
import { useAuth } from '../my-component/AuthContext';
import MyFooter from '../my-component/MyFooter';

const Login: React.FC = () => {
  const [formData, setFormData] = useState({
    loginId: '',
    password: '',
  });

  const { setIsLoggedIn } = useAuth()

  const navigate = useNavigate(); // navigate 함수 사용
  const location = useLocation();
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // 서버로 전송할 요청 객체
    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData),
    };

    try {
      // 서버에 요청 보내기
      const response = await fetch('https://dldm.kr/api/members/signin', requestOptions);

      if (response.ok) {
        const data = await response.json();
        const sssAccessToken = data.accessToken;
        localStorage.setItem('sssAccessToken', sssAccessToken);
        setIsLoggedIn(true); // 로그인 성공 시 상태 업데이트
        alert('로그인 성공');

        if(location.state === "signup"){
          navigate("/")
          //navigate(-2) 안되지만 이유를 모르겠다
        }
        else
          navigate(-1)

        //console.log(window.history)
        //window.history.back();
        /*
        const referrer = document.referrer;

        if (referrer) {
          const referrerUrl = new URL(referrer); // referrer를 URL 객체로 변환
          const referrerPath = referrerUrl.pathname; // 경로만 추출
          if(referrerPath === "/signup")
            navigate("/signin")
          else
            window.history.back();
        } else {
          console.log("이전 페이지가 없습니다."); // referrer가 없을 때 처리
        }
        */

        //if(document.referrer === )
        //window.history.back();
        //navigate('/'); // 로그인 후 홈으로 이동
      } else {
        alert("로그인 실패");
        console.error('로그인 실패:', response.status);
      }
    } catch (error) {
      console.error('오류 발생:', error);
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <MyHeader />
      <Container maxWidth="xs">
        <Box sx={{ mt: 5 }}>
          <Typography variant="h4" component="h1" align="center" gutterBottom>
            로그인
          </Typography>
          <form onSubmit={handleSubmit}>
            <TextField
              label="로그인 ID"
              name="loginId"
              fullWidth
              margin="normal"
              value={formData.loginId}
              onChange={handleChange}
              required
            />
            <TextField
              label="비밀번호"
              name="password"
              type="password"
              fullWidth
              margin="normal"
              value={formData.password}
              onChange={handleChange}
              required
            />
            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              sx={{ mt: 2 }}
            >
              로그인
            </Button>
          </form>
        </Box>

        <Button
              variant="outlined"
              color="primary"
              fullWidth
              sx={{ mt: 2 }}
              onClick={()=>{navigate("/signup")}}
            >
              회원가입
            </Button>
      </Container>
      <MyFooter/>
    </Box>
  );
};

export default Login;
