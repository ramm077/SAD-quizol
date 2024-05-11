import React, { useEffect, useState } from "react";
//import ApiServices from "../../Services/ApiServices";
import { useParams, useNavigate } from "react-router-dom";
import { Container, Paper, TextField, Button, Typography } from "@mui/material";
import Image from "../images/bg_image.jpg";
import axios from "axios";
import baseURL from "../BaseUrl";
import { useAxiosInterceptors } from "../useAxiosInterceptors";

const EditResponder = () => {

  useAxiosInterceptors();

  const navigate = useNavigate();
  const { responderId } = useParams();
  const [user, setUser] = useState([]);
  const [responder, setResponder] = useState({
    firstName: "",
    lastName: "",
    phoneNumber: "",
  });
  const [errors, setErrors] = useState({ firstName: "", phoneNumber: "" });
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(
          baseURL+`/responder/${responderId}`
        );
        //console.log(response.data);
        setResponder(response.data);
      } catch (error) {
        console.log("Error fetching data:", error);
      }
    };

    fetchData();
  }, [responderId]);

  // useEffect(()=>
  // {
  //   fetchData();
  // },[responderId]);

  // async function fetchData(){
  //   try{
  //     let response=await fetch();

  //     setResponder(response.data);
  //     console.log('Response',response.data)
  //     console.log('Form Data',responder)

  //   }catch(error){
  //    console.log(error);
  //     //navigate(`/admin/responders-list/edit/${responderId}`,{replace:false})
  //   }
  // }
  const handleChange = (e) => {
    setResponder({ ...responder, [e.target.name]: e.target.value });
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {
      firstName: responder.firstName ? "" : "First Name is required",

      phoneNumber:
        responder.phoneNumber.length !== 10
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
        const response = await axios.put(
          baseURL+`/user/update/${responderId}`,
          responder
        );
        console.log(response);
        if (response.status === 200) {
          console.log("200");
        }
        if (response.status === 400) {
          console.log("400");
        }
      } catch (error) {
        //  console.log(error);
      }
    } else {
      alert("Edit impossible");
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
      <div
        style={{
          height: "100vh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Container maxWidth="sm">
          <Paper
            elevation={3}
            style={{
              padding: "20px",
              border: "1px solid #ccc",
              borderRadius: "8px",
            }}
          >
            <Typography variant="h5" align="center" gutterBottom>
              Edit Responder
            </Typography>
            <form onSubmit={handleSubmit}>
              <TextField
                label="First Name"
                variant="outlined"
                margin="normal"
                name="firstName"
                value={responder.firstName}
                onChange={handleChange}
                fullWidth
                required
              />
              <TextField
                label="Last Name"
                variant="outlined"
                margin="normal"
                name="lastName"
                value={responder.lastName}
                onChange={handleChange}
                fullWidth
                required
              />
              <TextField
                label="Mobile"
                variant="outlined"
                margin="normal"
                name="phoneNumber"
                value={responder.phoneNumber}
                onChange={handleChange}
                fullWidth
                required
              />
              {/* <TextField
              label="Email"
              variant="outlined"
              margin="normal"
              fullWidth
              required
              type="email"
            /> */}
              <Button
                type="submit"
                variant="contained"
                color="primary"
                style={{ backgroundColor: "#33cccc", marginTop: "20px" }}
              >
                Save Changes
              </Button>
            </form>
          </Paper>
        </Container>
      </div>
    </>
  );
};
export default EditResponder;
