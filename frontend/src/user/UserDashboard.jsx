import React from 'react';
import { Box } from '@mui/material';
import UserCards from './UserCards';


const UserDashboard = () => {
    
    return (
        <>
            <Box sx={{ display: 'flex' }}>
                
                
                    <Box
                        component="main"
                        sx={{ flexGrow: 1, p: 10, transition: 'margin-left 0.3s', }}
                        style={{ marginTop: '10px' }}
                    >
                       
                        <UserCards />
                    </Box>
                    {/* <Box
                        component="main"
                        sx={{ flexGrow: 1, p: 10, transition: 'margin-left 0.3s', marginTop: '-120px' }}
                        
                    >
                        <Typography variant="h4" style={{ fontWeight: 'bold', marginLeft: '30px', marginBottom: '20px', fontSize: '26px' }}>Poll's</Typography>
                        <UserPolls />
                    </Box> */}
                </Box>
            
        </>
    );
};

export default UserDashboard;