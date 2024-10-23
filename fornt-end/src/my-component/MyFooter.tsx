import React from 'react';
import { Box, Button, Container, Typography } from '@mui/material';

const Footer: React.FC = () => {
  return (
    <Box
      component="footer"
      sx={{
        backgroundColor: '#f8f8f8',
        padding: '16px 0',
        position: 'relative',
        bottom: 0,
        width: '100%',
        textAlign: 'center',
        marginTop: 'auto',
      }}
    >
      <Container maxWidth="lg">
        <Typography variant="body2" color="textSecondary">
          © 2024 SSS - Capstone Design
        </Typography>
        <a href="https://github.com/kimtaehyun304/soccer-statistics" target="_blank" rel="noopener noreferrer">
        <Button>
          <img src="/images/github-icon.png" style={{width:"32px", height:"32px"}}/>
        </Button>
        </a>
      </Container>
    </Box>
  );
};

export default Footer;
