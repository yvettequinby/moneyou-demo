package com.quinby.moneyoudemo.dto;

public class MessageDTO {

    private String recipientSms;
    private String recipientEmail;
    private String text;

    public MessageDTO() {
    }

    public MessageDTO(String recipientSms, String recipientEmail, String text) {
        this.recipientSms = recipientSms;
        this.recipientEmail = recipientEmail;
        this.text = text;
    }

    public void setRecipientSms(String recipientSms) {
        this.recipientSms = recipientSms;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getRecipientSms() {
        return recipientSms;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getText() {
        return text;
    }
}
