import React from 'react';
import { Link } from 'react-router-dom';
import { Button, Typography, Container } from '@mui/material';
import Image from '../../images/home_img.jpg';

const Home = () => {
  return (
    
    <div style={{ position: 'relative', height: '100vh', overflow: 'hidden' }}>
      {/* Image as background */}
      <div style={{ position: 'absolute', top: 50, left: 0, width: '100%', height: '100%', zIndex: -1, background: `url(${Image})`, backgroundSize: 'cover', backgroundPosition: 'center', minHeight: '100%' }}></div>
      <Container maxWidth="md" style={{ textAlign: 'center', marginTop: '150px', color: 'black' }}>
        <Typography variant="h4" gutterBottom>
          <strong>Explore, Create, and Participate in Quizzes and Polls</strong>
        </Typography>
        <Typography variant="body1" paragraph>
          Welcome to Quizol, the platform where you can discover exciting quizzes, create your own, and engage in polls on various topics.
        </Typography>
        <Typography variant="body1" paragraph>
          Challenge your knowledge, share your expertise, and enjoy interactive polls. Get started now!
        </Typography>
        <Button
          variant="contained"
          color="primary"
          size="large"
          component={Link}
          to="/login"
          style={{ marginTop: '20px', backgroundColor: 'rgba(46,182,174,1)' }}
        >
          Get Started
        </Button>
        <br />
        <br />
        <Typography variant="body1" style={{ marginTop: '10px' }}>
          Join the community and unleash the world of quizzes and polls!
        </Typography>
      </Container>
    </div>
  );
};

export default Home;