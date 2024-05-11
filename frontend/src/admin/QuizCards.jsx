import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "react-responsive-carousel/lib/styles/carousel.min.css";
import {
  Card,
  CardContent,
  CardMedia,
  Typography,
  Box,
  Grid,
  Container,
  Button,
} from "@mui/material";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import PersonIcon from "@mui/icons-material/Person";
import '../admin/Create/Poll.css' 
import './NoContentdash.css'


function calculateTimeDifference(start, end) {
  const now = new Date().getTime();
  const startTime = new Date(start).getTime();
  const endTime = new Date(end).getTime();

  if (now < startTime) {
    // Quiz hasn't started yet
    return Math.floor((startTime - now) / 1000);
  } else if (now < endTime && (now > startTime || now === startTime)) {
    // Quiz is ongoing
    return Math.floor((endTime - now) / 1000);
  } else {
    return 0;
  }
}

const QuizCard = ({ quiz }) => {
  const [timeRemaining, setTimeRemaining] = useState(
    calculateTimeDifference(quiz.startTime, quiz.endTime)
  );

  const [isQuizStarted, setIsQuizStarted] = useState(false);
  const [isQuizEnded, setIsQuizEnded] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const timerInterval = setInterval(() => {
      const timeDiff = calculateTimeDifference(quiz.startTime, quiz.endTime);
      setTimeRemaining(timeDiff);
      const now = new Date().getTime();
      const start = new Date(quiz.startTime).getTime();
      const end = new Date(quiz.endTime).getTime();
      if (now < start) {
        setIsQuizStarted(false);
        setIsQuizEnded(false);
      } else if (now > start && now < end) {
        setIsQuizStarted(true);
        setIsQuizEnded(false);
      } else if (now > start && now > end) {
        setIsQuizStarted(true);
        setIsQuizEnded(true);
      }
    }, 100);

    return () => clearInterval(timerInterval);
  }, [quiz.startTime, quiz.endTime]);


  const formatTimer = (timeInSeconds) => {
    const days = Math.floor(timeInSeconds / (24 * 60 * 60));
    const hours = Math.floor((timeInSeconds % (24 * 60 * 60)) / (60 * 60));
    const minutes = Math.floor((timeInSeconds % (60 * 60)) / 60);
    const seconds = Math.floor(timeInSeconds % 60);

    const formattedDays = days.toString().padStart(2, "0");
    const formattedHours = hours.toString().padStart(2, "0");
    const formattedMinutes = minutes.toString().padStart(2, "0");
    const formattedSeconds = seconds.toString().padStart(2, "0");
    // console.log(${formattedDays}:${formattedHours}:${formattedMinutes}:${formattedSeconds})
    return `${formattedDays}:${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
  };
  const handleScheduleClick = () => {
    navigate(`/admin/schedule/${quiz.schedulerId}`, { state: quiz });
  };

  return (
    <Grid item xs={12} sm={6} mr={3}>
      <Card
        onClick={handleScheduleClick}
        style={{
          height: "320px",
          width: "180px",
          border: "1.5px solid grey",
          borderRadius: "8px",
          boxShadow: "0 1px 4px rgba(0, 0, 0, 0.4)",
          cursor: "pointer",
          overflow: "hidden",
          // transition: "transform 0.3s",
        }}
        // sx={{
        //   "&:": {
        //     // transform: "scale(1.05)",
        //     // transformOrigin: "center bottom",
        //     zIndex: 1,
        //   },
        // }}
      >
        <CardMedia
          component="img"
          style={{
            height: "50%",
            width: "100%",
            objectFit: "fill",
            borderBottom: "0.2px solid grey",
          }}
          src={
            quiz.quizType === "POLL"
              ? "https://static.wixstatic.com/media/829a0c_03006131375b450481ce4d3a9741b6a0~mv2.png"
              : "https://t3.ftcdn.net/jpg/04/07/11/98/360_F_407119847_nncJDJSF4tF4nD9s10RJvw5IV27ADlmr.jpg"
          }
        />

        <CardContent style={{ marginTop: "-16px" }}>
          <Box textAlign="center">
            <Typography
              variant="overline"
              style={{
                fontSize: "14px",
                fontWeight: "bold",
                textAlign: "center",
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
                transition: "all 0.3s ease",
              }}
              title={quiz.quizName}
              className="-effect"
            >
              {quiz.quizName.length > 10
                ? `${quiz.quizName.slice(0, 8)}...`
                : quiz.quizName}
            </Typography>
            <Box
              display="flex"
              alignItems="center"
              justifyContent="center"
              bgcolor="grey"
              p={0.5}
              borderRadius="4px"
              width="40px"
              height="30px"
              margin="auto"
              sx={{ display: "flex", alignItems: "center" }}
            >
              <PersonIcon fontSize="small" style={{ marginRight: "2px" }} />
              <Typography variant="caption" marginTop="3px">
                {quiz.memberCount}
              </Typography>
            </Box>
            <Box
              display="flex"
              alignItems="center"
              justifyContent="center"
              mt={1}
            >
              <AccessTimeIcon
                fontSize="small"
                color="black"
                style={{ marginRight: "2px" }}
              />

              <Typography
                variant="caption"
                color="black"
                style={{ fontWeight: "bold", fontSize: "16px" }}
              >
                {timeRemaining > 0 && !isQuizStarted
                  ? formatTimer(timeRemaining)
                  : timeRemaining > 0 && isQuizStarted && !isQuizEnded
                  ? formatTimer(timeRemaining)
                  : null}
              </Typography>

              <Typography
                variant="caption"
                color="black"
                style={{ fontWeight: "bold", fontSize: "16px" }}
              >
                {/* {timeRemaining > 0 && isQuizStarted && isQuizEnded && "TimesUp"} */}
                {isQuizEnded && "TimesUp"}
              </Typography>
            </Box>
            {timeRemaining > 0 && !isQuizStarted && (
              <Button
                variant="outlined"
                color="primary"
                size="small"
                sx={{ mt: "3px" }}
                // disabled
              >
                Scheduled
              </Button>
            )}
            {isQuizStarted && !isQuizEnded && (
              <Button
                variant="contained"
                style={{backgroundColor: "rgba(46,182,174,1)"}}
              >
                Live
              </Button>
            )}
            {isQuizEnded && (
              <Button variant="contained" color="inherit">
                Ended
              </Button>
            )}
          </Box>
        </CardContent>
      </Card>
    </Grid>
  );
};

const QuizCards = ({ data, type }) => {
  console.log("data: ", data);

  // useAxiosInterceptors();

  // const [schedules, setSchedules] = useState([]);
  // const [quizzes,setQuizzes]=useState([]);
  // const [polls,setPolls]=useState([]);
  // const [loading, setLoading] = useState(false)

  // const fetchAllSchedules = () => {
  //   // setLoading(true)
  //   axios
  //     .get(baseURL+`/schedule/all`)
  //     .then((res) => {
  //       console.log(res.data);
  //       if (res.status === 200) {
  //         const parsedSchedules = res.data.map((quiz) => {
  //           return (quiz.quizType === 'TEST' || quiz.quizType === 'SURVEY') ? {
  //             ...quiz,
  //             startTime: new Date(quiz.startTime).toISOString(),
  //             endTime: new Date(quiz.endTime).toISOString(),
  //           } : {
  //             ...quiz,
  //             startTime: new Date(quiz.startTime).toISOString(),
  //             endTime: new Date(quiz.endTime).toISOString(),
  //           };
  //         });

  //         // Separate quizzes and polls based on quizType
  //         const quizs = parsedSchedules.filter((quiz) => (quiz.quizType === 'TEST'|| quiz.quizType === 'SURVEY'));
  //         const polls = parsedSchedules.filter((quiz) => quiz.quizType === 'POLL');

  //         setQuizzes(quizs);

  //         setPolls(polls);
  //       } else {
  //         throw new Error(res.status);
  //       }
  //     })
  //     .catch((e) => console.log(e))
  //     // .finally(() => setLoading(false))
  // };

  // useEffect(() => {
  //   // setLoading(true)
  //   setTimeout(() => fetchAllSchedules(), 1000)
  //   // setLoading(false)
  // }, []);

  var settings = {
    dots: true,
    infinite: false,
    speed: 500,
    slidesToShow: 4,
    slidesToScroll: 2, // Change slidesToScroll to 1
    initialSlide: 0,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 3,
          slidesToScroll: 1, // Change slidesToScroll to 1
          infinite: true,
          dots: true,
        },
      },
      {
        breakpoint: 600,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 1, // Change slidesToScroll to 1
          initialSlide: 2,
        },
      },
      {
        breakpoint: 480,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1, // Change slidesToScroll to 1
        },
      },
    ],
  };

  return (
    <Container
      style={{
        margin: "0px 0px 0px 150px",
        maxWidth: "1000px",
        // width: "75%",
      }}
    >
      {data.length === 0 ? (
        <div className="no-quizzes-container"  style={{
          margin: "0px 0px 0px -35px",
          maxWidth: "1000px",
          // width: "75%",
        }}>
          {/* <h1>No Quizzes Available</h1> */}
          <p>
            Oops! It looks like there are no Scheduled quizzes available at the
            moment.
          </p>
          <p>Create New Scheduled Quizzes/Polls!!!</p>
          {/* <Link to={`/admin/schedule/quiz`}>
            <Button
              sx={{
                backgroundColor: "(46,182,174,1)",
                color: "#fff",
                height: "40px",
                fontSize: "14px",
                cursor: "pointer",
                boxShadow: "0 1px 4px (0, 0, 0, 0.4)",
                "&:": { backgroundColor: "(46,182,174,1)" },
              }}
            >
              Create
            </Button>
          </Link> */}
        </div>
      ) : (
        <Slider
          {...settings}
          style={{
            // width: "75%",
            margin: "0px 0px 0px -10px",
            perspective: "1000px",
            transformStyle: "preserve-3d",
            
          }}
        >
          
          {data
            .slice(0)
            .reverse()
            .map((quiz, index) => (
              <QuizCard key={index} quiz={quiz} /> 
            ))}
        </Slider>
      )}
      {/* <Typography variant="h4" style={{ fontWeight: 'bold', marginLeft: '30px', marginBottom: '20px',marginTop:'20px', fontSize: '26px' }}>Polls</Typography>
      <Slider {...settings}>
        {polls.map((quiz, index) => (
          <UserCard key={index} quiz={quiz} />
        ))}
      </Slider> */}
    </Container>
  );
};

export default QuizCards;