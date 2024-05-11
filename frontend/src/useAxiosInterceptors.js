import axios from 'axios';
import { useNavigate } from 'react-router';
import { toast } from 'react-toastify';

export const useAxiosInterceptors = () => {

  const navigate = useNavigate()

  // Request interceptor
  axios.interceptors.request.use(
    (config) => {
      const token = sessionStorage.getItem('token');
      // console.log('Token:', token);
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
  // Response interceptor
  axios.interceptors.response.use(
    (response) => {
      // Do something with the response data
      return response;
    },
    (error) => {
      // Do something with the response error
      // console.log(error)
      if (error.response.status === 403) {
        sessionStorage.removeItem('id')
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('role')
        sessionStorage.removeItem('name')
        toast.info('session has expired!', {
          toastId: 'session-expired'
        })
        navigate('/')
      }
      return Promise.reject(error);
    }
  );

}