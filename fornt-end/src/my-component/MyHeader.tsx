import React from 'react';
import { AppBar, Box, Toolbar, Button, IconButton, Avatar, Tooltip, Menu, MenuItem, Typography, Container } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext'; // AuthContext 사용

const MyHeader: React.FC = () => {
  const { isLoggedIn, setIsLoggedIn } = useAuth(); // 로그인 상태 가져오기
  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);
  const navigate = useNavigate();

  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handleLogout = () => {
    localStorage.removeItem("sssAccessToken");
    setIsLoggedIn(false)
  };

  return (
    <AppBar position="static" sx={{ backgroundColor: '#f8f9fa', boxShadow: 'none', borderBottom: 'none', transition: 'background-color 0.3s ease' }}>
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <Button onClick={() => navigate('/')} sx={{ color: '#343a40', padding: '10px', marginLeft:"10px" }}>
            <img src="/images/sss_logo2.png" style={{width: "70px", height: "70px", filter: "drop-shadow(0 0 10px rgba(0, 0, 0, 0.5))"}}/>
          </Button>
          <Box sx={{ flexGrow: 1 }}>
            <Button onClick={() => navigate('/search')} sx={{ color: '#343a40', padding: '10px', '&:hover': { backgroundColor: 'rgba(0, 0, 0, 0.1)' } }}>검색</Button>
            <Button onClick={() => navigate('/ranking/players')} sx={{ color: '#343a40', padding: '10px', '&:hover': { backgroundColor: 'rgba(0, 0, 0, 0.1)' } }}>랭킹</Button>
          </Box>
          <Box sx={{ flexGrow: 0 }}>
            {isLoggedIn ? (
              <>
                <Tooltip title="Open settings">
                  <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                    <Avatar alt="User Avatar" src="/static/images/avatar/2.jpg" />
                  </IconButton>
                </Tooltip>
                <Menu
                  sx={{ mt: '45px' }}
                  anchorEl={anchorElUser}
                  open={Boolean(anchorElUser)}
                  onClose={handleCloseUserMenu}
                >
                  <MenuItem onClick={handleCloseUserMenu}>Profile</MenuItem>
                  <MenuItem onClick={handleLogout}>Logout</MenuItem>
                </Menu>
              </>
            ) : (
              <Button onClick={() => navigate('/signin')} sx={{ marginRight:"1em",color: '#343a40', padding: '10px', '&:hover': { backgroundColor: 'rgba(0, 0, 0, 0.1)' } }} >로그인</Button>
            )}
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default MyHeader;
