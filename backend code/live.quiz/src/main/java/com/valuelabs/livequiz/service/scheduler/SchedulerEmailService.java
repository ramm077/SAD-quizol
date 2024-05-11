package com.valuelabs.livequiz.service.scheduler;
import com.valuelabs.livequiz.model.constants.AppConstants;
import com.valuelabs.livequiz.model.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
@Lazy
@Service
@Slf4j
public class SchedulerEmailService {
    private final JavaMailSender javaMailSender;
    @Lazy
    @Autowired
    public SchedulerEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public List<String> formatTimestampWithDateTime(Timestamp timestamp) {
        log.info("Formatting the timeStamp:"+timestamp);
        List<String> timeList=new ArrayList<>();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        timeList.add(sdfDate.format(timestamp));
        timeList.add(sdfTime.format(timestamp));
        return timeList;
    }
    /**
     * Sends an email using the configured JavaMailSender with MIME capabilities.
     * @param to The email address of the recipient.
     * @param subject The subject of the email.
     * @param htmlBody The HTML content of the email body.
     * @throws MessagingException If an error occurs during the creation or sending of the MimeMessage.
     * @implNote This method constructs a MimeMessage, configures it with recipient, subject, and HTML body,
     *           includes inline images (e.g., logo), and sends the email using the JavaMailSender.
     *           Any MessagingException is caught and printed to the standard error output.
     */
    public void sendEmail(String to, String subject, String htmlBody) {
        log.info("Sending email to User");
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlBody, true);
            // You can include inline images, logos, etc.
            mimeMessageHelper.addInline("logo", new ClassPathResource("static/ValueLabsLogo.jpg"));
            javaMailSender.send(mimeMessage);
            log.debug("Email sent successfully");
        }catch (MessagingException e){
            log.error("Email sending failed:"+e);
            e.printStackTrace();
        }
    }
    /**
     * Sends an email notification to a user scheduled for a quiz.
     *
     * @param user      The user scheduled for the quiz.
     * @param quizName  The name of the scheduled quiz.
     * @param quizType The type of the  scheduled quiz
     * @param startTime The starting time of the quiz.
     * @param endTime   The ending time of the quiz.
     * @implNote Constructs an email message with personalized details for the user's quiz schedule.
     * Includes the user's first name, quiz name, scheduled date, and time.
     * Provides a link to access the quiz and sends the email using the sendEmail method.
     */
    public void sendQuizScheduleEmailToResponder(User user, String quizName, String quizType, Timestamp startTime, Timestamp endTime){
        log.info("Send scheduled Email notification to users");
        List<String> startDateAndTime = formatTimestampWithDateTime(startTime);
        System.out.println(startDateAndTime);
        List<String> endDateAndTime = formatTimestampWithDateTime(endTime);
        String subject = "Quiz Scheduled Notification";
        String message = String.format("<img src='cid:logo' alt='Logo' style='width: 100px; height: 40px;'><br>"+"<p style='font-weight: bold;'>Dear %s,</p><br>"+
                        "<p>We are excited to inform you that you have been scheduled to participate in the upcoming quiz type : %s \"%s\" scheduled on %s at %s till %s at %s. Please be prepared and good luck!</p>"+
                        "<p>Click <a href='%s' style='color: red; text-decoration: underline;'>here</a> to access the quiz.</p>"+
                        "<p style='font-size: 14px;'>Best Regards,<br>The Live Quiz Team</p>",
                user.getFirstName(),quizType,quizName,startDateAndTime.get(0),startDateAndTime.get(1),endDateAndTime.get(0),endDateAndTime.get(1), AppConstants.QUIZ_URL);
        sendEmail(user.getEmailId(), subject, message);
    }
    /**
     * Sends a friendly reminder email to a user scheduled for a quiz.
     *
     * @param user         The user receiving the reminder.
     * @param quizName     The name of the scheduled quiz.
     * @param quizType     The type of the scheduled quiz
     * @param startTime    The starting time of the quiz.
     * @param reminderTime
     * @implNote Constructs an email reminder message with personalized details for the user's scheduled quiz.
     * Includes the user's first name, quiz name, and a wish for success.
     * Provides a link to access the quiz and sends the email using the sendEmail method.
     */

    public void sendQuizReminderEmail(User user, String quizName, String quizType, Timestamp startTime, Integer reminderTime) {
        log.info("Send scheduled Reminder Email notification to users");
        String subject = "Quiz Reminder";
        String message = String.format("<img src='cid:logo' alt='Logo' style='width: 100px; height: 40px;'><br>"+"<p style='font-weight: bold;'>Dear %s,</p><br>" +
                "<p>This is a friendly reminder for the quiz type : %s \"%s\" scheduled in %d minutes. We hope you are ready to showcase your knowledge. Best of luck!</p>" +
                "<p>Click <a href='%s' style='color: red; text-decoration: underline;'>here</a> to access the quiz.</p>" +
                "<p style='font-size: 14px;'>Best Regards,<br>The Live Quiz Team</p>", user.getFirstName(),quizType, quizName,reminderTime, AppConstants.QUIZ_URL);
        sendEmail(user.getEmailId(), subject, message);
    }
    /**
     * Notifies a user about updates to the schedule of a quiz via email.
     *
     * @param user      The user receiving the update notification.
     * @param quizName  The name of the quiz with the updated schedule.
     * @param quizType  The type of the quiz
     * @param startTime The new starting time of the quiz.
     * @param endTime   The new ending time of the quiz.
     * @implNote Formats start and end timestamps to obtain date and time details.
     * Constructs an email message notifying the user about the updated quiz schedule.
     * Includes the user's first name, quiz name, and the revised date and time.
     * Provides a link to access the quiz and sends the email using the sendEmail method.
     */
    public void sendUpdatedScheduleEmail(User user, String quizName, String quizType, Timestamp startTime, Timestamp endTime) {
        log.info("Send updated schedule Email notification to users");
        List<String> startDateAndTime = formatTimestampWithDateTime(startTime);
        System.out.println(startDateAndTime);
        List<String> endDateAndTime = formatTimestampWithDateTime(endTime);
        String subject = "Quiz Schedule Update Notification";
        String message = String.format("<img src='cid:logo' alt='Logo' style='width: 100px; height: 40px;'><br>"+"<p style='font-weight: bold;'>Dear %s,</p><br>" +
                "<p>The schedule for the quiz type : %s \"%s\" has been updated. The quiz is now reScheduled to %s at %s till %s at %s. Please be prepared and good luck!</p>"+
                "<p>Click <a href='%s' style='color: red; text-decoration: underline;'>here</a> to access the quiz.</p>" +
                "<p style='font-size: 14px;'>Best Regards,<br>The Live Quiz Team</p>", user.getFirstName(),quizType,quizName,startDateAndTime.get(0),startDateAndTime.get(1),endDateAndTime.get(0),endDateAndTime.get(1),AppConstants.QUIZ_URL);
        sendEmail(user.getEmailId(),subject,message);
    }
    /**
     * Notifies a user about the cancellation of a scheduled quiz via email.
     *
     * @param user            The user receiving the cancellation notification.
     * @param quizName        The name of the cancelled quiz.
     * @param quizType        The type of the quiz
     * @param reason          The reason for the cancellation.
     * @implNote Constructs an email message informing the user about the cancellation of the scheduled quiz.
     * Includes the user's first name, the name of the cancelled quiz, and the reason for cancellation.
     * Sends the cancellation notification email using the sendEmail method.
     */
    public void sendQuizCancellationEmail(User user, String quizName, String quizType, String reason) {
        log.info("Send deleted schedule Email notification to users");
        String subject = "Quiz Cancellation Notification";
        String message = String.format("<img src='cid:logo' alt='Logo' style='width: 100px; height: 40px;'><br>"+"<p style='font-weight: bold;'>Dear %s,</p><br>" +
                "<p>The scheduled quiz type : %s \"%s\" has been cancelled. The reason for cancellation is: %s.</p>"+
                "<p style='font-size: 14px;'>Best Regards,<br>The Live Quiz Team</p>", user.getFirstName(), quizType,quizName, reason);
        sendEmail(user.getEmailId(),subject,message);
    }
}