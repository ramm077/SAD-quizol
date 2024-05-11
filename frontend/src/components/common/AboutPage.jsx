import React from 'react'
import { Container, Typography, Card, CardContent } from '@mui/material'
import Image from '../../images/Valuelabs_img.jpg'

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: '100vh',
    padding: '16px 16px 0px 16px', // Adjust padding as needed
    // backgroundImage: `url(${Image})`, // Replace with your background image URL
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
  },
  card: {
    maxWidth: '750px',
    margin: '60px 5px 2px 5px',
    textAlign: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    borderRadius: '16px',
  },
  title: {
    marginBottom: '8px',
  },
  description: {
    marginBottom: '16px',
  },
  featureList: {
    textAlign: 'left',
    marginLeft: '5px', // Adjust margin as needed
  },
}

const AppDescriptionPage = () => {
  return (
    <Container style={styles.container}>
      <Card style={styles.card} elevation={8}>
        <CardContent>
          <Typography variant="h4" style={styles.title}>
            Welcome to Live Quiz and Polls!
          </Typography>
          <Typography variant="body1" style={styles.description}>
            Transform your virtual experiences with our Live Quiz and Polls
            application, designed to engage and captivate your audience. Whether
            it's a learning environment or a corporate meeting, our platform
            adds an interactive dimension to your sessions.
          </Typography>
          <Typography variant="body1" style={styles.description}>
            Key Features:
          </Typography>
          <div style={styles.featureList}>
            <ul>
              <li>
                <strong>Live Quizzes:</strong> Assess knowledge in real-time
                with interactive quizzes. Create engaging questions and get
                instant feedback from participants.
              </li>
              <li>
                <strong>Dynamic Polls:</strong> Foster audience participation
                with live polls. Collect opinions, conduct surveys, and make
                decisions collaboratively.
              </li>
              <li>
                <strong>User-Friendly Interface:</strong> Our application boasts
                an intuitive and user-friendly design, ensuring that both
                organizers and participants have a seamless experience.
              </li>
              <li>
                <strong>Real-Time Analytics:</strong> Gain valuable insights
                with real-time analytics. Track participant responses and
                engagement levels to tailor your sessions for maximum impact.
              </li>
              {/* Add more features as needed */}
            </ul>
          </div>
          <Typography variant="body1" style={styles.description}>
            Elevate your online events with the power of interactivity. Whether
            you're an educator looking to spice up your virtual classroom or a
            business professional aiming to make meetings more engaging, our
            Live Quiz and Polls application is the perfect solution.
          </Typography>
        </CardContent>
      </Card>
    </Container>
  )
}

export default AppDescriptionPage
