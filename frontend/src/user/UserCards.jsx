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
import axios from "axios";
import baseURL from "../BaseUrl";
import { useAxiosInterceptors } from "../useAxiosInterceptors";
import { useSelector } from "react-redux";

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

const UserCard = ({ quiz }) => {
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

  const handleStartQuiz = () => {
    // console.log(quiz.schedulerId)
    navigate(`/quiz_display/${quiz.schedulerId}`, { state: quiz });
  };

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

  return (
    <Grid item xs={12} sm={6} mr={3}>
      <Card
        style={{
          height: "250px",
          width: "180px",
          border: "1.5px solid grey",
          borderRadius: "8px",
          boxShadow: "0 1px 4px rgba(0, 0, 0, 0.4)",
        }}
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
                fontSize: "15px",
                fontWeight: "bold",
                textAlign: "center",
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
                transition: "all 0.3s ease",
              }}
              title={quiz.quizName}
              className="hover-effect"
            >
              {quiz.quizName.length > 10
                ? `${quiz.quizName.slice(0, 8)}...`
                : quiz.quizName}
            </Typography>
            <Box
              display="flex"
              alignItems="center"
              justifyContent="center"
              mt={1}
              style={{ marginTop: "-6px" }}
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
                Assigned
              </Button>
            )}
            {isQuizStarted && !isQuizEnded && (
              <Button
                variant="contained"
                onClick={handleStartQuiz}
                style={{ backgroundColor: "rgba(46,182,174,1)" }}
              >
                Start
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

const UserCards = () => {
  useAxiosInterceptors();

  const [quizzes, setQuizzes] = useState([]);
  const [polls, setPolls] = useState([]);
  const [id, setId] = useState("abc");
  const userId = useSelector(state => state.user.id);

  const fetchAllSchedules = () => {
    axios
      .get(baseURL + `/schedule/all?userId=${userId}`)
      .then((res) => {
        console.log(res.data);
        if (res.status === 200) {
          const parsedSchedules = res.data.map((quiz) => {
            return quiz.quizType === "TEST" || quiz.quizType === "SURVEY"
              ? {
                  ...quiz,
                  startTime: new Date(quiz.startTime).toISOString(),
                  endTime: new Date(quiz.endTime).toISOString(),
                }
              : {
                  ...quiz,
                  startTime: new Date(quiz.startTime).toISOString(),
                  endTime: new Date(quiz.endTime).toISOString(),
                };
          });

          // Separate quizzes and polls based on quizType
          const quizs = parsedSchedules.filter(
            (quiz) => quiz.quizType !== "POLL"
          ).filter(ele => {
            const end  = new Date(ele.endTime).getTime()
            const now = new Date().getTime()
            return end > now
          })
          const polls = parsedSchedules.filter(
            (quiz) => quiz.quizType === "POLL"
          ).filter(ele => {
            const end  = new Date(ele.endTime).getTime()
            const now = new Date().getTime()
            return end > now
          })

          setQuizzes(quizs);

          setPolls(polls);
        } else {
          throw new Error(res.status);
        }
      })
      .catch((e) => console.log(e));
  };

  useEffect(() => {
    setId(sessionStorage.getItem("id"));
    fetchAllSchedules();
  }, [id]);

  var settings = {
    dots: true,
    infinite: false,
    speed: 500,
    slidesToShow: 4,
    slidesToScroll: 2,
    initialSlide: 0,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 3,
          slidesToScroll: 1,
          infinite: true,
          dots: true,
        },
      },
      {
        breakpoint: 600,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 1,
          initialSlide: 2,
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
        margin: "0px -150px 0px -30px",
        maxWidth: "1240px",
        // width: "75%",
      }}
    >
      <Typography
        variant="h4"
        style={{
          fontWeight: "bold",
          marginLeft: "0px",
          marginBottom: "20px",
          fontSize: "26px",
        }}
      >
        Quizzes
      </Typography>
      {quizzes.length === 0 ? (
        <div className="no-quizzes-container" >
          {/* <h1>No Quizzes Available</h1> */}
          <p>
            Oops! It looks like there are no quizzes available at the moment.
          </p>
          <p>Check back later!</p>
        </div>
      ) : (
        <div style={{marginLeft:'90px'}}>
          {" "}
          <Slider {...settings}>
            {quizzes
              .slice(0)
              .reverse()
              .map((quiz, index) => (
                <UserCard key={index} quiz={quiz} />
              ))}
          </Slider>
        </div>
      )}

      <Typography
        variant="h4"
        style={{
          fontWeight: "bold",
          marginLeft: "0px",
          marginBottom: "20px",
          marginTop: "20px",
          fontSize: "26px",
        }}
      >
        Polls
      </Typography>
      {polls.length === 0 ? (
        <div className="no-quizzes-container" >
          {/* <h1>No Quizzes Available</h1> */}
          <p>Oops! It looks like there are no polls available at the moment.</p>
          <p>Check back later!</p>
        </div>
      ) : (
        <div style={{marginLeft:'90px'}}>
          {" "}
          <Slider {...settings}>
            {polls
              .slice(0)
              .reverse()
              .map((quiz, index) => (
                <UserCard key={index} quiz={quiz} />
              ))}
          </Slider>
        </div>
      )}
    </Container>
  );
};

export default UserCards;