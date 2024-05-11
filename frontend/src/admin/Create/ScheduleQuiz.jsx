import React, { useEffect, useState } from "react";
import {
  Typography,
  MenuItem,
  Box,
  Button,
  Modal,
  TextField,
  Autocomplete,
} from "@mui/material";
import { Card, CardContent, CardMedia, Grid } from "@mui/material";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { LocalizationProvider, DateTimePicker } from "@mui/x-date-pickers";
import { enGB } from "date-fns/locale";
import CheckIcon from "@mui/icons-material/Check";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import CancelRoundedIcon from "@mui/icons-material/CancelRounded";
import baseURL from "../../BaseUrl";
import { useAxiosInterceptors } from "../../useAxiosInterceptors";
import './Poll.css';
import { toast } from "react-toastify";

const ScheduleQuiz = () => {
  useAxiosInterceptors();

  useEffect(() => {
    fetchAllQuizzes();
  }, []);

  const fetchAllQuizzes = async() => {
    await axios
      .get(baseURL + "/quiz/allTypes", {
        maxRedirects: 0, // Set maximum redirects to 0 to manually handle the 302 status
      })
      .then((response) => {
        // Handle the response
        console.log("Response:", response);
      })
      .catch((error) => {
        if (error.response) {
          // The request was made and the server responded with a status code
          // other than 2xx, handle the error based on status code
          console.log("Status:", error.response.status);
          console.log("Headers:", error.response.headers);
          console.log("Data:", error.response.data);
          const data = error.response.data
          setDummyData(data instanceof Array ? data.filter(quiz => quiz.quizType !== 'POLL') : []);
        } else if (error.request) {
          // The request was made but no response was received
          console.log("Request:", error.request);
        } else {
          // Something happened in setting up the request that triggered an error
          console.log("Error:", error.message);
        }
      });
  };

  const initSchedule = {
    startTime: new Date(),
    endTime: new Date(),
    quizId: "",
    personIdList: [],
  };

  const [dummyData, setDummyData] = useState([]);
  const [names, setNames] = useState([]);
  const [schedule, setSchedule] = useState(initSchedule);

  const [openScheduleModal, setOpenScheduleModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const scheduleModalStyle = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 500,
    bgcolor: "background.paper",
    borderRadius: "10px",
    // border: '2px solid #000',
    // boxShadow: 24,
    p: 4,
  };

  const handleOpenSchedule = async (id) => {
    await axios
      .get(baseURL + "/user/responders")
      .then((res) => {
        console.log(res);
        if (res.status === 200) {
          const data = res.data;
          console.log(data);
          setSchedule({
            ...schedule,
            quizId: id,
          });

          setNames(
            data.map((person) => {
              const obj = {
                id: person.userId,
                name: `${person.firstName} ${person.lastName}`,
              };
              return obj;
            })
          );
        } else throw new Error(res.status);
      })
      .catch((e) => console.log(e));

    setOpenScheduleModal(!openScheduleModal);
  };

  const handleStartTimeChange = (value) => {
    // console.log(e)
    setSchedule({
      ...schedule,
      startTime: value,
    });
  };

  const handleEndTimeChange = (value) => {
    // console.log(e)
    setSchedule({
      ...schedule,
      endTime: value,
    });
  };

  const handlePersonIdChange = (e, value) => {
    console.log(value.map((obj) => obj.id));
    setSchedule({
      ...schedule,
      personIdList: value.map((obj) => obj.id),
    });
  };

  const handleScheduleSubmit = async() => {
    console.log(schedule);
    setLoading(true);

    const start = new Date(schedule.startTime).getTime()
    const end = new Date(schedule.endTime).getTime()
    const now = new Date().getTime()
    
    if(start < now) {
      toast.warning('Can\'t schedule for past dates or timings')
      setLoading(false)
      return
    }

    if(start >= end) {
      toast.warning('Start time should be less than End time!')
      setLoading(false)
      return
    }

    if(schedule.personIdList.length === 0) {
      toast.warning('Responders cannot be empty!')
      setLoading(false)
      return
    }

    await axios
      .post(
        baseURL + "/schedule/create",
        {
          startTime: formatToBackendDate(schedule.startTime),
          endTime: formatToBackendDate(schedule.endTime),
          quizId: schedule.quizId,
          personIdList: schedule.personIdList,
        },
        {
          headers: { "Content-Type": "application/json" },
        }
      )
      .then((res) => {
        if (res.status === 200) {
          toast.success('Quiz Scheduled!')
          setOpenScheduleModal(false);
          setSchedule(initSchedule);
        } else throw new Error(res.status);
      })
      .catch((e) => {
        console.log(e)
        toast.warning(e.response.data.message)
      });

    setLoading(false);
  };

  function formatToBackendDate(dateString) {
    const date = new Date(dateString);

    // Format the date and time components
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, "0"); // Month is 0-indexed
    const day = date.getDate().toString().padStart(2, "0");
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");
    const seconds = date.getSeconds().toString().padStart(2, "0");
    const milliseconds = date.getMilliseconds().toString().padStart(6, "0");

    // Combine the components into the desired format
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
  }

  return (
    <div>
      <Box>
        <Box
          component="main"
          sx={{ flexGrow: 1, p: 10, transition: "margin-left 0.3s" }}
          style={{ marginLeft: "-20px", marginTop: "10px" }}
        >
          <div style={{ display: "flex", justifyContent: "space-between" }}>
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
            <Button
              variant="contained"
              size="small"
              onClick={() => navigate("/admin/create-quiz")}
              sx={{ marginRight:'-30px', backgroundColor: "rgba(46,182,174,1)","&:hover":{backgroundColor: "rgba(46,182,174,1)"} }}
            >
              Create Quiz
            </Button>
          </div>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "auto auto auto auto auto",
              rowGap: "5rem",
              marginTop: "1rem",
             
            }}
          >
            {dummyData.length > 0 ? (
              dummyData.map((quiz, index) => (
                <Grid key={index} item xs={12} sm={6} mr={3}>
                  <Card
                    style={{
                      height: "220px",
                      width: "190px",
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
                      src="https://t3.ftcdn.net/jpg/04/07/11/98/360_F_407119847_nncJDJSF4tF4nD9s10RJvw5IV27ADlmr.jpg"
                    />
                     <CardContent
                      style={{ marginTop: "-15px", marginBottom: "15px",textAlign:'center' }}
                    >
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
                          title= {quiz.quizName}
                          className="hover-effect"
                        >
                        {quiz.quizName.length>10 ? `${quiz.quizName.slice(0,8)}...` : quiz.quizName}
                        </Typography>
                        <Box
                          display="flex"
                          alignItems="center"
                          justifyContent="center"
                          p={0.5}
                          borderRadius="4px"
                        >
                          {/* <PersonIcon fontSize="small" style={{ marginRight: '2px' }} /> */}
                          {/* <Typography
                            variant="body2"
                            marginTop="-10px"
                            marginBottom="4px"
                          >
                            questions: {quiz.questionCount}
                          </Typography> */}
                        </Box>
                        <div
                          style={{ display: "flex", justifyContent: "center" }}
                        >
                          {/* <button
                            type="button"
                            style={{
                              padding: "6px",
                              margin: "0 10px 0 20px",
                              cursor: "pointer",
                              backgroundColor: "GrayText",
                              borderRadius: 4,
                              border: "none",
                            }}
                          >
                            Edit
                          </button> */}
                          <button
                            variant="contained"
                            type="button"
                            style={{
                              padding: "6px",
                              cursor: "pointer",
                              backgroundColor: "rgba(46,182,174,1)",
                              borderRadius: 4,
                              border: "none",
                            }}
                            onClick={() => handleOpenSchedule(quiz.quizId)}
                          >
                            Schedule
                          </button>
                          <Modal
                            open={openScheduleModal}
                            onClose={() =>
                              setOpenScheduleModal(!openScheduleModal)
                            }
                            sx={{
                              "& .MuiModal-backdrop": {
                                backgroundColor: "rgba(0, 0, 0, 0.1);",
                              },
                            }}
                          >
                            <Box sx={scheduleModalStyle}>
                              <div
                                style={{
                                  display: "flex",
                                  justifyContent: "space-between",
                                }}
                              >
                                <Typography variant="h5" component="h2">
                                  Schedule
                                </Typography>
                                <CancelRoundedIcon
                                  style={{ cursor: "pointer" }}
                                  onClick={() =>
                                    setOpenScheduleModal(!openScheduleModal)
                                  }
                                />
                              </div>
                              <LocalizationProvider
                                dateAdapter={AdapterDateFns}
                                locale={enGB}
                              >
                                <DateTimePicker
                                  renderInput={(props) => (
                                    <TextField {...props} />
                                  )}
                                  label="Start Time"
                                  name="startTime"
                                  value={schedule.startTime}
                                  onChange={handleStartTimeChange}
                                  ampm={false} // Use 24-hour format
                                  sx={{
                                    margin: "15px 0 5px 0",
                                  }}
                                />
                              </LocalizationProvider>

                              <LocalizationProvider
                                dateAdapter={AdapterDateFns}
                                locale={enGB}
                              >
                                <DateTimePicker
                                  renderInput={(props) => (
                                    <TextField {...props} />
                                  )}
                                  label="End Time"
                                  name="endTime"
                                  value={schedule.endTime}
                                  onChange={handleEndTimeChange}
                                  ampm={false} // Use 24-hour format
                                  sx={{
                                    margin: "15px 0 15px 0",
                                  }}
                                />
                              </LocalizationProvider>

                              <Autocomplete
                                multiple
                                options={names}
                                onChange={handlePersonIdChange}
                                getOptionLabel={(option) => option.name}
                                disableCloseOnSelect
                                renderInput={(params) => (
                                  <TextField
                                    {...params}
                                    variant="outlined"
                                    label="Responders"
                                    placeholder="Select Responders"
                                  />
                                )}
                                renderOption={(props, option, { selected }) => (
                                  <MenuItem
                                    {...props}
                                    key={option.id}
                                    value={option}
                                    sx={{ justifyContent: "space-between" }}
                                  >
                                    {option.name}
                                    {selected ? (
                                      <CheckIcon color="info" />
                                    ) : null}
                                  </MenuItem>
                                )}
                              />
                              <div
                                style={{
                                  display: "flex",
                                  justifyContent: "center",
                                  marginTop: "12px",
                                }}
                              >
                                <Button
                                  variant="contained"
                                  onClick={handleScheduleSubmit}
                                  disabled={loading}
                                  sx={{backgroundColor:"rgba(46,182,174,1)"}}
                                >
                                  Submit
                                </Button>
                              </div>
                            </Box>
                          </Modal>
                        </div>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))
            ) : (
              <div
                className="no-quizzes-container"
                style={{ marginLeft: "100px" }}
              >
                {/* <h1>No Quizzes Available</h1> */}
                <p>
                  Oops! It looks like there are no Scheduled quizzes available
                  at the moment.
                </p>
                <p>Create New Scheduled Quizzes!!!</p>
              </div>
            )}
          </div>
        </Box>
      </Box>
    </div>
  );
};

export default ScheduleQuiz;