import React, { useEffect, useState } from 'react';
import {  useNavigate } from 'react-router-dom';
import './App.css';
import RoutesComponent from './RoutesComponent';
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css';


function App() {

  const navigate = useNavigate();
  const [id, setId] = useState('')
  const [token, setToken] = useState('')
  const [role, setRole] = useState('')
  const [name, setName] = useState('')

  useEffect(() => {

    if((window.sessionStorage.getItem('id')===null || window.sessionStorage.getItem('id')==='') && id!==null) window.sessionStorage.setItem('id', id);
    else setId(window.sessionStorage.getItem('id'))

    if((window.sessionStorage.getItem('token')===null || window.sessionStorage.getItem('token')==='') && token!==null) window.sessionStorage.setItem('token', token);
    else setToken(window.sessionStorage.getItem('token'));

    if((window.sessionStorage.getItem('role')===null || window.sessionStorage.getItem('role')==='') && role!==null) window.sessionStorage.setItem('role', role);
    else setRole(window.sessionStorage.getItem('role'));

    if((window.sessionStorage.getItem('name')===null || window.sessionStorage.getItem('name')==='') && name!==null) window.sessionStorage.setItem('name', name);
    else setName(window.sessionStorage.getItem('name'));


    if(!sessionStorage.token) navigate('/');

    
  }, [id, token, role, name])

  console.log('role: '+role)
  // props = {
  //   setId: setId,
    
  // }

  return (
    <div>
      <RoutesComponent setId={setId} setToken={setToken} setRole={setRole} setName={setName} />
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        // transition: Bounce,
      />
    </div>
  );
}

export default App;