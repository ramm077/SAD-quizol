import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Box,
  Typography,
  Paper,
  Container,
  TextField,
  Button,
  Checkbox,
  FormControlLabel,
  FormControl,
  InputLabel,
  OutlinedInput,
  FormHelperText,
  FormGroup,
} from "@mui/material";
import Image from "../../images/bg_image.jpg";
import axios from "axios";
import publicUrl from "../../publicURL";
import { toast } from "react-toastify";
import { useDispatch } from "react-redux";
import {setUser} from '../../redux/userActions';


const Login = ({ callbackID, callbackToken, callbackRole, callBackName }) => {
  const dispatch = useDispatch();
  
  useEffect(() => {
    if (sessionStorage.token) navigate("/user_dashboard");
  }, []);

  const navigate = useNavigate();
  const [value, setValue] = useState(0);
  const [checked, setChecked] = useState(false);
  const [redText, setRedText] = useState("");
  const [loginData, setLoginData] = useState({
    emailId: "",
    password: "",
  });

  const checkStrongPassword = (password) => {
    // At least 8 characters, 1 uppercase, 1 special character, 1 number
    const strongPasswordRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
    return password.length >= 8 && strongPasswordRegex.test(password);
  };
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const [signupData, setsignupData] = useState({
    firstName: "",
    lastName: "",
    emailId: "",
    phoneNumber: "",
    password: "",
  });

  const [errors, setErrors] = useState({
    firstName: "",
    lastName: "",
    emailId: "",
    phoneNumber: "",
    password: "",
  });
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setsignupData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleLoginInputChange = (e) => {
    setRedText("");
    const { name, value } = e.target;

    setLoginData({
      ...loginData,
      [name]: value,
    });
  };

  const handleLoginSubmit = (e) => {
    e.preventDefault();
    console.log(loginData);
    console.log(JSON.stringify(loginData));
    
    axios
      .post(publicUrl + "/auth/login", {
        emailId: loginData.emailId,
        password: loginData.password,
      })
      .then((res) => {
        // should change status code to 200
        if (res.status === 201) {
          console.log(res.data);
          if (res.data.token) {
            console.log(res.data.userId + " ," + res.data.token);
            callbackID(res.data.userId);
            callbackToken(res.data.token);
            //console.log(res.data.role)
            callbackRole(res.data.role)
            callBackName(`${res.data.lastName.charAt(0)} ${res.data.firstName}`)
            const userBody = {
              id: res.data.userId,
              token: res.data.token,
              role: res.data.role,
              name: `${res.data.lastName.charAt(0)} ${res.data.firstName}`
            }
            dispatch(setUser(userBody))
            toast.success(`Welcome ${res.data.lastName.charAt(0)} ${res.data.firstName}`)
            res.data.role === "RESPONDER"
              ? navigate("/user_dashboard")
              : navigate("/admin/dashboard");
            }
          } else throw new Error(res.status);
        })
        .catch((e) => {
          console.log(e);
          setRedText(e.response.data.message);
        });
    };

  const handleSignUp = (e) => {
    e.preventDefault();

    console.log(signupData);
    const newErrors = {
      firstName: signupData.firstName ? "" : "First Name is required",
      emailId: (emailRegex.test(signupData.emailId)?'':'Enter Valid Email'),
      phoneNumber:
        signupData.phoneNumber.length !== 10
          ? "Mobile Number Length should be 10"
          : "",
      password: checkStrongPassword(signupData.password)
        ? ""
        : "Password should be At least 8 characters with 1 uppercase and lower case letter, 1 special character and a number",
    };
    setErrors(newErrors);
    if (Object.values(newErrors).every((error) => error === "")) {
      // Perform form submission logic
      axios
        .post(publicUrl+"/auth/signUp", {
          firstName: signupData.firstName,
          lastName: signupData.lastName,
          emailId: signupData.emailId,
          password: signupData.password,
          phoneNumber: signupData.phoneNumber,
        })
        .then((res) => {
          if (res.status === 201) {
            //console.log("inside if");
            handleChange();
          } else if (res.status === 200) {
            console.log(res.data);
          } else {
           // console.log("inside else");
            throw new Error(res.status);
          }
        })
        .catch((e) => {
          console.log(e)
          setRedText(e.response.data.message)
        });
    }
  };

  const handleCheckboxChange = () => {
    setChecked(!checked);
  };

  const handleChange = () => {
    if (value === 0) {
      setValue(1);
    } else {
      setValue(0);
    }
    setRedText('')
    console.log(value);
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      console.log("clicked enter");
      document.getElementById("loginButton").click();
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
            padding: 25,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginTop: "100px",
            marginBottom: "60px",
            // border: "0.5px solid black",
            boxSizing: "border-box",
            borderRadius: '16px'
          }}
        >
          <Box width="100%" my={2}>
            {value === 0 && (
              <form
                style={{
                  marginTop: "-20px",
                  width: "100%",
                  textAlign: "center",
                }}
              >
                <Typography variant="h4" gutterBottom>
                  Login
                </Typography>
                <TextField
                  label="Email"
                  name="emailId"
                  value={loginData.emailId}
                  onChange={handleLoginInputChange}
                  variant="outlined"
                  margin="normal"
                  fullWidth
                  required
                />
                <TextField
                  label="Password"
                  type="password"
                  name="password"
                  value={loginData.password}
                  onKeyDown={handleKeyPress}
                  onChange={handleLoginInputChange}
                  variant="outlined"
                  margin="normal"
                  fullWidth
                  required
                />
                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    color: "red",
                  }}
                >
                  <div>{redText}</div>
                </div>
                <FormGroup
                  style={{
                    justifyContent: "space-between",
                    alignItems: "baseline",
                    width: "100%",
                    marginTop: "8px",
                  }}
                >
                  <span>
                    <FormControlLabel
                      control={
                        <Checkbox
                          checked={checked}
                          onChange={handleCheckboxChange}
                        />
                      }
                      label="Remember me"
                    />
                    <Link
                      to="/forgot_password"
                      style={{ color: "blue", marginLeft: "70px" }}
                    >
                      <u>Forgot Password?</u>
                    </Link>
                  </span>
                </FormGroup>
                <Button
                  id="loginButton"
                  type="submit"
                  onClick={handleLoginSubmit}
                  variant="contained"
                  color="primary"
                  style={{ backgroundColor: "rgba(46,182,174,1)", marginTop: "10px" }}
                >
                  Login
                </Button>
                <Typography style={{ marginBottom: "48px", marginTop: "15px",fontSize:'12px' }}>
                  Don't have an Account?{" "}
                  <Button variant="text" onClick={handleChange} style={{color: "blue",fontSize:'12px',fontWeight:'bold'}}>
                    <u>Signup</u>
                  </Button>
                </Typography>
              </form>
            )}
          </Box>

          <Box width="360px" my={-5}>
            {value === 1 && (
              <form style={{ width: "100%", textAlign: "center" }}>
                <Typography
                  variant="h4"
                  gutterBottom
                  style={{ marginBottom: "8px",marginTop:"5px" }}
                >
                  Sign Up
                </Typography>
                <TextField
                  label="Email"
                  variant="outlined"
                  margin="normal"
                  fullWidth
                  name="emailId"
                  type="email"
                  value={signupData.emailId}
                  onChange={handleInputChange}
                  error={!!errors.emailId}
                  helperText={errors.emailId}
                  required
                />
                <TextField
                  label="First Name"
                  variant="outlined"
                  margin="normal"
                  name="firstName"
                  type="text"
                  value={signupData.firstName}
                  onChange={handleInputChange}
                  error={!!errors.firstName}
                  helperText={errors.firstName}
                  fullWidth
                  required
                />
                <TextField
                  label="Last Name"
                  variant="outlined"
                  margin="normal"
                  name="lastName"
                  type="text"
                  value={signupData.lastName}
                  onChange={handleInputChange}
                  fullWidth
                />
                <TextField
                  label="Mobile"
                  variant="outlined"
                  margin="normal"
                  name="phoneNumber"
                  type="text"
                  value={signupData.phoneNumber}
                  onChange={handleInputChange}
                  error={!!errors.phoneNumber}
                  helperText={errors.phoneNumber}
                  fullWidth
                  style={{ marginBottom: "24px" }}
                />
                <FormControl
                  variant="outlined"
                  fullWidth
                  error={!!errors.password}
                  style={{ marginBottom: "20px" }}
                >
                  <InputLabel>Password</InputLabel>
                  <OutlinedInput
                    label="Password"
                    type={signupData.showPassword ? "text" : "password"}
                    name="password"
                    value={signupData.password}
                    onChange={handleInputChange}
                  />
                  <FormHelperText>{errors.password}</FormHelperText>
                </FormControl>
                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    color: "red",
                  }}
                >
                  <div>{redText}</div>
                </div>
                <Button
                  type="submit"
                  variant="contained"
                  onClick={handleSignUp}
                  style={{ backgroundColor: "rgba(46,182,174,1)", marginBottom: "10px" }}
                >
                  Sign Up
                </Button>
                <Typography style={{ marginBottom: "20px",marginTop:'12px',fontSize:'12px' }}>
                  Already have an account?{" "}
                  <Button variant="text" style={{color: "blue",fontSize:'12px',fontWeight:'bold'}} onClick={handleChange}>
                    <u>Login</u>
                  </Button>
                </Typography>
              </form>
            )}
          </Box>
        </Paper>
      </Container>
    </>
  );
};

export default Login;
