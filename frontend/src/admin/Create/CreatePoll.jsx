import React, { useEffect, useState } from "react";
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
} from "@mui/material";
import CheckIcon from "@mui/icons-material/Check";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import baseURL from "../../BaseUrl";
import { toast } from "react-toastify";
import { useAxiosInterceptors } from "../../useAxiosInterceptors";
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';

const CreatePoll = () => {

  useAxiosInterceptors();

  const fetchAllQuestions = async () => {
    await axios
      .get(baseURL + "/question/all")
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
        if(e.response.status === 404) return 
        const list = e.response.data.filter(ele => ele.questionType === 'MULTIPLE')
        .map((obj) => {
          const qn = {
            id: obj.questionId,
            text: obj.questionText,
            options: obj.optionList
          };
          return qn;
        });
        console.log(list);
        setNames(list);
      });
  };

  useEffect(() => {
    fetchAllQuestions();
  }, []);

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

  // const []

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

  const initQuiz = {
    quizName: "",
    quizType: "TEST",
    questionIds: [],
  };

  const [question, setQuestion] = useState(initQuestion);
  const [quiz, setQuiz] = useState(initQuiz);
  const navigate = useNavigate();

  const handleQuestionIds = (e, v) => {
    if(!v) {
      setQuestion(initQuestion);
      setQuiz({
        ...quiz,
        questionIds: []
      })
      return
    }
    const list = []
    list.push(v.id)
    setQuiz({
      ...quiz,
      questionIds: list
    });
    setQuestion({
      ...question,
      questionText: v.text,
      optionList: v.options.map(op => {
        const obj ={
          optionText: op.optionText,
          isTrue: op.isTrue 
        }
        return obj
      })
    })
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
    if(question.questionText === '' || question.optionList[0].optionText === '') {
      toast.warning('Question cannot be empty')
      return
    }
    await axios
      .post(baseURL + "/question/create", JSON.stringify(question), {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((res) => {
        if (res.status === 201) {
          fetchAllQuestions();
          setQuestion(initQuestion);
          setOpenQuestionModal(!openQuestionModal);
        } else throw new Error(res.status);
      })
      .catch((e) => console.log(e));
  };

  const handleSubmit = async() => {
    console.log(quiz);
    const list = []
    list.push(question)
    if(quiz.quizName === '' ) {
      toast.warning('Poll Name cannot be empty!')
      return
    } else if(question.questionText === '' ) {
      toast.warning('Question cannot be empty!')
      return
    } else if(question.optionList[0].optionText === '' || question.optionList[1].optionText === '') {
      toast.warning('Options cannot be empty!')
      return
    }
    await axios
      .post(baseURL + "/quiz/create", {
        quizName: quiz.quizName,
        quizType: "POLL",
        questionIds: [],
        questionList: list
      }, {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        if (res.status === 201) {
          toast.success('Poll Created!')
          navigate("/admin/schedule/poll");
        }
        else throw new Error(res.status);
      })
      .catch((e) => console.log(e));
  };

  const handleAddOption = () => {
    const list = question.optionList
    list.push({
      optionText: "",
      isTrue: false,
    })
    
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

  return (
    <Container>
      <Card sx={{ width: "75%", margin: "auto", mt: "5rem", mb: 2 }}>
        <CardContent>
          <Typography variant="h5">Create Poll</Typography>
          <TextField
            sx={{ mb: 1, mt: 2, width: "100%" }}
            label="Poll Name"
            variant="outlined"
            value={quiz.quizName}
            onChange={handleQuizNameChange}
          />
          {/* <p style={{textAlign: 'center', fontSize: 'small', margin: '0px'}}>Select existing one</p> */}
          <Autocomplete 
            sx={{mb: 1}}
            onChange={handleQuestionIds}
            options={names}
            getOptionLabel={(option) => option.text}
            renderInput={(params) => (
              <TextField
                {...params}
                variant="outlined"
                label="Select Existing One"
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
          <Box sx={{ "& .MuiTextField-root": { mb: 1, width: "100%" } }}>
            <TextField
              label="Create New Poll Question"
              variant="outlined"
              value={question.questionText}
              onChange={handleQuestionChange}
              disabled={quiz.questionIds.length > 0}
            />
            {question.optionList.map((opt, index) => (
              <Box key={index} sx={{ display: "flex", alignItems: "center" }}>
                <TextField
                  label={`Option ${index + 1}`}
                  variant="outlined"
                  value={opt.optionText}
                  onChange={(event) => handleOptionChange(index, event)}
                  disabled={quiz.questionIds.length > 0}
                />
                { (question.optionList.length > 2 && !quiz.questionIds.length > 0) && <RemoveCircleOutlineIcon sx={{color: 'red', ml: 2}} onClick={() => handleDeleteOption(index)} />}
              </Box>
            ))}
          </Box>
          
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
              onClick={handleAddOption}
            >
              Add Option
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
            {question.optionList.map((opt, index) => (
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

              </Box>
            ))}
            <div style={{ display: "flex", justifyContent: "center" }}>
              <Button
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

export default CreatePoll;
