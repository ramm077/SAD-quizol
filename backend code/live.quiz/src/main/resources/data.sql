-- Inserting default persons

INSERT INTO users (first_name, last_name, email_id, password, role, phone_number, created_by, created_at, updated_by, updated_at, in_active)
VALUES
  ('Super', 'Admin', 'admin@quizol.com', '$2y$10$EYmeqyaxaTAihYLo9nIlQOtrHatDtyUO1OtrkfJhfW.wrrp6UT4Ze', 'ADMIN', '1234567890', 'admin', '2023-12-31 04:45:00', 'admin','2023-12-31 04:45:00' , false)
  ON CONFLICT (email_id) DO NOTHING;
-- password: Admin@1

---- Insert Quizzes
  INSERT INTO quiz (quiz_id,quiz_name, quiz_type, in_active, created_at, created_by)
  VALUES
  (-1,'General Knowledge Quiz', 'TEST', FALSE, CURRENT_TIMESTAMP, 'Admin'),
  (-2,'Science Quiz', 'TEST', FALSE, CURRENT_TIMESTAMP, 'Admin'),
  (-3,'History Quiz', 'TEST', FALSE, CURRENT_TIMESTAMP, 'Admin'),
  (-4,'General Poll', 'POLL', FALSE, CURRENT_TIMESTAMP, 'Admin')
  ON CONFLICT (quiz_name) DO NOTHING;




-- Insert Questions and Options for General Knowledge Quiz
INSERT INTO question (question_id,question_text, question_type, created_at, created_by, in_active)
VALUES
(-1,'What is the capital of France?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-2,'Who wrote "Romeo and Juliet"?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-3,'What is the largest mammal on Earth?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-4,'Which planet is known as the Red Planet?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-5,'In which year did World War II end?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-6,'What is the chemical symbol for gold?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-7,'Who painted the Mona Lisa?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-8,'What is the powerhouse of the cell?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-9,'Who developed the theory of relativity?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE),
(-10,'What is the currency of Japan?', 'SINGLE', CURRENT_TIMESTAMP, 'Admin', FALSE)
ON CONFLICT (question_id) DO NOTHING;

-- Insert Options for General Knowledge Quiz Questions
INSERT INTO option (option_id,option_text, is_true, created_at, created_by, in_active, question_id)
VALUES
(-1,'Paris', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -1),
(-2,'Berlin', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -1),
(-3,'London', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -1),
(-4,'Madrid', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -1),

(-5,'William Shakespeare', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -2),
(-6,'Jane Austen', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -2),
(-7,'Charles Dickens', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -2),
(-8,'John Steinbeck', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -2),

(-9,'Blue Whale', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -3),
(-10,'Elephant', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -3),
(-11,'Giraffe', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -3),
(-12,'Lion', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -3),

(-13,'Mars', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -4),
(-14,'Venus', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -4),
(-15,'Jupiter', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -4),
(-16,'Saturn', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -4),

(-17,'In 1945', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -5),
(-18,'In 1955', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -5),

(-19,'Au', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -6),
(-20,'Ag', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -6),

(-21,'Leonardo da Vinci', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -7),
(-22,'Vincent van Gogh', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -7),

(-23,'Mitochondria', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -8),
(-24,'Nucleus', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -8),

(-25,'Albert Einstein', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -9),
(-26,'Isaac Newton', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -9),

(-27,'Yen', FALSE, CURRENT_TIMESTAMP, 'Admin', FALSE, -10),
(-28,'Won', TRUE, CURRENT_TIMESTAMP, 'Admin', FALSE, -10)


-- ... Repeat similar inserts for the remaining options and questions in Science and History quizzes
ON CONFLICT (option_id) DO NOTHING;



-- Link Questions to the Quiz with conditional check
INSERT INTO quiz_questions (quiz_id, question_id)
SELECT -1, question_id
FROM question
WHERE question_id IN (-1, -2, -3, -4, -6, -10)
AND NOT EXISTS (
    SELECT 1
    FROM quiz_questions
    WHERE quiz_id = -1 AND question_id = question.question_id
);
INSERT INTO quiz_questions (quiz_id, question_id)
SELECT -2, question_id
FROM question
WHERE question_id IN (-4, -6, -8, -9)
AND NOT EXISTS (
    SELECT 1
    FROM quiz_questions
    WHERE quiz_id = -2 AND question_id = question.question_id
);
INSERT INTO quiz_questions (quiz_id, question_id)
SELECT -3, question_id
FROM question
WHERE question_id IN (-2, -5, -7)
AND NOT EXISTS (
    SELECT 1
    FROM quiz_questions
    WHERE quiz_id = -3 AND question_id = question.question_id
);
INSERT INTO quiz_questions (quiz_id, question_id)
SELECT -4, -10
WHERE NOT EXISTS (
    SELECT 1
    FROM quiz_questions
    WHERE quiz_id = -4 AND question_id = -10
);
--
---- Mapping each question with options
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -1
FROM option
WHERE option_id IN (-1, -2, -3, -4)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -1 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -2
FROM option
WHERE option_id IN (-5, -6, -7, -8)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -2 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -3
FROM option
WHERE option_id IN (-9, -10, -11, -12)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -3 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -4
FROM option
WHERE option_id IN (-13, -14, -15, -16)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -4 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -5
FROM option
WHERE option_id IN (-17, -18)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -5 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -6
FROM option
WHERE option_id IN (-19, -20)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -6 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -7
FROM option
WHERE option_id IN (-21, -22)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -7 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -8
FROM option
WHERE option_id IN (-23, -24)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -8 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -9
FROM option
WHERE option_id IN (-25, -26)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -9 AND option_id = option.option_id
);
--
INSERT INTO question_option_list (option_id, question_id)
SELECT option_id, -10
FROM option
WHERE option_id IN (-27, -28)
AND NOT EXISTS (
    SELECT 1
    FROM question_option_list
    WHERE question_id = -10 AND option_id = option.option_id
);