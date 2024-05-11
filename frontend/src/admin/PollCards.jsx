import React, { useState } from 'react';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick-theme.css';
import 'slick-carousel/slick/slick.css';
import { Card, CardContent, Typography, LinearProgress, Container, IconButton, Grid } from '@mui/material';
import HighlightOffOutlinedIcon from '@mui/icons-material/HighlightOffOutlined';


const dummyPolls = [
  {
    id: 1,
    pollId: "poll_1",
    pollName: "Best Creatures on Earth",
    options: [
      { name: 'Polar Bear', percentage: 25 },
      { name: 'American black bear', percentage: 40 },
      { name: 'giant panda', percentage: 30 },
      { name: 'sloth bear', percentage: 5 },
    ],
  },
  {
    id: 2,
    pollId: "poll_2",
    pollName: "Favorite Foods",
    options: [
      { name: 'Apple', percentage: 30 },
      { name: 'Banana', percentage: 25 },
      { name: 'Orange', percentage: 20 },
      { name: 'Grapes', percentage: 25 },
    ],
  },
  {
    id: 3,
    pollId: "poll_3",
    pollName: "Best Travel Destinations",
    options: [
      { name: 'Paris', percentage: 40 },
      { name: 'New York', percentage: 20 },
      { name: 'Tokyo', percentage: 30 },
      { name: 'Barcelona', percentage: 10 },
    ],
  },
  {
    id: 4,
    pollId: "poll_4",
    pollName: "Favorite Sports",
    options: [
      { name: 'Real Madrid', percentage: 25 },
      { name: 'Barcelona', percentage: 40 },
      { name: 'Manchester United', percentage: 30 },
      { name: 'Bayern Munich', percentage: 5 },
    ],
  },
  {
    id: 5,
    pollId: "poll_5",
    pollName: "Favorite Books",
    options: [
      { name: 'Sapiens', percentage: 40 },
      { name: 'Educated', percentage: 20 },
      { name: 'The Immortal Life of Henrietta Lacks', percentage: 30 },
      { name: 'Becoming', percentage: 10 },
    ],
  },
];



const PollCards = () => {
  const [hoveredPoll, setHoveredPoll] = useState(null);

  const handleMouseEnter = (pollId) => {
    setHoveredPoll(pollId);
  };

  const handleMouseLeave = () => {
    setHoveredPoll(null);
  };
  const handleDeletePoll = (pollId) => {
    // Add your delete poll logic here
    console.log(`Deleting poll with ID: ${pollId}`);
  };

  var settings = {
    dots: true,
    infinite: false,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 2,
    initialSlide: 0,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1,
          infinite: true,
          dots: true,
        },
      },
      {
        breakpoint: 600,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1,
          initialSlide: 1,
        },
      },
      {
        breakpoint: 480,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1,
        },
      },
    ],
  };

  return (
    <Container
    style={{
      margin: "0px 0px 0px -50px",
      maxWidth: "1300px",
      // width: "75%",
    }}>
      <Slider {...settings}
       style={{
        // width: "75%",
        margin: "0px 0px 0px -10px",
        //perspective: "1000px",
        transformStyle: "preserve-3d",
      }}>
        {dummyPolls.map((poll) => (
          <Grid item xs={12} sm={6} key={poll.id}>
            <Card
              sx={{
                height: '100%',
                width: '90%', // Adjusted height
                display: 'flex',
                flexDirection: 'column',
                borderRadius: '8px',
                border: '1.5px solid grey',
                boxShadow: '0 1px 4px rgba(0,0,0,0.4)',
                margin: '0px',
                overflow: 'hidden',
                position:'relative'
              }}
              onMouseEnter={() => handleMouseEnter(poll.pollId)}
              onMouseLeave={handleMouseLeave}
            >
             
              <CardContent
              sx={{display:'flex',flexDirection:'column',justifyContent:'space-between',height:'100%',}}>
                <div style={{display:'flex',justifyContent:'space-between',alignItems:'center'}}>
                <Typography variant="h6" sx={{ marginBottom: '10px', fontWeight: 'bold' }}>
                  {poll.pollName}
                </Typography>
                {hoveredPoll === poll.pollId && (
                  <IconButton
                    variant="outlined"
                    color="inherit"
                    size="small"
                    sx={{
                     marginLeft:'auto',marginBottom:'20px'
                    }}
                    onClick={() => handleDeletePoll(poll.pollId)}
                  >
                    <HighlightOffOutlinedIcon />
                  </IconButton>
                )}
                </div>
                {poll.options && (
                  <Card
                    key={poll.id}
                    sx={{
                      height: '100%', // Adjusted height to match the outer card
                      display: 'flex',
                      flexDirection: 'column',
                      borderRadius: '8px',
                      border: '1.5px solid grey',
                      boxShadow: '0 1px 4px rgba(0,0,0,0.4)',
                      overflow: 'hidden',
                      
                    }}
                  >
                    <CardContent>
                      {poll.options.map((option) => (
                        <div key={option.name} sx={{ marginBottom: '1px' }}>
                          <Typography>{option.name}</Typography>
                          <LinearProgress variant="determinate" value={option.percentage} sx={{ marginTop: '.3px' }} />
                          <Typography variant="body2" sx={{ textAlign: 'center', marginTop: '1px' }}>
                            {option.percentage}% Votes
                          </Typography>
                        </div>
                      ))}
                    </CardContent>
                  </Card>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Slider>
    </Container>
  );
};

export default PollCards;