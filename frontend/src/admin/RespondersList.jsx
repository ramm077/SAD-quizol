import React, { useState, useEffect } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TextField,
  TableRow,
  Paper,
  Button,
  Box,
  Typography,
} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import TablePagination from "@mui/material/TablePagination";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from "@mui/material";
import axios from "axios";
import { Link } from "react-router-dom";
import baseURL from "../BaseUrl";
import { useAxiosInterceptors } from "../useAxiosInterceptors";
import { toast } from "react-toastify";

const ResponderList = () => {

  useAxiosInterceptors();

  const [open, setOpen] = useState(false);
  const [editedUser, setEditedUser] = useState({});
  const [searchQuery, setSearchQuery] = useState("");
  const [originalData, setOriginalData] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [page, changePage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [deleteConfirmation, setDeleteConfirmation] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  const fetchData = async () => {
    try {
      const response = await axios.get(baseURL + "/user/responders");
      setOriginalData(response.data);
      setFilteredUsers(response.data);
    } catch (error) {
      console.log("Error fetching data:", error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleEditClick = (user) => {
    setEditedUser(user);
    setOpen(true);
  };

  const handleDeleteClick = (user) => {
    setEditedUser(user);
    setDeleteConfirmation(true);
  };

  const handleDelete = () => {
    
    axios.delete(baseURL + `/user/delete?userId=${editedUser.userId}`)
    .then(res => {
      if(res.status === 200) toast.success('User Deleted!')
      fetchData()
    })
    .catch(e => console.log(e))

    setEditedUser({});
    setDeleteConfirmation(false);
    setOpen(false);
  };

  const handleClose = () => {
    setEditedUser({});
    setDeleteConfirmation(false);
    setOpen(false);
  };

  const handleSaveChanges = async () => {
    await axios.put(baseURL + `/user/update?userId=${editedUser.userId}`, {
      firstName: editedUser.firstName,
      lastName: editedUser.lastName,
      phoneNumber: editedUser.phoneNumber,
    })
    .then(res => {
      if(res.status === 200) toast.success('User Updated!')
      fetchData()
    })
    .catch(e => console.log(e))
      
    
    setOpen(false);
  };

  const handleSearch = () => {
    const filteredData = originalData.filter(
      (user) =>
        user.firstName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        user.lastName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        user.emailId.toLowerCase().includes(searchQuery.toLowerCase())
    );

    setFilteredUsers(filteredData);
  };

  return (
    <>
      <Box
        style={{
          display: "flex",
          justifyContent: "flex-end",
          alignItems: "center",
          marginTop: "75px",
          marginBottom: "30px", // Added marginBottom to match the design
        }}
      >
        {/* Search Bar */}
        <TextField
          label="Search..."
          variant="outlined"
          value={searchQuery}
          onChange={(e) => {
            setSearchQuery(e.target.value);
            handleSearch(); // Trigger search on each keystroke
          }}
          size="small"
          margin="dense"
          sx={{ marginRight: "10px" }}
        />

        {/* Create Responder Button */}
        <Link to="/admin/responders_list/add">
          <Button
            variant="contained"
            size="medium"
            sx={{ marginRight: "60px", backgroundColor: "rgba(46,182,174,1)","&:hover":{backgroundColor: "rgba(46,182,174,1)"} }}
          >
            Create
          </Button>
        </Link>
      </Box>
      <div style={{ textAlign: "center" }}>
        <Paper
          sx={{
            width: "90%",
            marginLeft: "5%",
            marginTop: "-20px",
            marginBottom: "40px",
          }}
        >
          <TableContainer>
            <Table stickyHeader>
              <TableHead>
                <TableRow>
                  <TableCell
                    style={{
                      textAlign: "center",
                      backgroundColor: "black",
                      color: "white",
                    }}
                  >
                    ID
                  </TableCell>
                  <TableCell
                    style={{
                      textAlign: "center",
                      backgroundColor: "black",
                      color: "white",
                    }}
                  >
                    First Name
                  </TableCell>
                  <TableCell
                    style={{
                      textAlign: "center",
                      backgroundColor: "black",
                      color: "white",
                    }}
                  >
                    Last Name
                  </TableCell>
                  <TableCell
                    style={{
                      textAlign: "center",
                      backgroundColor: "black",
                      color: "white",
                    }}
                  >
                    Email
                  </TableCell>
                  <TableCell
                    style={{
                      textAlign: "center",
                      backgroundColor: "black",
                      color: "white",
                    }}
                  >
                    Phone Number
                  </TableCell>
                  <TableCell
                    style={{
                      textAlign: "center",
                      backgroundColor: "black",
                      color: "white",
                    }}
                  >
                    Action
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredUsers &&
                  filteredUsers
                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                    .map((user, index) => (
                      <TableRow key={index}>
                        <TableCell style={{ textAlign: "center" }}>
                          {index + 1}
                        </TableCell>
                        <TableCell style={{ textAlign: "center" }}>
                          {user.firstName}
                        </TableCell>
                        <TableCell style={{ textAlign: "center" }}>
                          {user.lastName}
                        </TableCell>
                        <TableCell style={{ textAlign: "center" }}>
                          {user.emailId}
                        </TableCell>
                        <TableCell style={{ textAlign: "center" }}>
                          {user.phoneNumber}
                        </TableCell>
                        <TableCell style={{ textAlign: "center" }}>
                          <IconButton onClick={() => handleEditClick(user)}>
                            <EditIcon />
                          </IconButton>
                          <IconButton onClick={() => handleDeleteClick(user)}>
                            <DeleteIcon />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
              </TableBody>
            </Table>
          </TableContainer>
          <Dialog open={open} onClose={handleClose}>
            <DialogTitle style={{ fontWeight: "bold", marginLeft: "10px" }}>
              Edit User
            </DialogTitle>
            <DialogContent>
              <TextField
                label="First Name"
                fullWidth
                value={editedUser.firstName || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser, firstName: e.target.value })
                }
                style={{ margin: "10px", width: "95%" }}
              />
              <TextField
                label="Last Name"
                fullWidth
                value={editedUser.lastName || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser, lastName: e.target.value })
                }
                style={{ margin: "10px", width: "95%" }}
              />
              <br />
              <TextField
                label="Email"
                fullWidth
                disabled
                name="emailId"
                value={editedUser.emailId || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser, emailId: e.target.value })
                }
                style={{ margin: "10px", width: "95%" }}
              />
              <TextField
                label="Phone Number"
                fullWidth
                value={editedUser.phoneNumber || ""}
                onChange={(e) =>
                  setEditedUser({ ...editedUser, phoneNumber: e.target.value })
                }
                style={{ margin: "10px", width: "95%" }}
              />
            </DialogContent>
            <DialogActions style={{ marginBottom: "10px", marginTop: "-20px" }}>
              <Button
                variant="contained"
                color="inherit"
                onClick={handleClose}
                style={{ marginRight: "6px" }}
              >
                Cancel
              </Button>
              <Button
                variant="contained"
                style={{
                  backgroundColor: "rgba(46,182,174,1)",
                  color: "black",
                  marginRight: "30px",
                }}
                onClick={handleSaveChanges}
              >
                Save Changes
              </Button>
            </DialogActions>
          </Dialog>
          <Dialog open={deleteConfirmation} onClose={handleClose}>
            <DialogContent>
              <Typography style={{ fontWeight: "bold"}}>
                Are you sure you want to delete {editedUser.firstName}{" "}
                {editedUser.lastName}?
              </Typography>
            </DialogContent>
            <DialogActions style={{ marginBottom: "10px", marginTop: "-10px" }}>
              <Button
                variant="contained"
                color="inherit"
                onClick={handleClose}
                style={{ marginRight: "6px" }}
              >
                Cancel
              </Button>
              <Button
                variant="contained"
                onClick={handleDelete}
                style={{
                  marginRight: "30px",
                  color: "black",
                  backgroundColor: "rgba(46,182,174,1)",
                }}
              >
                Delete
              </Button>
            </DialogActions>
          </Dialog>
          {successMessage && (
            <Typography
              sx={{
                marginLeft: "5%",
                marginTop: "10px",
                color: "green",
              }}
            >
              {successMessage}
            </Typography>
          )}

          <TablePagination
            rowsPerPageOptions={[5, 8]}
            page={page}
            rowsPerPage={rowsPerPage}
            component="div"
            onPageChange={(e, newPage) => changePage(newPage)}
            count={filteredUsers.length}
            onRowsPerPageChange={(e) => {
              setRowsPerPage(+e.target.value);
              changePage(0);
            }}
          />
        </Paper>
      </div>
    </>
  );
};

export default ResponderList;
