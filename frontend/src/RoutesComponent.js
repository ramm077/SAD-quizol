import React from 'react'
import { Routes, Route } from 'react-router-dom';
import Home from './components/Home/Home';
import Login from './components/Welcome/Login';
import NavbarPage from './components/common/NavbarPage';
import AdminDashboard from './admin/AdminDashboard';
import ForgotPassword from './components/Welcome/ForgotPassword';
import RespondersList from './admin/RespondersList';
import EditResponder from './admin/EditResponder';
import IndividualSchedule from './admin/IndividualSchedule';
import CreateQuiz from './admin/Create/CreateQuiz';
import CreatePoll from './admin/Create/CreatePoll';
import UserDashboard from './user/UserDashboard';
import UserHistory from './user/UserHistory';
import Profile from './user/Profile';
import QuizDisplay from './user/QuizDisplay';
import AddResponder from './admin/AddResponder';
import AdminProfile from './admin/AdminProfile';
import Navbar from './components/common/Navbar';
import ScheduleQuiz from './admin/Create/ScheduleQuiz';
import SchedulePoll from './admin/Create/SchedulePoll';
import AboutPage from './components/common/AboutPage'


const RoutesComponent = ({setId, setToken, setRole, setName}) => {
  // console.log('role: '+roleData)
  return (
    <div>
        <Routes>
          <Route path="/login" element={<NavbarPage><Login callbackID={setId} callbackToken={setToken} callbackRole={setRole} callBackName={setName} /></NavbarPage>} />
          <Route path="/" element={<NavbarPage><Home /></NavbarPage>} />
          <Route path="/forgot_password" element={<NavbarPage><ForgotPassword /></NavbarPage>} />
          <Route path="/admin/dashboard" element={<Navbar> <AdminDashboard /> </Navbar>} />
          <Route path="/admin/responders_list" element={<Navbar><RespondersList /></Navbar>} />
          <Route path="/admin/responders_list/edit/:responderId" element={<Navbar><EditResponder /></Navbar>} />
          <Route path='/admin/schedule/:scheduleId' element={<Navbar> <IndividualSchedule /> </Navbar>} />
          <Route path='/admin/schedule/quiz' element={<Navbar> <ScheduleQuiz /> </Navbar>} />
          <Route path='/admin/schedule/poll' element={<Navbar> <SchedulePoll /> </Navbar>} />
          <Route path='/admin/create-quiz' element={<Navbar> <CreateQuiz /> </Navbar>} />
          <Route path='/admin/create-poll' element={<Navbar> <CreatePoll /> </Navbar>} />
          <Route path="/profile/:adminId" element={<Navbar> <AdminProfile/> </Navbar>}/>
          <Route path="/admin/responders_list/add" element={<Navbar> <AddResponder/> </Navbar>} />
          <Route path="/about" element={<Navbar> <AboutPage /> </Navbar>} />
         
          <Route path="/user_dashboard" element={<Navbar> <UserDashboard /></Navbar>}/>
          <Route path="/user_history" element={<Navbar> <UserHistory /></Navbar>}/>
          <Route path="/quiz_display/:schedulerId" element={<NavbarPage> <QuizDisplay/></NavbarPage>}/>
          <Route path="/profile/:userId" element={<Navbar> <Profile/></Navbar>}/>
        </Routes>
      </div>
  )
}

export default RoutesComponent