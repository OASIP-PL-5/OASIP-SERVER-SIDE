package sit.int221.oasipservice.services;

import sit.int221.oasipservice.dtos.EmailDetail;

public interface EmailService {
    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetail details);

    // Method
    // To send an email with attachment
//    String sendMailWithAttachment(EmailDetail details);
}
