import React from 'react';
import { Popover, Paper, Typography, Box, Button } from '@mui/material';

const QuizPopover = ({ onClose, quizData }) => {
  if (!quizData) {
    return null;
  }

  const handleClose = () => {
    onClose();
  };

  const centerPosition = {
    top: window.innerHeight / 2,
    left: window.innerWidth / 2,
  };

  return (
    <Popover
      open={Boolean(quizData)}
      anchorReference="anchorPosition"
      anchorPosition={centerPosition}
      transformOrigin={{
        vertical: 'center',
        horizontal: 'center',
      }}
      onClose={handleClose}
    >
      <Paper style={{ padding: '20px', position: 'relative', minWidth: '300px' }}>
        <Typography variant="h6">{quizData.quizName}</Typography>
        {quizData.questions.map((question, index) => (
          <Box key={index} mt={2}>
            <Typography variant="body1">
              {index + 1}. {question.question}
            </Typography>
            <Typography variant="body2" color={question.userAnswer === question.correctAnswer ? 'green' : 'red'}>
              Your Answer: {question.userAnswer}
            </Typography>
            <Typography variant="body2" color="textSecondary">
              Correct Answer: {question.correctAnswer}
            </Typography>
          </Box>
        ))}
        <Button
          variant="outlined"
          color="primary"
          style={{ position: 'absolute', top: '8px', right: '8px' }}
          onClick={handleClose}
        >
          &#10006; {/* Close symbol */}
        </Button>
      </Paper>
    </Popover>
  );
};

export default QuizPopover;