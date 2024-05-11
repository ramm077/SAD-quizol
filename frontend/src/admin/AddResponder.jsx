import React, { useState } from "react";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Container from "@mui/material/Container";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import { Typography } from "@mui/material";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Image from "../images/bg_image.jpg";
import baseURL from "../BaseUrl";
import { useAxiosInterceptors } from "../useAxiosInterceptors";
import { toast } from "react-toastify";
import publicUrl from "../publicURL";

const AddResponder = () => {

  useAxiosInterceptors()

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const navigate = useNavigate();
  const [successMessage, setSuccessMessage] = useState("");

  const [signupData, setsignupData] = useState({
    firstName: "",
    lastName: "",
    emailId: "",
    phoneNumber: "",
    password: "Welcome@123",
  });
  const [errors, setErrors] = useState({
    firstName: "",
    lastName: "",
    emailId: "",
    phoneNumber: "",
    password: "",
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    setsignupData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };
  
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {
      firstName: signupData.firstName !== "" ? "" : "First Name is required",
      emailId: emailRegex.test(signupData.emailId) ? "" : "Enter Valid Email",
      phoneNumber:
        signupData.phoneNumber.length !== 10
          ? "Mobile Number Length should be 10"
          : "",
    };
    setErrors(newErrors);
  
    let validForm = false;
    if (Object.values(newErrors).every((error) => error === "")) {
      validForm = true;
    }
  
    if (validForm) {
      try {
        const response = await axios.post(publicUrl + "/auth/signUp", {
          emailId: signupData.emailId,
          firstName: signupData.firstName,
          lastName: signupData.lastName,
          password: signupData.password,
          phoneNumber: signupData.phoneNumber,
        });
  
        if (response.status === 201) {
          setSuccessMessage("Successfully Added Responder");
          setTimeout(() => {
            setSuccessMessage("");
            navigate('/admin/responders_list',{replace:true})
          }, 2000); // Adjust the duration according to your preference
        } else if (response.status === 200) {
          console.log(response.data);
          toast.success(response.data)
          navigate('/admin/responders_list')
        } else {
          console.log("Unable to Create");
        }
      } catch (error) {
        console.error(error);
        toast.warning(error.response.data.message)
      }
    }
  };
  

  return (
    <>
      <style>
        {`
          body {
            margin: 0;
            padding: 0;
            background: url(${Image}) center/cover no-repeat fixed; // Set the background image for the body
            
            overflow: hidden;
          }
        `}
      </style>
      <Container  component="main"
        maxWidth="xs"
        style={{
          marginTop:'64px',
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "90vh",}}>
        <Paper
          elevation={3}
          style={{
            padding: 20,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            border: "0.5px solid black",
            boxSizing: "border-box",
          }}
        >
          <form onSubmit={handleSubmit} >
            <Typography
              variant="h6"
              style={{ textAlign: "center", fontWeight: "bold" }}
            >
              Add Responder
            </Typography>
            <TextField
              label="Email"
              type="email"
              variant="outlined"
              margin="normal"
              fullWidth
              value={signupData.emailId}
              onChange={handleChange}
              required
              name="emailId"
              error={!!errors.emailId}
              helperText={errors.emailId}
            />
            <TextField
              label="First Name"
              variant="outlined"
              margin="normal"
              fullWidth
              value={signupData.firstName}
              onChange={handleChange}
              required
              name="firstName"
              error={!!errors.firstName}
              helperText={errors.firstName}
            />
            <TextField
              label="Last Name"
              variant="outlined"
              margin="normal"
              fullWidth
              value={signupData.lastName}
              onChange={handleChange}
              error={!!errors.lastName}
              helperText={errors.lastName}
              name="lastName"
            />
            <TextField
              label="Mobile"
              type="tel"
              variant="outlined"
              margin="normal"
              fullWidth
              value={signupData.phoneNumber}
              onChange={handleChange}
              required
              name="phoneNumber"
              error={!!errors.phoneNumber}
              helperText={errors.phoneNumber}
            />
            <TextField
              label="Password"
              type="password"
              variant="outlined"
              margin="normal"
              fullWidth
              value={signupData.password}
              required
              name="password"
              disabled
            />
            <Box
              sx={{
                display: "flex",
                justifyContent: "center",
                marginTop: "20px",
              }}
            >
              <Button
                type="submit"
                variant="contained"
                size="large"
                color="primary"
                style={{ backgroundColor: "rgba(46,182,174,1)",marginTop:'-8px' }}
              >
                Add
              </Button>
            </Box>
          </form>
          {successMessage && (
  <Typography
    sx={{ textAlign: "center", marginTop: "10px", color: "green" }}
  >
    {successMessage}
  </Typography>
)}
        </Paper>
      </Container>
    </>
  );
};

export default AddResponder;
