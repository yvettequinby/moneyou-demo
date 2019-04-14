# Moneyou Coding Assignment

I decided to go with the "creative" option and build a quick demo of how to use the AWS Comprehend service. 

I combined this with some functionality for sending SMS messages, sending email messages, and using S3 storage.

The demo is coded as a simple Java application and is deployed as an AWS Lambda function, accessable as a REST API.

If you'd like to see an example of AWS Lambda functions using RDS, I also have this project (but it's not currently deployed in my AWS environment): https://github.com/yvettequinby/aws-java-lambda


## Functionality

The demo has on single _post_ REST API endpoint, which accepts a JSON object in the body, such as:

```
{
  "recipientSms": "+31636431212",
  "recipientEmail": "yvette.quinby@protonmail.com",
  "text": "I hate the rain. All the water is bad."
}
```

When a valid message is sent to the REST API endpoint, the following occurs:

1. The text of the message is sent to the AWS Comrehend service to determine the sentiment of the message. The sentiment may be positive, negative or neutral.
2. The sentiment is sent as an SMS to the recipientSms.
3. An email, with a link to an emoji reflecting the sentiment, is sent to the recipientEmail.


## AWS Usage

* Comprehend
  * The Comprehend service is used to determine the sentiment of the message. 
  * The service is accessed via the Java SDK, from within the function.
* SNS
  * The SNS service is used to send SMS messages to the recipient.
  * The service is accessed via the Java SDK, from within the function. 
  * A price limiter is applied to the service. SMSes will not be sent once the price limit is reached.
* SES
  * The SES service is used to send email messages to the recipient.
  * The service is accessed via the Java SDK, from within the function. 
  * The service can only send emails to verified email recipients.
* S3
  * The emoji images, linked to from the emails, are stored in an AWS S3 bucket
* Lambda
  * The Java application is deployed an an AWS Lambda function
* API Gateway
  * The API Gateway service is used to define a REST API that calls the Lambda function


## How to Use

Using Postman, you may access the API:

POST https://ay0tymdoke.execute-api.eu-west-1.amazonaws.com/MoodMsgBot

After a successful post, an SMS and email should be sent to the specified recipients, informing them of the sentiment of the message.

### Example Positive Message (JSON Body)
```
{
  "recipientSms": "+31636431212",
  "recipientEmail": "yvette.quinby@protonmail.com",
  "text": "I love the beach. Swimming is great."
}
```

### Example Negative Message (JSON Body)
```
{
  "recipientSms": "+31636431212",
  "recipientEmail": "yvette.quinby@protonmail.com",
  "text": "I hate the rain. All the water is bad."
}
```

### Example Neutral Message (JSON Body)
```
{
  "recipientSms": "+31636431212",
  "recipientEmail": "yvette.quinby@protonmail.com",
  "text": "The car is red. Cars go fast."
}
```
