import React, { useState } from "react";
import { AppBar, Toolbar, Typography, Box } from "@mui/material";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import Logo from "../images/quizol-logo(colors).png";

const Header = () => {
  const [anchorEl, setAnchorEl] = useState(null);

  const handleIconClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  return (
    <>
      <AppBar
        position="fixed"
        sx={{
          zIndex: (theme) => theme.zIndex.drawer + 1,
          backgroundColor: "rgba(0, 0, 0, 1)",
        }}
      >
        <Toolbar>
          <div
            style={{
              marginLeft: "-5px",
              marginRight: "5px",
              alignContent: "center",
            }}
          >
            <img
              src={Logo}
              alt="ValueLabs Logo"
              style={{
                width: "200px",
                marginBottom: "-50px",
                marginTop: "-50px",
              }}
            />
            <Typography
              variant="subtitle2"
              style={{ fontSize: "10px", color: "white", marginLeft: "37px" }}
            >
              Doing the right thing. Always.
            </Typography>
          </div>
          <Box sx={{ flexGrow: 1 }} />
          <Box>
            <AccountCircleIcon
              sx={{ color: "white", fontSize: 35 }}
              onClick={handleIconClick}
              className="account circle"
            />
          </Box>
        </Toolbar>
      </AppBar>
    </>
  );
};

export default Header;
