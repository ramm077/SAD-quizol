import React, { useState } from 'react';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick-theme.css';
import 'slick-carousel/slick/slick.css';
import { Card, CardContent, Typography, LinearProgress, Container, Grid, Radio, Button } from '@mui/material';

// const initialDummyPolls = [
//   {
//     id: 1,
//     pollId: "poll_1",
//     pollName: "Best Creatures on Earth",
//     options: [
//       { name: 'Polar Bear', votes: 0, percentage: 0 },
//       { name: 'American black bear', votes: 2, percentage: 25 },
//       { name: 'giant panda', votes: 1, percentage: 12.5 },
//       { name: 'sloth bear', votes: 5, percentage: 62.5 },
//     ],
//   },
//   {
//     id: 2,
//     pollId: "poll_2",
//     pollName: "Favorite Foods",
//     options: [
//       { name: 'Apple', votes: 0, percentage: 0 },
//       { name: 'Banana', votes: 0, percentage: 0 },
//       { name: 'Orange', votes: 0, percentage: 0 },
//       { name: 'Grapes', votes: 0, percentage: 0 },
//     ],
//   },
//   {
//     id: 3,
//     pollId: "poll_3",
//     pollName: "Best Travel Destinations",
//     options: [
//       { name: 'Paris', votes: 0, percentage: 0 },
//       { name: 'New York', votes: 0, percentage: 0 },
//       { name: 'Tokyo', votes: 0, percentage: 0 },
//       { name: 'Barcelona', votes: 0, percentage: 0 },
//     ],
//   },
//   {
//     id: 4,
//     pollId: "poll_4",
//     pollName: "Favorite Sports",
//     options: [
//       { name: 'Real Madrid', votes: 15, percentage: 25 },
//       { name: 'Barcelona', votes: 24, percentage: 40 },
//       { name: 'Manchester United', votes: 18, percentage: 30 },
//       { name: 'Bayern Munich', votes: 3, percentage: 5 },
//     ],
//   },
//   {
//     id: 5,
//     pollId: "poll_5",
//     pollName: "Favorite Books",
//     options: [
//       { name: 'Sapiens', votes: 40, percentage: 40 },
//       { name: 'Educated', votes: 20, percentage: 20 },
//       { name: 'The Immortal Life of Henrietta Lacks', votes: 30, percentage: 30 },
//       { name: 'Becoming', votes: 10, percentage: 10 },
//     ],
//   },
//   {
//     id: 6,
//     pollId: "poll_6",
//     pollName: "Best Workout",
//     options: [
//       { name: 'Burpies', votes: 2, percentage: 13.33 },
//       { name: 'Planks', votes: 1, percentage: 6.66 },
//       { name: 'Walking', votes: 5, percentage: 33.33 },
//       { name: 'Dance', votes: 7, percentage: 46.6 },
//     ],
//   },
// ];

