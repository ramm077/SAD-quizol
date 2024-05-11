import React, { useEffect, useState } from "react";
import {
  Container,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Box,
  IconButton,
  Button,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import baseURL from "../BaseUrl";
import DeleteModal from "../components/common/DeleteModal";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { enGB } from "date-fns/locale";
import { toast } from "react-toastify";
import { useAxiosInterceptors } from "../useAxiosInterceptors";
import VisibilityIcon from "@mui/icons-material/Visibility";
import PollCard from "../user/PollCard";


function IndividualSchedule() {

  useAxiosInterceptors();

  const { state } = useLocation();
  // const []
  const { scheduleId } = useParams();
  const [users, setUsers] = useState([]);
  const [editUsers, setEditUsers] = useState([])
  const [questions, setQuestions] = useState([]);
  const [openSection, setOpenSection] = useState(null);
  const [isQuizStarted, setIsQuizStarted] = useState(false)
  const [isQuizEnded, setIsQuizEnded] = useState(false)
  const [inEditMode, setInEditMode] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [responseData, setResponseData] = useState({})

  

  const fetchAllUsers = () => {
    axios.get(baseURL + `/schedule/allUsers?schedulerId=${scheduleId}`)
      .then((res) => {
        console.log(res.data);
        if (res.status === 200) setUsers(res.data);
        else throw new Error(res.status);
      })
      .catch((e) => console.log(e));
  };

  const fetchAllQuestions = () => {
    axios
      .get(baseURL + `/quiz?quizId=${state.quizId}`)
      .then((res) => {
        console.log(res.data);
        // if (res.status === 200) setQuestions(res.data);
        // else throw new Error(res.status);
      })
      .catch((e) => {
        console.log(e)
        if (e.response.status === 302) {
          const data = e.response.data
          setQuestions(e.response.data)
        }
      });
  };

  const viewPoll = () => {
    axios.get(baseURL+`/response/all?schedulerId=${scheduleId}`)
    .then(res => {
      console.log(res)
    })
    .catch(e => {
      console.log(e)
      if(e.response.status === 302) setPollData(e.response.data)
    })
  }


  useEffect(() => {
    fetchAllUsers();
    fetchAllQuestions();
    viewPoll();
    // let sbool, ebool;

    const timerInterval = setInterval(() => {
      const now = new Date().getTime();
      const start = new Date(state.startTime).getTime();
      const end = new Date(state.endTime).getTime();
      // sbool = now >= start
      // ebool = now >= end
      setIsQuizStarted(now >= (start - 2700000))
      setIsQuizEnded(now >= end)
    }, 100);

    // if(sbool && !ebool) toast.info('Quiz is Ongoing!')
    // if(ebool) toast.info('Quiz has ended!')

    return () => clearInterval(timerInterval);
  }, []);

  const toggleSection = (sectionId) => {
    setOpenSection((prevOpenSection) =>
      prevOpenSection === sectionId ? null : sectionId
    );
  };

  function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // getMonth() returns a zero-based value
    const day = date.getDate();
    const hrs = date.getHours();
    const mins = date.getMinutes();
    return `${year}-${month < 10 ? "0" + month : month}-${day < 10 ? "0" + day : day
      } ${hrs < 10 ? "0" + hrs : hrs}:${mins < 10 ? "0" + mins : mins}`;
  }

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

  const initUpdate = {
    startTime: new Date(state.startTime),
    endTime: new Date(state.endTime),
    personIdList: []
  }

  const initPoll = {
    id: 1,
    questionId: "poll_1",
    questionText: "Best Creatures on Earth",
    optionList: [
      { optionText: 'Polar Bear', responseCount: 0 },
      { optionText: 'American black bear', responseCount: 2 },
      { optionText: 'giant panda', responseCount: 1 },
      { optionText: 'sloth bear', responseCount: 5 },
    ],
    totalResponses: 8
  }

  const [updateSchedule, setUpdateSchedule] = useState(initUpdate);
  const [open, setOpen] = useState(false)
  const [openResSection, setOpenResSection] = useState(false)
  const [pollData, setPollData] = useState(initPoll)
  const navigate = useNavigate();

  const handleCloseModal = () => setIsModalOpen(!isModalOpen)

  const handleInEditModeChange = () => {
    console.log(updateSchedule)
    if(!inEditMode) {

      setUpdateSchedule({
        ...updateSchedule,
        personIdList: users.map(u => u.personId)
      })

      axios.get(baseURL+'/user/responders')
      .then(res => {
        console.log(res.data)
        if(res.status === 200) setEditUsers(res.data)
        else throw new Error(res.status)
      })
      .catch(e => console.log(e))
    }
    setInEditMode(!inEditMode)
  }


  const handleStartTimeChange = (val) => {
    setUpdateSchedule({
      ...updateSchedule,
      startTime: val
    })
  }

  const handleEndTimeChange = (val) => {
    setUpdateSchedule({
      ...updateSchedule,
      endTime: val
    })
  }

  const handleEditUserListChange = (id) => {
    console.log(id)

    let list = updateSchedule.personIdList
    if(list.includes(id)) list = list.filter(x => x !== id)
    else list.push(id)

    setUpdateSchedule({
      ...updateSchedule,
      personIdList: list
    })
  }


  const handleEditSubmit = (reason) => {
    const userList = users.map(u => u.personId)
    const reqBody = {
      startTime: formatToBackendDate(updateSchedule.startTime),
      endTime: formatToBackendDate(updateSchedule.endTime),
      addPersonIdList: updateSchedule.personIdList.filter(x => !userList.includes(x)),
      deletePersonIdList: userList.filter(x => !updateSchedule.personIdList.includes(x)),
      reason: reason
    }

    axios.put(baseURL+`/schedule/update?schedulerId=${scheduleId}`, JSON.stringify(reqBody), {
      headers: {'Content-Type': 'application/json'}
    })
    .then(res => {
      console.log(res)
      toast.success('Updated')
      navigate('/admin/dashboard')
    })
    .catch(e => console.log(e))
  }

  const handleDeleteSubmit = (reason) => {
    // setLoading(true)
    
    // setTimeout(() => {
    //   console.log(reason)

    //   // navigate('/admin/dashboard')
    // }, 5000)
    //api
    axios.delete(baseURL+`/schedule/delete?schedulerId=${scheduleId}`, {
      data: {reason: reason},
      headers: {'Content-Type': 'application/json'}
    })
    .then(res => {
      console.log(res)
      toast.success('Deleted')
      navigate('/admin/dashboard')
    })
    .catch(e => console.log(e))

    // setLoading(false)
  }


  const handleViewClick = async (userId) => {

    try {
      let response = await axios.get(
        baseURL +
          `/response/display?userId=${userId}&schedulerId=${scheduleId}`
      );
      if (response.status === 200) {
        const data = await response.data;
        setResponseData(data);
      } else if (response.status === 204) {
        setResponseData({});
      } else {
        throw new Error(response.data);
      }
    } catch (error) {
      console.log(error);
      if (error.response.status === 302) setResponseData(error.response.data);
    }

    setOpen(true);
  };

  const handleClose = () => {
    setResponseData({});
    setOpen(false);
  };

  const toggleResSection = (sectionId) => {
    setOpenResSection((prevOpenSection) =>
      prevOpenSection === sectionId ? null : sectionId
    );
  };

  const customStyles = {
    // Apply your custom styles here
    root: {
      display: "block",
      margin: "auto",
      width: "70%",
    },
    title: {
      color: "#ffffff",
    },
    content: {
      padding: "20px",
    },
    actions: {
      justifyContent: "center",
    },
  };


  return (
    <Box>
      {/* <img src={loadingIcon} alt="loading icon" /> */}
      {/* role{sessionStorage.getItem('role')} */}
      <Container
        sx={{
          marginTop: "50px",
          // marginBottom: "50px ",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <div style={{ width: "100%" }}>
          {/* Quiz Metadata  */}
          <div
            style={{ padding: "16px 16px 5px 16px", marginTop: '20px' }}
            elevation={1}
          >
            <Typography variant="h5" style={{ fontWeight: "bold", }}>{state.quizName}</Typography>
            <div
              style={{
                display: "flex",
                justifyContent: 'space-between',
                marginTop: '16px'
              }}
            >
              <div style={{ display: "flex", flexDirection: "column" }}>
                <Typography
                  variant="body1"
                  style={{ fontSize: "0.75rem", lineHeight: "1rem" }}
                >
                  Number of members
                </Typography>
                <Typography
                  variant="body1"
                  style={{ fontSize: "1.125rem", lineHeight: "1.75rem" }}
                >
                  {state.memberCount}
                </Typography>
              </div>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <Typography
                  variant="body1"
                  style={{ fontSize: "0.75rem", lineHeight: "1rem" }}
                >
                  Number of Questions
                </Typography>
                <Typography
                  variant="body1"
                  style={{ fontSize: "1.125rem", lineHeight: "1.75rem" }}
                >
                  {state.questionCount}
                </Typography>
              </div>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <Typography
                  variant="body1"
                  style={{ fontSize: "0.75rem", lineHeight: "1rem" }}
                >
                  Start Time
                </Typography>
                {
                  inEditMode
                  ? <LocalizationProvider
                      dateAdapter={AdapterDateFns}
                      locale={enGB}
                    >
                      <DateTimePicker
                        renderInput={(props) => (
                          <TextField {...props} />
                        )}
                        name="startTime"
                        value={updateSchedule.startTime}
                        onChange={handleStartTimeChange}
                        ampm={false} // Use 24-hour format
                        sx={{
                          margin: "15px 0 5px 0",
                        }}
                      />
                    </LocalizationProvider>
                  : <Typography
                      variant="body1"
                      style={{ fontSize: "1.125rem", lineHeight: "1.75rem" }}
                    >
                      {formatDate(state.startTime)}
                    </Typography>
                }
              </div>

              <div style={{ display: "flex", flexDirection: "column" }}>
                <Typography
                  variant="body1"
                  style={{ fontSize: "0.75rem", lineHeight: "1rem" }}
                >
                  End Time
                </Typography>
                {
                  inEditMode
                  ? <LocalizationProvider
                      dateAdapter={AdapterDateFns}
                      locale={enGB}
                    >
                      <DateTimePicker
                        renderInput={(props) => (
                          <TextField {...props} />
                        )}
                        name="endTime"
                        value={updateSchedule.endTime}
                        onChange={handleEndTimeChange}
                        ampm={false} // Use 24-hour format
                        sx={{
                          margin: "15px 0 5px 0",
                        }}
                      />
                    </LocalizationProvider>
                  : <Typography
                      variant="body1"
                      style={{ fontSize: "1.125rem", lineHeight: "1.75rem" }}
                    >
                      {formatDate(state.endTime)}
                    </Typography>
                }
              </div>
            </div>
          </div>
          
          { questions.quizType === 'POLL' && isQuizEnded &&
            <div style={{display: 'flex', justifyContent: 'center', marginTop: '40px'}}>
              <div style={{width: '60%'}}>
                <PollCard poll={pollData} /> 
              </div>
            </div>
          }

          {/* User Table */}
          <div
            elevation={5}
            style={{ padding: "16px 16px 5px 16px", marginTop: '20px', marginBottom: '20px' }}
          >
            <Typography variant="h6" style={{ fontWeight: 'bold' }}>Responders List</Typography>
            <TableContainer style={{ marginTop: "10px", border: "0.2px solid grey" }}>
              <Table >
                <TableHead style={{ backgroundColor: "black" }} >
                  <TableRow>
                    {
                      inEditMode
                      ? <TableCell style={{ color: "white", fontWeight: "bold" }} />
                      : null
                    }
                    <TableCell style={{ color: "white", fontWeight: "bold" }}>
                      ID
                    </TableCell>
                    <TableCell style={{ color: "white", fontWeight: "bold" }}>
                      First Name
                    </TableCell>
                    <TableCell style={{ color: "white", fontWeight: "bold" }}>
                      Last Name
                    </TableCell>
                    <TableCell style={{ color: "white", fontWeight: "bold" }}>
                      Email
                    </TableCell>
                    {isQuizEnded && 
                      <TableCell style={{ color: "white", fontWeight: "bold" }}>
                        Action
                      </TableCell>
                    }
                  </TableRow>
                </TableHead>
                <TableBody >
                  {(inEditMode ? editUsers : users).map((user, index) => (
                    <TableRow key={index} onClick={() => handleEditUserListChange(user.userId)} > 
                      {
                        inEditMode
                        ? <><TableCell >
                              <input 
                                type="checkbox" 
                                checked={updateSchedule.personIdList.includes(user.userId)}
                                onChange={() => handleEditUserListChange(user.userId)}
                              />
                              {/* <Checkbox /> */}
                          </TableCell>
                          <TableCell>{index + 1}</TableCell>
                          <TableCell>{user.firstName}</TableCell>
                          <TableCell>{inEditMode ? user.lastName : user.lastname}</TableCell>
                          <TableCell>{user.emailId}</TableCell>
                        </>
                        : <>
                          <TableCell>{index + 1}</TableCell>
                          <TableCell>{user.firstName}</TableCell>
                          <TableCell>{inEditMode ? user.lastName : user.lastname}</TableCell>
                          <TableCell>{user.emailId}</TableCell>
                          {isQuizEnded &&
                            <TableCell>
                              <IconButton
                                color="primary"
                                onClick={() => handleViewClick(user.personId)}
                              >
                                <VisibilityIcon />
                              </IconButton>
                            </TableCell>
                          }
                        </>
                      }
                    </TableRow> 
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </div>

          <Dialog
            open={open}
            onClose={handleClose}
            key={responseData.quizId}
            PaperProps={{
              style: customStyles.root,
            }}
          >
            <DialogTitle variant="h4" >{responseData.quizName}</DialogTitle>
            <DialogContent>
              {Object.keys(responseData).length !== 0 ? (
                <div>
                  <Typography>Quiz Type: {responseData.quizType}</Typography>
                  {responseData.quizType === "TEST" && (
                    <Typography sx={{mb: 2}}>
                      Quiz Score: {responseData.quizScore}
                    </Typography>
                  )}
                  {responseData.questionList &&
                    responseData.questionList.map((row, index) => (
                      <Accordion
                        key={row.questionId}
                        expanded={openResSection === row.questionId}
                        onChange={() => toggleResSection(row.questionId)}
                      >
                        <AccordionSummary
                          expandIcon={<ExpandMoreIcon />}
                          aria-controls={`panel${row.questionId}a-content`}
                          id={`panel${row.questionId}a-header`}
                        >
                          <Typography variant="h6">
                            {row.questionText}
                          </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                          <div>
                            {row.questionType === 'TEXT' 
                            ? <div>
                                Response: {row.response.textResponse.answerText}
                              </div>
                            : row.optionList.map((opt, ind) => (
                                <div
                                  key={opt.optionId}
                                  style={{ display: "flex" }}
                                >
                                  <input
                                    type="checkbox"
                                    style={{ marginRight: "10px" }}
                                    disabled
                                    checked={row.response.optionResponses.chosenOptions.includes(
                                      opt.optionId
                                    )}
                                  />
                                  <Typography
                                    style={{
                                      color: 
                                      responseData.quizType === 'POLL'
                                      ? (row.response.optionResponses.chosenOptions.includes(opt.optionId) ? 'blue' : 'black')
                                      : (row.response.optionResponses.chosenOptions.includes(opt.optionId)
                                        ? opt.isTrue
                                          ? "green"
                                          : "red"
                                        : opt.isTrue
                                          ? "green"
                                          : "black")
                                    }}
                                  >
                                    {opt.optionText}
                                  </Typography>
                                </div>
                              ))
                            }
                          </div>
                        </AccordionDetails>
                      </Accordion>
                    ))}
                </div>
              ) : (
                <Typography>No Response</Typography>
              )}
            </DialogContent>
            <DialogActions>
              <Button
                variant="contained"
                size="small"
                sx={{ backgroundColor: "rgba(46,182,174,1)" }}
                onClick={handleClose}
              >
                Cancel
              </Button>
            </DialogActions>
          </Dialog>

          
          { questions.quizType !== 'POLL' &&
            <div style={{ padding: "16px 16px 5px 16px", marginTop: '20px', marginBottom: '20px' }}>
              <Typography variant="h6" style={{ marginBottom: 8, fontWeight: 'bold' }}>
                Questions
              </Typography>
              {questions.questionList &&
                questions.questionList.map((row, index) => (
                  <Accordion
                    key={index}
                    style={{ border: "0.2px solid grey" }}
                    expanded={openSection === row.questionId}
                    onChange={() => toggleSection(row.questionId)}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls={`panel${row.questionId}a-content`}
                      id={`panel${row.questionId}a-header`}
                    >
                      <Typography variant="subtitle1" style={{ fontWeight: 'bold' }}>{row.questionText}</Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                      <div>
                        {row.optionList.map((opt, ind) => (
                          <div key={opt.optionId} style={{ display: "flex" }}>
                            <div
                              style={{
                                marginRight: "10px",
                                color: opt.isTrue ? "green" : "black",
                              }}
                            >{`${ind + 1}.`}</div>
                            <Typography
                              style={{ color: opt.isTrue ? "green" : "black" }}
                            >
                              {opt.optionText}
                            </Typography>
                          </div>
                        ))}
                      </div>
                    </AccordionDetails>
                  </Accordion>
                ))
              }
            </div>
          }
          {
            !isQuizStarted
            ? <div style={{ padding: "16px 16px 5px 16px", marginTop: '20px', marginBottom: '20px', display: 'flex', flexDirection: 'row-reverse' }}>
                <button style={{ marginLeft: '6px', height: '30px', width: '80px' }} onClick={() => setIsModalOpen(true)}>
                  {inEditMode ? 'Submit' : 'Delete'}
                </button>
                <button style={{ height: '30px', width: '80px'}} onClick={handleInEditModeChange}>
                  {inEditMode ? 'Cancel' : 'Edit'}
                </button>
              </div>
            : <div></div>
          }
        </div>
      </Container>
      <DeleteModal visible={isModalOpen} onClose={handleCloseModal} onConfirm={inEditMode ? handleEditSubmit : handleDeleteSubmit} text={inEditMode ? 'update' : 'delete'} name={state.quizName} />
    </Box>
  );
}

export default IndividualSchedule;
