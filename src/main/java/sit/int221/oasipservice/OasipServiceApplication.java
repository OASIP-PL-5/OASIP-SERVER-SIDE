package sit.int221.oasipservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class OasipServiceApplication {

//    @Autowired
//    private EmailSenderService senderService;
    public static void main(String[] args) {
        SpringApplication.run(OasipServiceApplication.class, args);
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void sendMail(){
//        senderService.sendEmail("studentForTest123@gmail.com","Subject","Body");
//    }


}
