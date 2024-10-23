import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Box } from '@mui/material';
import MyHeader from '../my-component/MyHeader';
import MyFooter from '../my-component/MyFooter';
import { useNavigate } from 'react-router-dom';

const Signup = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    loginId: '',
    password: '',
  });

  const handleChange = (e: { target: { name: string; value: any; }; }) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e: { preventDefault: () => void; }) => {
    e.preventDefault();

    // 서버로 전송할 요청 객체
    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData),
    };

    try {
      const passwordCheck = document.querySelector('input[name="password2"]') as HTMLInputElement;

      if(formData.password !== passwordCheck.value){
        alert("비밀번호가 일치하지 않습니다")
        return
      }

      // 서버에 요청 보내기
      const response = await fetch('https://dldm.kr/api/members/signup', requestOptions);
      const responseText = await response.text();
      if (response.ok) {
        alert("회원가입 성공")
        navigate("/signin",  { state: 'signup' })
        // 성공 후 원하는 처리 (예: 페이지 이동)
      } else {
        alert(responseText)
        //console.error('회원가입 실패:', response.status);
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
            회원가입
          </Typography>
          <form onSubmit={handleSubmit}>
            <TextField
              label="이름"
              name="name"
              fullWidth
              margin="normal"
              value={formData.name}
              onChange={handleChange}
              required
            />
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
            <TextField
              label="비밀번호 확인"
              name="password2"
              type="password"
              fullWidth
              margin="normal"
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
              회원가입
            </Button>
          </form>
        </Box>
      </Container>
      <MyFooter />
    </Box>
  );
};

export default Signup;
