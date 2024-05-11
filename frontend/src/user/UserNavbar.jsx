import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, Box, Popover, MenuItem } from '@mui/material';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import Logo from '../images/quizol-logo(colors).png';
import { Link, useNavigate } from 'react-router-dom';

const UserNavbar = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const navigate = useNavigate();

  const handleIconClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClosePopover = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    handleClosePopover();
    sessionStorage.removeItem('id')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('role')
    sessionStorage.removeItem('name')
    // Add any additional logout logic here if needed
    navigate('/');
  };
  const handleProfile = () => {
    handleClosePopover();
    // Add any additional logout logic here if needed
    let id=sessionStorage.getItem('id');
    navigate(`/user_profile/${id}`);
  };

  const open = Boolean(anchorEl);
  const id = open ? 'user-popover' : undefined;

  return (
    <>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1, backgroundColor: 'rgba(0, 0, 0, 1)' }}>
        <Toolbar>
          <div style={{ marginLeft: '-5px', marginRight: '5px', alignContent: 'center' }}>
            <img src={Logo} alt="ValueLabs Logo" style={{ width: '200px', marginBottom: '-50px', marginTop: '-50px' }} />
           
          </div>
          <Box sx={{ flexGrow: 1 }} />
          <Typography variant="subtitle1" component="div" sx={{ color: 'white', marginRight: '40px' }}>
            <Link to="/user_dashboard" style={{ textDecoration: 'none', color: 'white' }}>
              Home
            </Link>
          </Typography>
          <Typography variant="subtitle1" component="div" sx={{ color: 'white', marginRight: '40px' }}>
            <Link to="/user_history" style={{ textDecoration: 'none', color: 'white' }}>
              History
            </Link>
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', marginRight: '40px' }}>
            {/* <Box sx={{ position: 'relative' }}>
              <InputBase
                placeholder="Search..."
                inputProps={{ 'aria-label': 'search' }}
                sx={{ border: '1px solid white', height: '30px', borderRadius: '10px', backgroundColor: 'white', padding: '5px' }}
              />
              <SearchIcon sx={{ position: 'absolute', right: '4px', top: '50%', transform: 'translateY(-50%)', color: 'black' }} />
            </Box> */}
            <Box sx={{ marginLeft: '40px' }}>
              <AccountCircleIcon sx={{ color: 'white', fontSize: 35, cursor: 'pointer',marginTop:'5px'  }} onClick={handleIconClick} />
              <Popover
                id={id}
                open={open}
                anchorEl={anchorEl}
                onClose={handleClosePopover}
                anchorOrigin={{
                  vertical: 'bottom',
                  horizontal: 'right',
                }}
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
              >
                <MenuItem onClick={handleProfile}>Profile</MenuItem>
                <MenuItem onClick={handleLogout}>Logout</MenuItem>
              </Popover>
            </Box>
          </Box>
        </Toolbar>
      </AppBar>
    </>
  );
};

export default UserNavbar;