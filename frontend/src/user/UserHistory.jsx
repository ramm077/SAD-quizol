import React, { useEffect, useState } from "react";
import "./NoQuizzesPage.css";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  IconButton,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import TablePagination from "@mui/material/TablePagination";
import VisibilityIcon from "@mui/icons-material/Visibility";
import axios from "axios";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import baseURL from "../BaseUrl";
import { useAxiosInterceptors } from "../useAxiosInterceptors";

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

const UserHistory = () => {
  useAxiosInterceptors();

  const userId = sessionStorage.getItem("id");
  const [schedules, setSchedules] = useState([]);
  const [openSection, setOpenSection] = useState(null);
  const [page, changePage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [open, setOpen] = useState(false);
  const [responseData, setResponseData] = useState({});
  //const [responseData,setResponseData] = useState();
  

  const fetchScheduleData = async () => {
    try {
      let response = await axios.get(
        baseURL + `/schedule/all?userId=${userId}`
      );
      //console.log(response.data);
      let now = new Date().getTime();

      if (response.status === 200) {
        const filteredData = response.data.filter(
          (item) =>
            new Date(item.endTime).getTime() < now &&
            new Date(item.startTime).getTime() < now
        );
        console.log(filteredData);
        setSchedules(filteredData);
      } else {
        throw new Error(response.status);
      }
    } catch (error) {
      console.log(error.data);
    }
  };

  useEffect(() => {
    fetchScheduleData();
  }, []);

  const handleClose = () => {
    setResponseData({});
    setOpen(false);
  };

  const handlePageChange = (e, newpage) => {
    changePage(newpage);
  };
  const handleRowsPerPageChange = (e) => {
    setRowsPerPage(+e.target.value);
    changePage(0);
  };
  const toggleSection = (sectionId) => {
    setOpenSection((prevOpenSection) =>
      prevOpenSection === sectionId ? null : sectionId
    );
  };

  const handleViewClick = async (schedulerId) => {
    // console.log(quizId, "id");?

    axios.get(baseURL+`/response/all?schedulerId=${schedulerId}`)
    .then(res => {
      console.log(res)
    })
    .catch(e => console.log(e))

    try {
      let response = await axios.get(
        baseURL +
          `/response/display?userId=${userId}&schedulerId=${schedulerId}`
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

  return (
    <>
      {schedules.length === 0 ? (
        <div className="no-schedules-container">
          <p className="message">No previous schedules for you...</p>
          <p className="check-again">Check again later!</p>
        </div>
      ) : (
        <div style={{ textAlign: "center" }}>
          <Paper sx={{ width: "90%", marginLeft: "5%", marginTop: "120px"}}>
            <TableContainer
              // component={Paper}
              // style={{
              //   margin: "20px",
              //   padding: "20px",
              //   width: "auto",

              //   marginBottom: "1px",
              // }}
            >
              <Table stickyHeader>
                <TableHead>
                  <TableRow>
                    <TableCell
                      style={{ backgroundColor: "black", color: "white" }}
                    >
                      S.No
                    </TableCell>
                    <TableCell
                      style={{ backgroundColor: "black", color: "white"}}
                    >
                      Quiz Name
                    </TableCell>
                    <TableCell style={{ backgroundColor: 'black', color: 'white' }}>Type</TableCell>
                    <TableCell
                      style={{ backgroundColor: "black", color: "white" }}
                    >
                      Action
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {schedules
                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                    .map((row, index) => (
                      <TableRow key={index} >
                        <TableCell>{index + 1}</TableCell>
                        <TableCell>{row.quizName}</TableCell>
                        <TableCell>{row.quizType}</TableCell>
                        <TableCell>
                          <IconButton
                            color="primary"
                            onClick={() => handleViewClick(row.schedulerId)}
                          >
                            <VisibilityIcon />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </TableContainer>
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
                  sx={{ backgroundColor: "rgba(46,182,174,1)","&:hover":{backgroundColor: "rgba(46,182,174,1)"} }}
                  onClick={handleClose}
                >
                  Cancel
                </Button>
              </DialogActions>
            </Dialog>
            <TablePagination
              rowsPerPageOptions={[5, 8]}
              page={page}
              rowsPerPage={rowsPerPage}
              component="div"
              onPageChange={handlePageChange}
              count={schedules.length}
              onRowsPerPageChange={handleRowsPerPageChange}
            ></TablePagination>

            {/* <QuizPopover onClose={handleClosePopover} quizData={selectedQuiz} /> */}
          </Paper>
        </div>
      )}
    </>
  );
};

export default UserHistory;