import React, { useState } from 'react';
import { Container, Button, Modal, Typography, List, ListItem, ListItemText, Checkbox, TextField, DialogActions, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';


function SelectResponders() {
  const [open, setOpen] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [usersInTable, setUsersInTable] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  const availableUsers = [
    { id: 1, name: 'John Doe' },
    { id: 2, name: 'Jane Smith' },
    // ... other available users
  ];

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const handleSelectUser = (user) => {
    setSelectedUsers((prevSelected) =>
      prevSelected.includes(user)
        ? prevSelected.filter((u) => u !== user)
        : [...prevSelected, user]
    );
  };

  const handleConfirm = () => {
    setUsersInTable(selectedUsers);
    handleClose();
  };

  const filteredUsers = searchTerm
    ? availableUsers.filter((user) =>
        user.name.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : availableUsers;

  return (
    <Container>
      {/* Add Users Button */}
      <Button variant="contained" color="primary" onClick={handleOpen}>
        Add Users
      </Button>

      {/* Users Modal */}
      <Modal 
        open={open} 
        onClose={handleClose}
        sx={{
          position: 'absolute',
          width: 400,
          // backgroundColor: theme.palette.background.paper,
          border: '2px solid #000',
          // boxSh
          padding: 5,
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
        }}
      >
        <div style={{
          position: 'absolute',
          width: 400,
          // backgroundColor: theme.palette.background.paper,
          border: '2px solid #000',
          // boxSh
          padding: 5,
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
        }}>
          <Typography variant="h6">Select Users</Typography>
          <TextField
            label="Search Users"
            fullWidth
            margin="normal"
            value={selectedUsers.map((u) => u.name).join(", ")}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <List>
            {filteredUsers.map((user) => (
              <ListItem key={user.id} button onClick={() => handleSelectUser(user)}>
                <Checkbox checked={selectedUsers.includes(user)} />
                <ListItemText primary={user.name} />
              </ListItem>
            ))}
          </List>
          <DialogActions>
            <Button onClick={handleConfirm} color="primary">
              Confirm
            </Button>
          </DialogActions>
        </div>
      </Modal>

      {/* User Table */}
      {/* ... User Table code ... */}
    </Container>
  );
}

export default SelectResponders;
