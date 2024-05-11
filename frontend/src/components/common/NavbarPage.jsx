import React from 'react'
import { AppBar, Toolbar, Typography, Button, CssBaseline } from '@mui/material'
import { Link } from 'react-router-dom'
import Logo from '../../images/quizol-logo(colors).png' // Adjust the image path
import Footer from './Footer'

const NavbarPage = ({ children }) => {
  return (
    <div>
      <CssBaseline />
      <AppBar
        position="fixed"
        style={{ backgroundColor: '#000', height: '60px' }}
      >
        <Toolbar
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <div
            style={{
              marginLeft: '-5px',
              marginRight: '5px',
              alignContent: 'center',
            }}
          >
            <img src={Logo} alt="ValueLabs Logo" style={{ width: '200px' }} />
          </div>
        </Toolbar>
      </AppBar>
      {children}
      {/* < Footer/> */}
    </div>
  )
}

export default NavbarPage
