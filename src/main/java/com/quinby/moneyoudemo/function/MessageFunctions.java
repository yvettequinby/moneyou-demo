package com.quinby.moneyoudemo.function;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.quinby.moneyoudemo.dto.MessageDTO;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MessageFunctions {

    private static final Logger log = Logger.getLogger(MessageFunctions.class);

    private static final String SES_SENDER = "mood.msg.bot@gmail.com";

    private static final String MOOD_IMAGE_PATH = "https://s3.eu-central-1.amazonaws.com/moneyou-demo/%s.png";

    private static final String EMAIL_BODY = "<p>API received a message and the sentiment is: </p><img src='%s' />";

    private final AWSCredentialsProvider awsCredentials = DefaultAWSCredentialsProviderChain.getInstance();

    private final AmazonSNS snsClient = AmazonSNSClient.builder()
            .withRegion(Regions.EU_WEST_1)
            .withCredentials(awsCredentials)
            .build();

    private final AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
            .withRegion(Regions.EU_WEST_1)
            .build();

    private final AmazonComprehend comprehendClient = AmazonComprehendClientBuilder.standard()
            .withCredentials(awsCredentials)
            .withRegion(Regions.EU_CENTRAL_1)
            .build();


    public static void main(String[] args) {
        MessageFunctions messageFunctions = new MessageFunctions();
        messageFunctions.handleMessage(new MessageDTO("+31636431212", "yvette.quinby@protonmail.com","happy happy joy joy"));
        messageFunctions.handleMessage(new MessageDTO("+31636431212", "yvette.quinby@protonmail.com", "boo hiss boo hiss"));
        messageFunctions.handleMessage(new MessageDTO("+31636431212", "yvette.quinby@protonmail.com", "the hat was red"));
    }


    public void handleMessage(MessageDTO message) {
        log.info("SendMessage() invoked.");
        DetectSentimentResult sentimentResult = detectMessageSentiment(message);
        sendSms(sentimentResult.getSentiment(), message.getRecipientSms());
        sendEmail(sentimentResult.getSentiment(), message.getRecipientEmail());
        log.info("Finished SendMessage().");
    }


    private DetectSentimentResult detectMessageSentiment(MessageDTO message) {
        log.info("Calling DetectSentiment");
        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest()
                .withText(message.getText())
                .withLanguageCode("en");
        return comprehendClient.detectSentiment(detectSentimentRequest);
    }


    private void sendSms(String sentiment, String recipient) {
        try {
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue() .withStringValue("MoodMsgBot").withDataType("String"));
            smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue().withStringValue("0.15").withDataType("Number"));
            smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue("Promotional").withDataType("String"));

            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(sentiment)
                    .withPhoneNumber(recipient)
                    .withMessageAttributes(smsAttributes));
            log.info("Sent SMS msg " + result.getMessageId() + " to " + recipient);
        } catch(Exception e) {
            log.error("Error sending SMS. ", e);
        }

    }



    private void sendEmail(String sentiment, String recipient) {
        try {
            String imagePath = String.format(MOOD_IMAGE_PATH, sentiment.toLowerCase());
            String htmlBody = String.format(EMAIL_BODY, imagePath);
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(recipient))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(htmlBody)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData("Mood Message Bot")))
                    .withSource(SES_SENDER);
            sesClient.sendEmail(request);
            log.info("Email sent.");
        } catch (Exception e) {
            log.error("Error sending email. ", e);
        }
    }
}