const UserPolls = () => {
  const [selectedOptions, setSelectedOptions] = useState({});
  const [polls, setPolls] = useState(initialDummyPolls);
  const [isSubmittedMap, setIsSubmittedMap] = useState({});

  const handleChooseOption = (pollId, option) => {
    if (!isSubmittedMap[pollId]) {
      setPolls((prevPolls) => {
        const updatedPolls = [...prevPolls];
        const pollIndex = updatedPolls.findIndex((poll) => poll.pollId === pollId);

        // Reset selected options for the poll
        updatedPolls[pollIndex].options.forEach((opt) => {
          opt.votes -= opt.name === selectedOptions[pollId] ? 1 : 0;
        });

        const optionIndex = updatedPolls[pollIndex].options.findIndex((opt) => opt.name === option.name);

        // Increment votes for the selected option
        updatedPolls[pollIndex].options[optionIndex].votes += 1;

        // Update percentages
        const totalVotes = updatedPolls[pollIndex].options.reduce((total, opt) => total + opt.votes, 0);
        updatedPolls[pollIndex].options.forEach((opt) => {
          opt.percentage = (opt.votes / totalVotes) * 100;
        });

        return updatedPolls;
      });

      setSelectedOptions((prevSelectedOptions) => ({
        ...prevSelectedOptions,
        [pollId]: option.name,
      }));
    }
  };

  const handleSubmit = (pollId) => {
    if (!selectedOptions[pollId]) {
      alert('Please choose an option before submitting.');
      return;
    }

    setPolls((prevPolls) => {
      const updatedPolls = [...prevPolls];
      const pollIndex = updatedPolls.findIndex((poll) => poll.pollId === pollId);

      // Move the submitted poll card to the end
      const submittedPoll = updatedPolls.splice(pollIndex, 1)[0];
      updatedPolls.push(submittedPoll);

      return updatedPolls;
    });

    setIsSubmittedMap((prevIsSubmittedMap) => ({
      ...prevIsSubmittedMap,
      [pollId]: true,
    }));
    // You can perform additional actions on submission if needed
  };

  const getOptionStyle = (pollId, option) => {
    if (isSubmittedMap[pollId]) {
      if (selectedOptions[pollId] === option.name) {
        return { color: 'teal' };
      } else {
        return { color: 'grey' };
      }
    } else {
      return { color: 'black' };
    }
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
    <Container>
      <Slider {...settings}>
        {polls.map((poll) => (
          <Grid item xs={12} sm={6} key={poll.id}>
            <Card
              sx={{
                height: '100%',
                width: '90%',
                display: 'flex',
                flexDirection: 'column',
                borderRadius: '8px',
                border: '1.5px solid grey',
                boxShadow: '0 1px 4px rgba(0,0,0,0.4)',
                margin: '0px',
                overflow: 'hidden',
              }}
            >
              <CardContent>
                <Typography variant="h6" sx={{ marginBottom: '10px', fontWeight: 'bold' }}>
                  {poll.pollName}
                </Typography>

                {isSubmittedMap[poll.pollId] ? (
                  <Card
                    key={poll.id}
                    sx={{
                      height: '100%',
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
                        <div key={option.name} sx={{ display: 'flex', flexDirection: 'column'}} style={{...getOptionStyle(poll.pollId, option) }}>
                          <Typography>{option.name}</Typography>
                          <LinearProgress
                            variant="determinate"
                            value={option.percentage}
                            sx={{ marginTop: '5px', flexGrow: 1 }}
                          />
                          <Typography variant="body2" sx={{ textAlign: 'center', marginTop: '5px'}}>
                            {option.percentage.toFixed(2)}% Votes
                          </Typography>
                        </div>
                      ))}
                    </CardContent>
                  </Card>
                ) : (
                  <Card
                  key={poll.id}
                  sx={{
                    height: '100%',
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
                      <div key={option.name} sx={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                        <Radio
                          sx={{ padding: 0 }}
                          checked={selectedOptions[poll.pollId] === option.name}
                          onChange={() => handleChooseOption(poll.pollId, option)}
                        />
                        <div sx={{ display: 'flex', flexDirection: 'column', marginLeft: '5px', flexGrow: 1 }}>
                          <Typography>{option.name}</Typography>
                          <div sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div sx={{ flexGrow: 1 }}>
                              <LinearProgress
                                variant="determinate"
                                value={option.percentage}
                                sx={{ marginTop: '5px', flexGrow: 1 }}
                              />
                            </div>
                            <Typography variant="body2" sx={{ textAlign: 'right', marginTop: '5px', marginLeft: '10px' }}>
                              {option.percentage.toFixed(2)}% Votes
                            </Typography>
                          </div>
                        </div>
                      </div>
                    ))}
                  </CardContent>
                </Card>
              )}

              <CardContent sx={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '-20px' }}>
                {!isSubmittedMap[poll.pollId] && (
                  <Button variant="contained" color="primary" name="Submit" label = "Submit"  onClick={() => handleSubmit(poll.pollId)}>
                    Submit
                  </Button>
                )}
              </CardContent>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Slider>
  </Container>
);
};

export default UserPolls;