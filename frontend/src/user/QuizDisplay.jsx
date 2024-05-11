import React, { useEffect, useState } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Checkbox,
  Button,
  FormControlLabel,
  FormGroup,
  TextareaAutosize,
  Dialog,
  DialogTitle,
  DialogContent,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  DialogActions,
  RadioGroup,
  Radio,
} from '@mui/material';
import { useLocation, useNavigate } from 'react-router-dom';
import Header from './Header';
import axios from 'axios';
import baseURL from '../BaseUrl';
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import DeleteModal from '../components/common/DeleteModal';
import { useAxiosInterceptors } from '../useAxiosInterceptors';
import { toast } from 'react-toastify';
import Image from "../images/home_img.jpg";

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

const QuizDisplay = () => {


  useAxiosInterceptors()

  const {state}=useLocation();
  const {schedulerId, quizId} = state
  
  const fetchQuizData = async() => {
    axios.get(`${baseURL}/quiz?quizId=${quizId}`)
    .then(res => {
      console.log(res.data)
    })
    .catch(e => {
      console.log(e)
      if(e.response.status === 302) {
        setQuiz(e.response.data)
        setIsLoading(false)
      }
    }) 
  }
  
  const fetchResponseData = () => {
    axios.get(`${baseURL}/response/display?userId=${sessionStorage.getItem('id')}&schedulerId=${schedulerId}`)
    .then(res => {
      console.log(res)
    })
    .catch(e => {
      console.log(e)
      const data = e.response.data
      if(e.response.status === 302) {
        const list = data.questionList.map(res => {
          const obj = {
            questionId: res.questionId,
            responseId: res.response.responseId,
            textResponseId: res.response.textResponse?.textResponseId,
            optionResponseId: res.response.optionResponses?.optionResponseId,
            questionType: res.questionType,
            chosenOptions: res.response.optionResponses?.chosenOptions,
            answerText: res.response.textResponse?.answerText,
            finalSubmit: data.finalSubmit
          }
          return obj
        })
        console.log('parse',list)
        setAllResponses(list)
        setCurrentResponse(list[0])
      }
    })
  }

  useEffect(() => {
    fetchQuizData()
    fetchResponseData()
  },[])

  const initResponse = {
    questionId: '',
    questionType: '',
    chosenOptions: [],
    answerText: ''
  }

  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true)
  const [openModal1, setOpenModal1] = useState(false)
  const [openModal2, setOpenModal2] = useState(false)
  const [quiz,setQuiz]=useState({});
  //const [poll,setPoll]=useState({});
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [allResponses, setAllResponses] = useState([])
  const [currentResponse, setCurrentResponse] = useState(initResponse)
  const [open, setOpen] = useState(false)
  const [openSection, setOpenSection] = useState(false)
  const [responseData, setResponseData] = useState({})

  const handleClose = () => {
    setResponseData({});
    setOpen(false);
  };

  const handleCloseModal1 = () => setOpenModal1(!openModal1)
  const handleCloseModal2 = () => setOpenModal2(!openModal2)

  const handleCheckboxChange = (optionId) => {
    let list = currentResponse.chosenOptions ? currentResponse.chosenOptions : []
    if(list.includes(optionId)) {
      list = list.filter(id => id !== optionId)
      console.log('list')
      console.log(list)
    }
    else list.push(optionId)
    
    setCurrentResponse({
      ...currentResponse,
      questionId: quiz.questionList[currentQuestion].questionId,
      questionType: quiz.questionList[currentQuestion].questionType,
      chosenOptions: list,
      answerText: ''
    })
  };

  const handleRadioChange = (e) => {
    const optionId = e.target.value
    console.log('optionId: ',optionId)
    let list = []
    list.push(optionId)
    
    setCurrentResponse({
      ...currentResponse,
      questionId: quiz.questionList[currentQuestion].questionId,
      questionType: quiz.questionList[currentQuestion].questionType,
      chosenOptions: list,
      answerText: ''
    })
  };


  const handleAnswerChange = (e) => {
    setCurrentResponse({
      ...currentResponse,
      questionId: quiz.questionList[currentQuestion].questionId,
      questionType: quiz.questionList[currentQuestion].questionType,
      chosenOptions: [],
      answerText: e.target.value
    })
    // console.log(!currentResponse.chosenOptions.length)
  }

  const handleNextClick = () => {
    setCurrentQuestion((prevQuestion) => prevQuestion + 1);
    let fl = true
    let list = allResponses
    list = list.map(res => {
      if(res.questionId === currentResponse.questionId) {
        fl = false
        console.log('matched',currentResponse)
        return currentResponse
      }
      return res
    })

    if(fl)  list.push(currentResponse)
    
    console.log(list)
    setAllResponses(list)
    setCurrentResponse(allResponses[currentQuestion + 1] ? allResponses[currentQuestion + 1] : initResponse)
  };

  const handlePrevClick = () => {
    setCurrentQuestion((prevQuestion) => prevQuestion - 1);
    setCurrentResponse(allResponses[currentQuestion - 1])
  };

  const handleQuizSubmit = (finalSubmit) => {

    let list = quiz.quizType === 'POLL' ? [] : allResponses
    list.push(currentResponse)
    console.log('list',list)

    axios.post(`${baseURL}/response/capture?userId=${sessionStorage.getItem('id')}&schedulerId=${schedulerId}`, {
      responseList: list,
      finalSubmit: finalSubmit
    }, {
      headers: {'Content-Type': 'application/json'}
    })
    .then(res => {
      console.log(res)
      if(res.status === 201) {
        toast.success(finalSubmit ? 'Final submitted!' : 'Submitted!')
        navigate('/user_dashboard')
      }
    })
    .catch(e => console.log(e))
  }

  const handleQuizUpdate = (finalSubmit) => {
    let list = quiz.quizType === 'POLL' ? [] : allResponses
    list.push(currentResponse)

    axios.put(`${baseURL}/response/update?userId=${sessionStorage.getItem('id')}&schedulerId=${schedulerId}`, {
      textResponses: list.filter(res => res.questionType === 'TEXT').map(ele => {
        const obj = {
          responseId: ele.responseId,
          textResponseId: ele.textResponseId,
          answerText: ele.answerText
        }
        return obj
      }),
      optionResponses: list.filter(res => res.questionType !== 'TEXT'),
      // .map(ele => {
      //   const obj = {
      //     questionId: ele.questionId,
      //     responseId: ele.responseId,
      //     optionResponseId: ele.optionResponseId,
      //     chosenOptions: ele.chosenOptions
      //   }
      // }),
      finalSubmit: finalSubmit
    })
    .then(res => {
      console.log(res)
      if(res.status === 200) {
        toast.success(finalSubmit ? 'Final submitted!' : 'Updated!')
        navigate('/user_dashboard')
      }
    })
    .catch(e => console.log(e))
  }

  const toggleSection = (sectionId) => {
    setOpenSection((prevOpenSection) =>
      prevOpenSection === sectionId ? null : sectionId
    );
  };

  const handleViewClick = async () => {
    // console.log(quizId, "id");?

    try {
      let response = await axios.get(
        baseURL +
          `/response/display?userId=${sessionStorage.getItem('id')}&schedulerId=${schedulerId}`
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


  const isLastQuestion = currentQuestion === quiz.questionList?.length - 1;

  return (
    <>
      <style>
        {`
          body {
            background: url(${Image}) center/cover no-repeat fixed; // Set the background image for the body
          }
        `}
      </style>
      <Header />

      {
      isLoading 
        ? <div>Loading...</div>
        : <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
        }}
      >
        {
          !currentResponse.finalSubmit
          ? <div>
              <Card
                elevation={8}
                style={{
                  width: '500px',
                  height: '300px',
                  position: 'relative',
                  // border: '2px solid black', // Set border to black
                  borderRadius: '16px'
                }}
              >
                <CardContent>
                  {
                    quiz.quizType === 'POLL'
                    ? <Typography variant="h5" component="div" sx={{mb: 3}}>
                        {quiz.questionList[currentQuestion].questionText}
                      </Typography>
                    : <><Typography variant="h5" component="div">
                        Question {currentQuestion + 1}:
                      </Typography>
                      <Typography variant="body1" sx={{mt: 1}} gutterBottom>
                        {quiz.questionList[currentQuestion].questionText}
                      </Typography></>
                  }
                  {quiz.quizType === 'POLL'
                    ? <RadioGroup
                        value={`${currentResponse.chosenOptions[0]}`}
                        onChange={handleRadioChange}
                      >
                        {quiz.questionList[currentQuestion]?.optionList.map((option, i) => (
                          <FormControlLabel
                            key={i}
                            value={option.optionId}
                            control={ <Radio /> }
                            label={option.optionText}
                          />
                        ))}
                      </RadioGroup>
                    : quiz.questionList[currentQuestion].optionList.map((option, i) => (
                        <FormGroup key={i}>
                          <FormControlLabel
                          control={
                            <Checkbox
                              checked={currentResponse.chosenOptions?.includes(option.optionId)}
                              onChange={() => handleCheckboxChange(option.optionId)}
                            />
                          }
                          label={option.optionText}
                        />
                        </FormGroup>
                      ))
                  }
                  {
                    quiz.questionList[currentQuestion].questionType === 'TEXT' &&
                    <TextareaAutosize 
                      minRows={4} 
                      placeholder="Enter the answer" 
                      style={{ marginTop: '5px', width: '95%', fontSize: '14px', lineHeight: 1.5, padding: '8px 12px', borderRadius: '8px'}} 
                      value={currentResponse.answerText}
                      onChange={handleAnswerChange}
                    />
                  }
                  <div
                    style={{
                      position: 'absolute',
                      bottom: '20px',
                      right: '20px',
                      display: 'flex',
                      gap: '10px', // Set the gap between buttons
                    }}
                  >
                    <Button
                      variant="contained"
                      
                      disabled={currentQuestion === 0}
                      onClick={handlePrevClick}
                      sx={{backgroundColor:'rgba(46,182,174,1)',"&:disabled":{backgroundColor: '#bdc3c7', 
                      color: 'black'}, "&:hover":{backgroundColor:'rgba(46,182,174,1)'}}}
                    >
                      Previous
                    </Button>
                    
                    {
                      isLastQuestion
                      ? <><Button 
                          variant="contained" 
                          onClick={isLastQuestion ? handleCloseModal1 : handleNextClick} 
                          disabled={!currentResponse.chosenOptions?.length && currentResponse.answerText === ''}
                          sx={{backgroundColor:'rgba(46,182,174,1)',"&:disabled":{backgroundColor: '#bdc3c7', 
                          color: 'black'},"&:hover":{backgroundColor:'rgba(46,182,174,1)'}}}
                        >
                          {currentResponse.responseId ? 'Update' : 'Finish'}
                        </Button></>
                      : <><Button 
                          variant="contained" 
                          onClick={handleNextClick} 
                          disabled={!currentResponse.chosenOptions?.length && currentResponse.answerText === ''}
                          sx={{backgroundColor:'rgba(46,182,174,1)',"&:disabled":{backgroundColor: '#bdc3c7', 
                          color: 'black'},"&:hover":{backgroundColor:'rgba(46,182,174,1)'}}}
                        >
                          Next
                        </Button></>
                    }
                    {/* {currentResponse.responseId ? 'Update' : 'Finish'} */}
                  </div>
                </CardContent>
                <DeleteModal visible={openModal1} onClose={handleCloseModal1} onConfirm={() => {currentResponse.responseId ? handleQuizUpdate(false) : handleQuizSubmit(false)}} text={currentResponse.responseId ? 'Update' : 'Submit'} name={quiz.quizName} />
                <DeleteModal visible={openModal2} onClose={handleCloseModal2} onConfirm={() => {currentResponse.responseId ? handleQuizUpdate(true) : handleQuizSubmit(true)}} text='Final Submit' name={quiz.quizName} />
              </Card>
              {isLastQuestion && 
                <div style={{display: 'flex', justifyContent: 'center'}}>
                  <Button variant='contained' color='error' sx={{ mt: 3}} onClick={() => setOpenModal2(!openModal2)} >Final Submit</Button>
                </div>
              }
            </div>
          : <div>
              <Typography variant='h4'>You have already submitted the quiz</Typography>
              {/* <Typography variant='subtitle1' textAlign='center' marginTop={3}>Click to view your responses</Typography> */}
              <div style={{display: 'flex', justifyContent: 'center'}}>
                <Button variant='contained' 
                  size='large' 
                  sx={{backgroundColor:'rgba(211, 211, 211, 1)', marginTop: 3,
                  color: 'black',"&:hover":{backgroundColor:'rgba(180,180,180,1)'}, mr: 3}}
                  onClick={() => navigate('/user_dashboard')}
                >
                  Back
                </Button>
                <Button variant='contained' 
                  size='large' 
                  sx={{backgroundColor:'rgba(46,182,174,1)', marginTop: 3,
                  color: 'black',"&:hover":{backgroundColor:'rgba(46,182,174,1)'}}}
                  onClick={handleViewClick}
                >
                  view responses
                </Button>
              </div>
              <Dialog
              open={open}
              onClose={handleClose}
              key={responseData.quizId}
              PaperProps={{
                style: customStyles.root,
              }}
            >
              <DialogTitle>Response</DialogTitle>
              <DialogContent>
                {Object.keys(responseData).length !== 0 ? (
                  <div>
                    <Typography>Quiz Name: {responseData.quizName}</Typography>
                    <Typography>Quiz Type: {responseData.quizType}</Typography>
                    {responseData.quizType === "TEST" && (
                      <Typography>
                        Quiz Score: {responseData.quizScore}
                      </Typography>
                    )}
                    {responseData.questionList &&
                      responseData.questionList.map((row, index) => (
                        <Accordion
                          key={row.questionId}
                          expanded={openSection === row.questionId}
                          onChange={() => toggleSection(row.questionId)}
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
            </div>
        }
      </div>
      }
    </>
  );
};

export default QuizDisplay;