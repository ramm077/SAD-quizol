import React, { useState } from "react";
import {
  Card,
  CardContent,
  TextField,
  Container,
  Typography,
  Autocomplete,
  MenuItem,
  Button,
  Modal,
  Box,
  FormControlLabel,
  Checkbox,
  TextareaAutosize,
} from "@mui/material";
import CheckIcon from "@mui/icons-material/Check";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import baseURL from "../../BaseUrl";
import { useAxiosInterceptors } from "../../useAxiosInterceptors";
import { toast } from "react-toastify";
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';


const CreateQuiz = () => {

  useAxiosInterceptors();

  const fetchAllQuestions = async (type) => {
    await axios
      .get(baseURL + `/question/all?inActive=${false}`)
      .then((res) => {
        console.log("ok");
        // if (res.status === 200) {
        //   const list = res.data.map((obj) => {
        //     const qn = {
        //       id: obj.questionId,
        //       text: obj.questionText,
        //     };
        //     return qn;
        //   });
        //   console.log(list);
        //   setNames(list);
        // } else throw new Error(res.status);
      })
      .catch((e) => {
        console.log("error");
        console.log(e);
        if(e.response.status === 404) {
          setNames([])
          return 
        }
        let list = e.response.data.map((obj) => {
          const qn = {
            id: obj.questionId,
            text: obj.questionText,
            type: obj.questionType
          };
          return qn;
        });
        if(type === 'TEST') {
          list = list.filter(qn => qn.type === 'MULTIPLE')
        }
        console.log(list);
        setNames(list);
      });
  };


  const [names, setNames] = useState([]);
  const modalStyle = {
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
  const [openQuestionModal, setOpenQuestionModal] = useState(false);

  const types = ['TEST', 'OPEN_ENDED']
  const openQuestionTypes = ['MULTIPLE', 'TEXT']


  const initQuestion = {
    questionType: 'MULTIPLE',
    questionText: "",
    optionList: [
      {
        optionText: "",
        isTrue: false,
      },
      {
        optionText: "",
        isTrue: false,
      }
    ],
  };

  const initOption = {
    optionText: "",
    isTrue: false,
  }


  const initQuiz = {
    quizName: '',
    quizType: '',
    questionIds: [],
    questionList: [],
    quizImageBase64URL: null
  };

  const [question, setQuestion] = useState(initQuestion);
  const [quiz, setQuiz] = useState(initQuiz);
  const [option, setOption] = useState(initOption)
  const navigate = useNavigate();

  const handleTypeChange = (e, v) => {
    console.log(v)
    setQuiz({
      ...quiz,
      quizType: v
    })
    if(v === 'TEST') {
      setQuestion({
        ...question,
        questionType: 'MULTIPLE'
      })
    }
    if(!v) {
      setNames([])
      return
    }
    fetchAllQuestions(v)
  }

  const handleQuestionTypeChange = (e, v) => {
    console.log(v)
    setQuestion({
      ...question,
      questionType: v
    })
    setOption(initOption)
  }

  const handleQuestionIds = (e, v) => {
    setQuiz({
      ...quiz,
      questionIds: v.map((obj) => obj.id),
    });
  };

  const handleQuizNameChange = (e) => {
    setQuiz({
      ...quiz,
      quizName: e.target.value,
    });
  };

  const handleQuestionChange = (event) => {
    // setQuestion(event.target.value);
    setQuestion({
      ...question,
      questionText: event.target.value,
    });
  };

  const handleOptionChange = (index, event) => {
    const updatedOptions = question.optionList;
    updatedOptions[index].optionText = event.target.value;
    setQuestion({
      ...question,
      optionList: updatedOptions,
    });
  };

  const handleCheckboxChange = (index) => {
    const updatedOptions = question.optionList;
    updatedOptions[index].isTrue = !updatedOptions[index].isTrue;
    setQuestion({
      ...question,
      optionList: updatedOptions,
    });
  };

  const handleCreate = async() => {
    // Submit the data or process it here
    console.log(question);
    if(question.questionText === '' || question.questionType === '') {
      toast.warning('Question cannot be empty')
      return
    }

    if(question.questionType === 'TEXT') {
      if(option.optionText === '') {
        console.log(option)
        toast.warning('Answer cannot be empty!')
        return
      }
    } else if(question.optionList[0].optionText === '' || question.optionList[1].optionText === '') {
      toast.warning('Options cannot be empty!')
      return
    } 

    const questionBody = question.questionType === 'MULTIPLE' ? question : {
      questionType: question.questionType,
      questionText: question.questionText,
      optionList: [{
        optionText: option.optionText,
        isTrue: true
      }]
    }
    await axios
      .post(baseURL + "/question/create", JSON.stringify(questionBody), {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((res) => {
        if (res.status === 201) {
          fetchAllQuestions();
          setQuestion(initQuestion);
          setOpenQuestionModal(!openQuestionModal);
          toast.success('Question Created')
        } else throw new Error(res.status);
      })
      .catch((e) => console.log(e));
  };

  const handleSubmit = async() => {
    console.log(quiz)
    if(quiz.quizName === '' || quiz.quizType === '' || quiz.questionIds.length === 0) {
      toast.warning('Quiz cannot be empty')
      return
    }
    console.log(quiz);
    await axios
      .post(baseURL + "/quiz/create", JSON.stringify(quiz), {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        if (res.status === 201) {
          toast.success('Quiz Created!')
          navigate("/admin/schedule/quiz");
        }
        else throw new Error(res.status);
      })
      .catch((e) => console.log(e));
  };

  const handleAddOption = () => {
    const list = question.optionList
    list.push(initOption)
    setQuestion({
      ...question,
      optionList: list
    })
  }

  const handleDeleteOption = (id) => {
    console.log(id)
    const list = question.optionList.filter((opt, index) => index !== id)
    setQuestion({
      ...question,
      optionList: list
    })
  }

  const handleAnswerChange = (e) => {
    setOption({
      ...option,
      optionText: e.target.value
    })
  }

  return (
    <Container>
      <Card sx={{ width: "75%", margin: "auto", mt: "5rem", mb: 2 }}>
        <CardContent>
          <Typography variant="h5">Create Quiz</Typography>
          
          <TextField
            sx={{ marginY: 2, width: "100%" }}
            label="Quiz Name"
            variant="outlined"
            value={quiz.quizName}
            onChange={handleQuizNameChange}
          />
          <Autocomplete
            sx={{mb: 2}}
            onChange={handleTypeChange}
            options={types}
            getOptionLabel={(option) => option}
            renderInput={(params) => (
              <TextField
                {...params}
                variant="outlined"
                label="Type"
                placeholder="Select type"
              />
            )}
            renderOption={(props, option, { selected }) => (
              <MenuItem
                {...props}
                key={option}
                value={option}
                sx={{ justifyContent: "space-between" }}
              >
                {option}
                {selected ? <CheckIcon color="info" /> : null}
              </MenuItem>
            )}
          />
          <Autocomplete
            multiple
            onChange={handleQuestionIds}
            options={names}
            getOptionLabel={(option) => option.text}
            disableCloseOnSelect
            renderInput={(params) => (
              <TextField
                {...params}
                variant="outlined"
                label="Question"
                placeholder="Select Questions"
              />
            )}
            renderOption={(props, option, { selected }) => (
              <MenuItem
                {...props}
                key={option.id}
                value={option}
                sx={{ justifyContent: "space-between" }}
              >
                {option.text}
                {selected ? <CheckIcon color="info" /> : null}
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
              sx={{ mr: 4 ,backgroundColor: 'rgba(46,182,174,1)'}}
              onClick={() => (quiz.quizType === '' || quiz.quizType === null) ? toast.info('Select Quiz Type first') : setOpenQuestionModal(!openQuestionModal)}
            >
              Create New Question
            </Button>
            <Button variant="contained" style={{ backgroundColor:'rgba(46,182,174,1)'}} onClick={handleSubmit}>
              Submit
            </Button>
          </div>
        </CardContent>
      </Card>

      <Modal
        open={openQuestionModal}
        onClose={() => setOpenQuestionModal(!openQuestionModal)}
        sx={{
          "& .MuiModal-backdrop": {
            backgroundColor: "rgba(0, 0, 0, 0.2)",
          },
        }}
      >
        <Box sx={modalStyle}>
          <Box sx={{ "& .MuiTextField-root": { m: 1, width: "95%" } }}>
            <TextField
              label="Question"
              variant="outlined"
              value={question.questionText}
              onChange={handleQuestionChange}
            />
            {
              quiz.quizType === 'TEST'
              ? 
              <TextField
                label="Type"
                variant="outlined"
                value='MULTIPLE'
                disabled
              />
              : 
              <Autocomplete
                onChange={handleQuestionTypeChange}
                options={openQuestionTypes}
                getOptionLabel={(option) => option}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    variant="outlined"
                    label="Type"
                    placeholder="Select type"
                  />
                )}
                renderOption={(props, option, { selected }) => (
                  <MenuItem
                    {...props}
                    key={option}
                    value={option}
                    sx={{ justifyContent: "space-between" }}
                  >
                    {option}
                    {selected ? <CheckIcon color="info" /> : null}
                  </MenuItem>
                )}
              />
            }
            {
              question.questionType === 'TEXT'
              ? <TextareaAutosize 
                  minRows={3} 
                  placeholder="Enter the answer" 
                  style={{marginLeft: '9px', width: '95%', fontSize: '0.875rem', lineHeight: 1.5, padding: '8px 12px', borderRadius: '8px'}} 
                  value={option.optionText}
                  onChange={handleAnswerChange}
                />
              : question.optionList.map((opt, index) => (
                <Box key={index} sx={{ display: "flex", alignItems: "center" }}>
                  <TextField
                    label={`Option ${index + 1}`}
                    variant="outlined"
                    value={opt.optionText}
                    onChange={(event) => handleOptionChange(index, event)}
                  />
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={opt.isTrue}
                        onChange={() => handleCheckboxChange(index)}
                      />
                    }
                    label="Correct"
                  />
                  { question.optionList.length > 2 && <RemoveCircleOutlineIcon sx={{color: 'red'}} onClick={() => handleDeleteOption(index)} />}
                </Box>
              ))
            }
            <div style={{ display: "flex", justifyContent: "center" }}>
              {question.questionType !== 'TEXT' &&
                <Button
                  variant="contained"
                  style={{ backgroundColor:'rgba(46,182,174,1)'}}
                  onClick={handleAddOption}
                >
                  Add Option
                </Button>
              }
              <Button
                sx={{marginLeft: 2}}
                variant="contained"
                style={{ backgroundColor:'rgba(46,182,174,1)'}}
                onClick={handleCreate}
              >
                Create
              </Button>
            </div>
          </Box>
        </Box>
      </Modal>
    </Container>
  );
};

export default CreateQuiz;
