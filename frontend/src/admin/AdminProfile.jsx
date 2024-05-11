import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import { Container, Button, TextField, Grid, Typography, Paper } from "@mui/material";
import axios from "axios";
import baseURL from "../BaseUrl";
import Image from "../images/bg_image.jpg"
import NavbarPage from "../components/common/NavbarPage"
import { useAxiosInterceptors } from "../useAxiosInterceptors";
import { useSelector } from "react-redux";

const AdminProfile = () => {

  useAxiosInterceptors();

  const { adminId } = useParams();
  //const userId = useSelector(state => state.user.id)
  //const adminId = 1;
  const navigate = useNavigate();
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  //   const strongPasswordRegex =
  //     /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
  const [adminDetails, setAdminDetails] = useState({
    firstName: "",
    lastName: "",
    emailId: "",
    phoneNumber: "",
  });
  const [errors, setErrors] = useState({
    firstName: "",
    emailId: "",
    phoneNumber: "",
  });
  //   const checkStrongPassword = (password) => {
  //     // At least 8 characters, 1 uppercase, 1 special character, 1 number
  //     const strongPasswordRegex =
  //       /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
  //     return password.length >= 8 && strongPasswordRegex.test(password);
  //   };
  const [isEditing, setIsEditing] = useState(false);
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(baseURL + `/user?userId=${adminId}`);
        //console.log(response.data);
        setAdminDetails(response.data);
      } catch (error) {
        console.log("Error fetching data:", error);
      }
    };

    fetchData();
  }, [adminId]);
  const handleEditClick = () => {
    setIsEditing((prevIsEditing) => !prevIsEditing);
  };
  const handleChange = (e) => {
    setAdminDetails({ ...adminDetails, [e.target.name]: e.target.value });
  };
  console.log(adminDetails);
  const handleSaveClick = async (e) => {
    e.preventDefault();
    const newErrors = {
      firstName: adminDetails.firstName ? "" : "First Name is required",
      emailId: emailRegex.test(adminDetails.emailId) ? "" : "Enter Valid Email",
      phoneNumber:
        adminDetails.phoneNumber.length !== 10
          ? "Mobile Number Length should be 10"
          : "",
    };
    setErrors(newErrors);
    console.log(errors);
    let validForm = false;
    if (Object.values(newErrors).every((error) => error === "")) {
      validForm = true;
    }
    if (validForm) {
      try {
        const response = await axios.put(
          baseURL + `/user/update?userId=${adminId}`,
          {
            firstName: adminDetails.firstName,
            lastName: adminDetails.lastName,
            phoneNumber: adminDetails.phoneNumber,
          }
        );

        //console.log(response.data);
        setIsEditing(false);
        if (response.status === 200) {
          navigate(`/profile/${sessionStorage.getItem('id')}`);
        }
      } catch (error) {
        console.log(error);
      }
    } else {
      window.alert("Form Not Valid");
    }
  };

  return (
    <>
      <Container
        component="main"
        maxWidth="xs"
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "40vh",
          marginTop: "170px",
          marginBottom:'0px'
        }}
      >
        <style>
          {`
          body {
            
            background: url(${Image}) center/cover no-repeat fixed; // Set the background image for the body
            overflow: hidden;
          }
        `}
        </style>
        {/* <Paper elevation={8}> */}
        <Paper 
          elevation={8}
          style={{
            padding: 25,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginBottom: "60px",
            // border: "0.5px solid grey",
            borderRadius: '16px',
            backgroundColor: 'white',
            boxSizing: "border-box",
          }}
        >
          <Typography variant="h6" style={{ textAlign: 'center', marginBottom: '20px', marginTop: '0px', fontWeight: 'bold' }}>
            Edit Details
          </Typography>
          <Grid container spacing={2}>

            <Grid item xs={12} sm={6}>

              <TextField
                fullWidth
                label="First Name"
                name="firstName"
                value={adminDetails.firstName}
                error={!!errors.firstName}
                helperText={errors.firstName}
                onChange={handleChange}
                disabled={!isEditing}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Last Name"
                name="lastName"
                value={adminDetails.lastName}
                error={!!errors.lastName}
                helperText={errors.lastName}
                onChange={handleChange}
                disabled={!isEditing}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Mobile"
                name="phoneNumber"
                value={adminDetails.phoneNumber}
                onChange={handleChange}
                error={!!errors.phoneNumber}
                helperText={errors.phoneNumber}
                required
                disabled={!isEditing}
              />
            </Grid>
            {/* <Grid item xs={12}>
              <TextField
                fullWidth
                label="Password"
                type="password"
                name="password"
                value={adminDetails.password}
                onChange={handleChange}
                error={!!errors.password}
                helperText={errors.password}
                disabled={!isEditing}
                required
              />
            </Grid> */}
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                name="emailId"
                value={adminDetails.emailId}
                onChange={handleChange}
                error={!!errors.emailId}
                helperText={errors.emailId}
                disabled={true}
                required
              />
            </Grid>
          </Grid>
          <Button
            onClick={isEditing ? handleSaveClick : handleEditClick}
            style={{
              backgroundColor: 'rgba(46,182,174,1)',
              marginTop: "20px",
              color: "white",
            }}
          >
            {isEditing ? "Save Changes" : "Edit"}
          </Button>
        </Paper>
      </Container>
    </>
  );
};
export default AdminProfile;
