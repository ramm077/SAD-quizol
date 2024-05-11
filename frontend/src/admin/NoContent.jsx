import React from 'react';
import { Link } from 'react-router-dom';
//import './NoContantdash.css'; // Import your stylesheet

const NoQuizzesPage = () => {
  return (
    <div className="no-quizzes-container">
      <div className="no-quizzes-content" >
        {/* <h1>No Quizzes Available</h1> */}
        <p>Oops! It looks like there are no Scheduled quizzes available at the moment.</p>
        <p>Create them Now !!</p>
        <Link to='/admin/schedule/quiz'><button className="create-quiz-button">Create Quiz</button></Link>
      </div>
    </div>
  );
};
const NoPollsPage = () => {
    return (
      <div className="no-quizzes-container">
        <div className="no-quizzes-content">
          
          <p>Oops! It looks like there are no polls available at the moment.</p>
          <p>Create Them Now !!</p>
          <Link to='/admin/schedule/poll'><button className="create-quiz-button">Create Polls</button></Link>
        </div>
      </div>
    );
  };

export {NoQuizzesPage,NoPollsPage};