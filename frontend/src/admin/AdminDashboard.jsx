import React, { useEffect, useState } from "react";
import { Typography, Box } from "@mui/material";
import QuizCards from "./QuizCards";
import axios from "axios";
import baseURL from "../BaseUrl";
import { useAxiosInterceptors } from "../useAxiosInterceptors";

const AdminDashboard = () => {
  useAxiosInterceptors();

  const fetchAllSchedules = async() => {
    await axios
      .get(baseURL + "/schedule/allSchedules")
      .then((res) => {
        //console.log(res.data);
        if (res.status === 200) {
          setQuizSchedules(res.data.filter((quiz) => quiz.quizType !== "POLL"));
          setPollSchedules(res.data.filter((quiz) => quiz.quizType === "POLL"));
        } else throw new Error(res.status);
      })
      .catch((e) => {console.log(e)});
  };

  useEffect(() => {
    fetchAllSchedules();
  }, []);

  const [quizSchedules, setQuizSchedules] = useState([]);
  const [pollSchedules, setPollSchedules] = useState([]);

  return (
    <div>
      <Box>
        <Box
          component="main"
          sx={{ flexGrow: 1, p: 10, transition: "margin-left 0.3s" }}
          style={{ marginLeft: "-100px", marginTop: "10px" }}
        >
          <h4
            variant="h4"
            style={{
              fontWeight: "bold",
              marginLeft: "80px",
              marginBottom: "20px",
              fontSize: "26px",
              color: "rgb(30 41 59 1)",
            }}
          >
            Scheduled Quizzes
          </h4>
          <QuizCards data={quizSchedules} type='quiz'/>
        </Box>
        <Box
          component="main"
          sx={{ flexGrow: 1, p: 10, transition: "margin-left 0.3s" }}
          style={{ marginLeft: "-100px", marginTop: "10px" }}
        >
          <h4
            variant="h4"
            style={{
              fontWeight: "bold",
              marginLeft: "80px",
              marginBottom: "20px",
              marginTop: "-100px",
              fontSize: "26px",
              color: "rgb(30 41 59 1)",
            }}
          >
            Scheduled Polls
          </h4>
          <QuizCards data={pollSchedules} type='poll'/>
        </Box>
      </Box>
    </div>
  );
};

export default AdminDashboard;
