import React, { useState } from "react";
import {
  Box,
  Typography,
  Paper,
  Container,
  TextField,
  Button,
} from "@mui/material";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Image from "../../images/bg_image.jpg";
import publicUrl from "../../publicURL";
import { toast } from "react-toastify";

const ForgotPassword = () => {
  const [flag, setFlag] = useState(0);
  const [redText, setRedText] = useState("");
  const [submitText, setSubmitText] = useState("Send OTP"); // Default text
  const [disableButton, setDisableButton] = useState(false);
  const navigate = useNavigate();
  const [user, setUser] = useState({
    email: "",
    otp: "",
    password: "",
    confirmPassword: "",
  });

  const handleInputChange = (e) => {
    setRedText("");
    setUser({
      ...user,
      [e.target.name]: e.target.value,
    });

    if (e.target.name === "confirmPassword")
      user.password !== e.target.value
        ? setRedText("Password doesn't match")
        : setRedText("");
  };

  const sendOTP = () => {
     const isValidEmail = validateEmail();

     if(!isValidEmail){
      return; // Do not proceed if email is empty
    }
  
    setDisableButton(true);
    setSubmitText("Sending OTP ...");

    axios
      .post(publicUrl + '/auth/forgot-password', {
        emailId: user.email,
      })
      .then((res) => {
        if (res.status === 201) {
          setFlag(1);
          setSubmitText("Verify OTP");
        } else {
          setRedText("Email doesn't exist");
          throw new Error(res.status);
        }
      })
      .catch((e) => console.log(e))
      .finally(() => {
        setDisableButton(false);
      });
  };

  const validateEmail = () => {

    const emailRegex=  /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!user.email.trim()) {
      setRedText("Email cannot be empty");
      return false;
    }
    else if(!emailRegex.test(user.email)){
      setRedText("Invalid Email");
      return false;
    }
    else
    return true;
  };

  const verifyOTP = () => {
    setDisableButton(true);

    axios
      .post(publicUrl + '/auth/validate-otp', {
        emailId: user.email,
        enteredOTP: user.otp,
      })
      .then((res) => {
        if (res.status === 200) {
          setFlag(2);
          setSubmitText("Submit");
        } else {
          setRedText("Incorrect OTP or OTP has expired");
          throw new Error(res.status);
        }
      })
      .catch((e) => {
        setRedText("Incorrect OTP or OTP has expired");
        console.log(e);
      })
      .finally(() => {
        setDisableButton(false);
      });
  };

  const checkStrongPassword = (password) => {
    const strongPasswordRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
    return password.length >= 8 && strongPasswordRegex.test(password);
  };

  const sendPassword = () => {
    if (user.password === "") {
      setRedText("Password cannot be empty");
      return;
    }

    if (!checkStrongPassword(user.password)) {
      setRedText(
        "Password should have at least 8 characters, 1 uppercase, 1 special character, 1 number"
      );
      return;
    }

    setDisableButton(true);

    axios
      .post(publicUrl + '/auth/confirm-password', {
        emailId: user.email,
        password: user.password,
      })
      .then((res) => {
        if (res.status === 200) {
          setSubmitText("Password Changed Successfully!");
          toast.success('Password Updated!')
          navigate("/login");
        } else {
          setRedText("Error while updating password!");
          throw new Error(res.status);
        }
      })
      .catch((e) => console.log(e))
      .finally(() => {
        setDisableButton(false);
      });
  };

  return (
    <>
      <style>
        {`
          body {
            margin: 0;
            padding: 0;
            background: url(${Image}) center/cover no-repeat fixed;
            overflow: hidden;
          }
        `}
      </style>
      <Container
        component="main"
        maxWidth="xs"
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
        }}
      >
        <Paper
          elevation={8}
          style={{
            padding: 19,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            width: "90%",
            marginTop: "30px",
            // border: "0.2px solid black",
            boxSizing: "border-box",
            borderRadius: '16px'
          }}
        >
          <Box width="100%" my={2}>
            <>
              <Typography
                variant="h5"
                align="center"
                fontWeight="bold"
                gutterBottom
              >
                Forgot Password
              </Typography>
              <form>
                {flag !== 2 && (
                  <TextField
                    label="Email"
                    type="text"
                    fullWidth
                    margin="normal"
                    name="email"
                    value={user.email}
                    onChange={handleInputChange}
                  />
                )}

                {flag === 1 && (
                  <TextField
                    label="OTP"
                    type="text"
                    fullWidth
                    margin="normal"
                    name="otp"
                    value={user.otp}
                    onChange={handleInputChange}
                  />
                )}

                {flag === 2 && (
                  <>
                    <TextField
                      label="Password"
                      type="password"
                      fullWidth
                      margin="normal"
                      name="password"
                      value={user.password}
                      onChange={handleInputChange}
                    />
                    <TextField
                      label="Confirm Password"
                      type="password"
                      fullWidth
                      margin="normal"
                      name="confirmPassword"
                      value={user.confirmPassword}
                      onChange={handleInputChange}
                    />
                  </>
                )}

                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    color: "red",
                  }}
                >
                  <div>{redText}</div>
                </div>
                <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                  <Button
                    variant="contained"
                    style={{ marginTop: "20px", backgroundColor: "rgba(46,182,174,1)",color:'white' }}
                 onClick={() => {
                      flag === 0 ? validateEmail() && sendOTP() :
                      flag === 1 ? verifyOTP() : sendPassword();
                    }}
                    disabled={
                      flag === 2
                        ? user.password !== user.confirmPassword
                        : disableButton
                    }
                  >
                    {submitText}
                  </Button>
                </Box>
              </form>
            </>
          </Box>
        </Paper>
      </Container>
    </>
  );
};

export default ForgotPassword;
