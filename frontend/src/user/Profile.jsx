import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Typography,Container, Button, TextField, Grid } from "@mui/material";
import Image from "../images/bg_image.jpg";
import axios from "axios";
import baseURL from "../BaseUrl";
import { useAxiosInterceptors } from "../useAxiosInterceptors";

const Profile = () => {

  useAxiosInterceptors();

  const { userId } = useParams();
  const navigate = useNavigate();
  

  const [details, setDetails] = useState({
    firstName: "",
    lastName: "",
    emailId: "",
    phoneNumber: "",
  });
  const [errors, setErrors] = useState({ firstName: "", phoneNumber: "" });

  const [isEditing, setIsEditing] = useState(false);
  useEffect(() => {
    fetchData();
  }, [userId]);

  async function fetchData() {
    try {
      let response = await axios.get(
        baseURL+`/user?userId=${userId}`
      );
      console.log(response);
      if (response.status === 200) {
        setDetails(response.data);
      } else {
        alert("No Data Found");
      }
    } catch (error) {
      console.log(error);
    }
  }
  
  const handleEditClick = () => {
    setIsEditing((prevIsEditing) => !prevIsEditing);
  };
  const handleChange = (e) => {
    setDetails({ ...details, [e.target.name]: e.target.value });
  };
  const handleSaveClick = async (e) => {
    e.preventDefault();
    const newErrors = {
      firstName: details.firstName ? "" : "First Name is required",
      phoneNumber:
        details.phoneNumber.length !== 10
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
          baseURL+`/user/update/${userId}`,
          details
        );
        console.log(response.data);

        setIsEditing(false);
        if (response) {
          navigate("/user_dashboard", { replace: true });
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
        <form style={{
            padding: 25,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginTop: "100px",
            marginBottom: "60px",
            // border: "0.5px solid grey",
            borderRadius:'4px',
            backgroundColor:'white',
            boxSizing: "border-box"}}
          >
              <Typography variant="h6"style={{textAlign:'center',marginBottom:'20px',marginTop:'0px',fontWeight:'bold'}}>
                Edit Details
              </Typography>
          <Grid container spacing={2} style={{ alignItems: "center" }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="First Name"
                name="firstName"
                value={details.firstName}
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
                value={details.lastName}
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
                value={details.phoneNumber}
                onChange={handleChange}
                error={!!errors.phoneNumber}
                helperText={errors.phoneNumber}
                required
                disabled={!isEditing}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                name="email"
                value={details.emailId}
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
              backgroundColor: "teal",
              marginTop: "10px",
              marginLeft: "5opx",
              color: "white",
            }}
          >
            {isEditing ? "Save Changes" : "Edit"}
          </Button>
        </form>
      </Container>
    </>
  );
};
export default Profile;
