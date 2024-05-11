import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  AppBar,
  Box,
  CssBaseline,
  List,
  ListItem,
  ListItemText,
  MenuItem,
  Popover,
  Toolbar,
  Typography,
  Divider,
} from '@mui/material'
import MuiDrawer from '@mui/material/Drawer'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import DashboardIcon from '@mui/icons-material/Dashboard'
import GroupIcon from '@mui/icons-material/Group'
import AddBoxIcon from '@mui/icons-material/AddBox'
import InfoIcon from '@mui/icons-material/Info'
import AccountCircleIcon from '@mui/icons-material/AccountCircle'
import ExitToAppIcon from '@mui/icons-material/ExitToApp'
import HistoryIcon from '@mui/icons-material/History'
import Logo from '../../images/quizol-logo(admin).png'

const Navbar = ({ children }) => {
  const [userType, setUserType] = useState('RESPONDER')

  useEffect(() => {
    const type = sessionStorage.getItem('role')
    setUserType(type)
    setSelectedMenuItem(
      type === 'ADMIN' ? '/admin/dashboard' : '/user_dashboard'
    )
  }, [userType])

  const [isDrawerOpen, setDrawerOpen] = useState(false)
  const [popoverAnchorEl, setPopoverAnchorEl] = useState(null)
  const [selectedMenuItem, setSelectedMenuItem] = useState('')
  const navigate = useNavigate()

  const handleMenuClick = (path) => {
    setSelectedMenuItem(path)
    navigate(path)
    setDrawerOpen(false)
  }

  const handleAdminClick = (event) => {
    setPopoverAnchorEl(event.currentTarget)
  }

  const handleAdminClose = () => {
    navigate(`/profile/${sessionStorage.getItem('id')}`)
    setPopoverAnchorEl(null)
  }

  const handleProfile = () => {
    let id = sessionStorage.getItem('id')
    navigate(`/profile/${id}`)
  }

  const handleDrawerClose = () => {
    setDrawerOpen(false)
  }

  const handleLogoutClick = () => {
    sessionStorage.removeItem('id')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('role')
    sessionStorage.removeItem('name')
    navigate('/')
  }

  const open = Boolean(popoverAnchorEl)
  const id = open ? 'simple-popover' : undefined
  //const userId = sessionStorage.getItem("id");
  // let drawerItems = []
  // setTimeout(() => {
  // const userType = sessionStorage.getItem('role')
  const drawerItems =
    userType === 'RESPONDER'
      ? [
          {
            path: '/user_dashboard',
            icon: <DashboardIcon />,
            text: 'Dashboard',
          },
          {
            path: '/user_history',
            icon: <HistoryIcon />,
            text: 'History',
          },
          {
            path: `/profile/${sessionStorage.getItem('id')}`,
            icon: <AccountCircleIcon />,
            text: 'Profile',
          },
          { path: '/about', icon: <InfoIcon />, text: 'About' },
        ]
      : [
          {
            path: '/admin/dashboard',
            icon: <DashboardIcon />,
            text: 'Dashboard',
          },
          {
            path: '/admin/responders_list',
            icon: <GroupIcon />,
            text: 'Responders',
          },
          {
            path: '/admin/schedule/quiz',
            icon: <AddBoxIcon />,
            text: 'Scheduler',
          },
          {
            path: `/profile/${sessionStorage.getItem('id')}`,
            icon: <AccountCircleIcon />,
            text: 'Profile',
          },
          { path: '/about', icon: <InfoIcon />, text: 'About' },
        ]

  // }, 2000)

  return (
    <>
      <CssBaseline />

      <MuiDrawer
        variant="permanent"
        open={isDrawerOpen}
        sx={{
          '& .MuiDrawer-paper': {
            height: '100%',
            backgroundColor: 'rgba(38,38,38,1)',
            zIndex: (theme) => theme.zIndex.drawer + 1,
          },
        }}
        onClick={handleDrawerClose}
      >
        <div
          style={{
            marginLeft: '-5px',
            marginRight: '5px',
            alignContent: 'center',
          }}
        >
          {/* <img
            src={Logo}
            alt="Quizol Logo"
            style={{ margin: "25px 2px 0px 30px", width: "160px" }}
          /> */}
        </div>
        <List sx={{ marginTop: '30px', color: 'white' }}>
          {drawerItems.map(({ path, icon, text }) => (
            <ListItem key={path}>
              <ListItemButton
                selected={
                  text === 'Scheduler'
                    ? selectedMenuItem === path ||
                      selectedMenuItem === '/admin/schedule/poll'
                    : selectedMenuItem === path
                }
                onClick={() => handleMenuClick(path)}
                sx={{
                  '&:hover': {
                    background:
                      'linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))',
                  },
                  '&.Mui-selected': {
                    background:
                      'linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))',
                  },
                }}
              >
                <ListItemIcon sx={{ color: 'white' }}>{icon}</ListItemIcon>
                <ListItemText primary={text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>

        <Box sx={{ flexGrow: 1 }} />

        <Divider
          sx={{
            backgroundColor: 'rgba(46,182,174,1)',
            width: '169px',
            marginLeft: '20px',
          }}
        />

        <List sx={{ color: 'white', display: 'flex', alignItems: 'center' }}>
          <Typography
            variant="body1"
            style={{
              cursor: 'default',
              fontWeight: 'bold',
              color: 'white',
              width: '60%',
              textAlign: 'center',
            }}
          >
            {sessionStorage.getItem('name')}
          </Typography>
          <ListItem
            onClick={handleLogoutClick}
            disablePadding
            sx={{ cursor: 'pointer', padding: '8px', width: '20%' }}
          >
            <ListItemIcon sx={{ color: 'white' }}>
              <ExitToAppIcon />
            </ListItemIcon>
          </ListItem>
        </List>
      </MuiDrawer>

      <AppBar
        position="fixed"
        sx={{
          backgroundColor: 'rgba(241,243,244,1.000)',
        }}
      >
        <Toolbar>
          {userType === 'RESPONDER' && (
            <>
              <Typography
                variant="h6"
                component="div"
                sx={{ flexGrow: 1, color: 'black' }}
              >
                Home
              </Typography>
              <ListItemButton
                selected={selectedMenuItem === '/user_history'}
                onClick={() => handleMenuClick('/user_history')}
                sx={{
                  borderBottom:
                    selectedMenuItem === '/user_history'
                      ? '2px solid rgba(46,182,174,1)'
                      : 'none',
                  fontStyle: 'Poppins',
                  cursor: 'pointer',
                  marginLeft: '36px',
                  fontWeight: 'bold',
                }}
              >
                <ListItemText
                  primary="History"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    color:
                      selectedMenuItem === '/user_history'
                        ? 'rgba(46,182,174,1)'
                        : '#363940',
                  }}
                />
              </ListItemButton>
              <ListItemButton
                selected={selectedMenuItem === '/about'}
                onClick={() => handleMenuClick('/about')}
                sx={{
                  borderBottom:
                    selectedMenuItem === '/about'
                      ? '2px solid rgba(46,182,174,1)'
                      : 'none',
                  fontStyle: 'Poppins',
                  cursor: 'pointer',
                  marginLeft: '36px',
                  fontWeight: 'bold',
                }}
              >
                <ListItemText
                  primary="About"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    color:
                      selectedMenuItem === '/about'
                        ? 'rgba(46,182,174,1)'
                        : '#363940',
                  }}
                />
              </ListItemButton>
            </>
          )}

          {userType === 'ADMIN' && (
            <>
              <ListItemButton
                selected={selectedMenuItem === '/about'}
                onClick={() => handleMenuClick('/about')}
                sx={{
                  '&:hover': {
                    background:
                      'linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))',
                  },
                  '&.Mui-selected': {
                    background:
                      'linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))',
                  },
                }}
              >
                <ListItemText
                  primary="About"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    color:
                      selectedMenuItem === '/admin/quizzes'
                        ? 'rgba(46,182,174,1)'
                        : '#363940',
                  }}
                />
              </ListItemButton>
              <ListItemButton
                selected={selectedMenuItem === '/admin/schedule/quiz'}
                onClick={() => handleMenuClick('/admin/schedule/quiz')}
                sx={{
                  borderBottom:
                    selectedMenuItem === '/admin/schedule/quiz'
                      ? '2px solid rgba(46,182,174,1)'
                      : 'none',
                  fontStyle: 'Poppins',
                  cursor: 'pointer',
                  marginLeft: '36px',
                  fontWeight: 'bold',
                }}
              >
                <ListItemText
                  primary="Quizzes"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    color:
                      selectedMenuItem === '/admin/schedule/quiz'
                        ? 'rgba(46,182,174,1)'
                        : '#363940',
                  }}
                />
              </ListItemButton>
              <ListItemButton
                selected={selectedMenuItem === '/admin/schedule/poll'}
                onClick={() => handleMenuClick('/admin/schedule/poll')}
                sx={{
                  borderBottom:
                    selectedMenuItem === '/admin/schedule/poll'
                      ? '2px solid rgba(46,182,174,1)'
                      : 'none',
                  fontStyle: 'Poppins',
                  cursor: 'pointer',
                  marginLeft: '36px',
                  // marginRight: "-70px",
                  fontWeight: 'bold',
                  // "&:hover": {
                  //   background:
                  //     "linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))",
                  // },
                  // "&.Mui-selected": {
                  //   background:
                  //     "linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))",
                  // },
                }}
              >
                <ListItemText
                  primary="Polls"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    color:
                      selectedMenuItem === '/admin/schedule/poll'
                        ? 'rgba(46,182,174,1)'
                        : '#363940',
                  }}
                />
              </ListItemButton>
              <ListItemButton
                selected={selectedMenuItem === '/about'}
                onClick={() => handleMenuClick('/about')}
                sx={{
                  borderBottom:
                    selectedMenuItem === '/about'
                      ? '2px solid rgba(46,182,174,1)'
                      : 'none',
                  fontStyle: 'Poppins',
                  cursor: 'pointer',
                  marginLeft: '36px',
                  // marginRight: "-70px",
                  fontWeight: 'bold',

                  // "&:hover": {
                  //   background:
                  //     "linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))",
                  // },
                  // "&.Mui-selected": {
                  //   background:
                  //     "linear-gradient(to right, rgba(255,255,255,0.5), rgba(255,255,255,0))",
                  // },
                }}
              >
                <ListItemText
                  primary="About"
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    color:
                      selectedMenuItem === '/about'
                        ? 'rgba(46,182,174,1)'
                        : '#363940',
                  }}
                />
              </ListItemButton>
            </>
          )}

          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              marginLeft: 'auto',
              marginRight: '16px',
            }}
          >
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                marginLeft: '50px',
              }}
            >
              <AccountCircleIcon
                style={{
                  width: '38px',
                  height: '38px',
                  marginBottom: '-3px',
                  marginRight: '0px',
                  borderRadius: '30px',
                  cursor: 'pointer',
                  color: 'black',
                }}
                onClick={handleAdminClick}
              />

              <Popover
                id={id}
                open={open}
                anchorEl={popoverAnchorEl}
                onClose={handleAdminClose}
                anchorOrigin={{
                  vertical: 'bottom',
                  horizontal: 'left',
                }}
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'left',
                }}
              >
                <MenuItem
                  selected={
                    selectedMenuItem ===
                    `/profile/${sessionStorage.getItem('id')}`
                  }
                  onClick={
                    userType === 'RESPONDER' ? handleProfile : handleAdminClose
                  }
                >
                  Profile
                </MenuItem>
                <MenuItem onClick={handleLogoutClick}>Logout</MenuItem>
              </Popover>
            </div>
          </div>
        </Toolbar>
      </AppBar>

      <div style={{ marginLeft: '200px' }}>{children}</div>
    </>
  )
}

export default Navbar
