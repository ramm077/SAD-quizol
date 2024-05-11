import React from "react";
import { AppBar, Toolbar, Typography, CssBaseline } from "@mui/material";

const Footer = () => {
  return (
    <div style={{ display: "flex" }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        style={{ top: "auto", bottom: 0, backgroundColor: "black" }}
      >
        <Toolbar style={{minHeight: "25px"}}>
          <Typography
            variant="body2"
            color="Gray"
            sx={{ flexGrow:1,fontSize: "12px"}}
          >
            Contact us: 123-456-7890 | Email: valuelabs@service-now.com
          </Typography>
          <div>
            <Typography
              variant="body2"
              color="Gray"
              style={{ fontSize: "12px" }}
            >
              CopyRight © 2023 ValueLabs. All rights reserved.
            </Typography>
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
};

export default Footer;
