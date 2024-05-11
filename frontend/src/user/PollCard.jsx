import React from 'react'
import { Card, CardContent, Typography, LinearProgress, Grid } from '@mui/material';


const PollCard = ({poll}) => {



  return (
    <div>
      <Grid item xs={12} sm={6} >
        <Card
          sx={{
            height: '100%',
            width: '90%',
            display: 'flex',
            flexDirection: 'column',
            borderRadius: '8px',
            border: '1.5px solid grey',
            boxShadow: '0 1px 4px rgba(0,0,0,0.4)',
            margin: '0px',
            overflow: 'hidden',
          }}
        >
          <CardContent>
            <Typography variant="h6" sx={{ marginBottom: '10px', fontWeight: 'bold' }}>
              {poll.questionText}
            </Typography>

            <Card
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                borderRadius: '8px',
                border: '1.5px solid grey',
                boxShadow: '0 1px 4px rgba(0,0,0,0.4)',
                overflow: 'hidden',
              }}
            >
              <CardContent>
                {poll.optionList.map((option, i) => (
                  <div key={i} sx={{ display: 'flex', flexDirection: 'column' }} style={{ color: 'black' }}>
                    <Typography>{option.optionText}</Typography>
                    <LinearProgress
                      variant="determinate"
                      value={(option.responseCount/poll.totalResponses)*100}
                      sx={{ marginTop: '5px', flexGrow: 1 }}
                    />
                    <Typography variant="body2" sx={{ textAlign: 'center', marginTop: '5px' }}>
                      {option.responseCount? option.responseCount:0} Votes
                    </Typography>
                  </div>
                ))}
              </CardContent>
            </Card>
          </CardContent>
        </Card>
      </Grid>
    </div>
  )
}

export default PollCard
