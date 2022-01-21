package com.lordjoe.utilities;

/**
 * com.lordjoe.utilities.SendMail
 * User: Steve
 * Date: 6/4/2020
 */

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SendMail {

    private static final File mailProperties = new File("/opt/blastserver/keys/Mailer.properties");

    private static final Properties mailerProperies = new Properties();

    private static synchronized void guaranterMailProperties() {
        try {
            if (mailerProperies.isEmpty()) {
                FileInputStream fs = new FileInputStream(mailProperties);
                mailerProperies.load(fs);
                fs.close();
                setUsername(mailerProperies.getProperty("mail.smtp.user"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public static boolean UseListSMTP = !System.getProperty("user.name").equalsIgnoreCase("steve");

    private static String g_username = "steve"; // "FEDER-bio-HPC-jobs@list.lu";

    public static String getUsername() {
        return g_username;
    }

    public static void setUsername(String username) {
        g_username = username;
    }


    private static Session getMailSession() {
        guaranterMailProperties();
        Properties used = new Properties();
        used.putAll(mailerProperies);
        used.remove("mail.smtp.encryptedPassword");
        used.remove("mail.smtp.user");
        Authenticator authenticator = null;
        String username = getUsername();
        String pwd = mailerProperies.getProperty("mail.smtp.encryptedPassword");
        String passwordX = mailerProperies.getProperty("mxe.smtp.password");
        if (pwd != null) {
            passwordX = Encrypt.decryptString(pwd);
        }
          if(passwordX != null) {
              final String password = passwordX;
                 authenticator = new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                     return new PasswordAuthentication(username, password);
              }
            };

        }
        Session session = Session.getDefaultInstance(used, authenticator);
        return session;
    }


    public static void sendMail(String recipient, String subjectline, String messagebody, ILogger logger) {
        String username = getUsername();
        Session session = getMailSession();

//           Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(username, pwdx);
//                    }
//                });
//
        try {
            if (logger != null) logger.log("Session Created ");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            //message.setSubject("Testing Subject");
            message.setSubject(subjectline);
            //message.setText("Dear Mail Crawler,"
            //	+ "\n\n No spam to my email, please!");
            // Create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

// Fill the message
            messageBodyPart.setText(messagebody, "UTF-8", "html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

// Put parts in message
            message.setContent(multipart);

            if (logger != null) logger.log("Message created");
            Transport.send(message);

            if (logger != null) logger.log("Message sent");
            System.out.println("EMail Sent");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

//
//    {
//        props.put("mail.smtp.auth", "false");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.private.list.lu");
//        props.put("mail.smtp.port", "25");
//    }
        //Get properties object
        //  props.put("mail.smtp.host", "smtp.gmail.com");
        //  props.put("mail.smtp.socketFactory.port", "465");
        //get Session


        try {
            if (logger != null) logger.log("Session Created ");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            //message.setSubject("Testing Subject");
            message.setSubject(subjectline);
            //message.setText("Dear Mail Crawler,"
            //	+ "\n\n No spam to my email, please!");
            // Create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

// Fill the message
            messageBodyPart.setText(messagebody, "UTF-8", "html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

// Put parts in message
            message.setContent(multipart);

            if (logger != null) logger.log("Message created");
            Transport.send(message);

            if (logger != null) logger.log("Message sent");
            System.out.println("EMail Sent");

        } catch (
                MessagingException e) {
            throw new RuntimeException(e);
        }

    }

 /*   public static void sendMailWithAttachment(String recipient, String subjectline, String messagebody, File attachment) {

        String username = getUsername();
        Session session = getMailSession();


        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("FEDER-bio-HPC-jobs@list.lu"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            //message.setSubject("Testing Subject");
            message.setSubject(subjectline);
            //message.setText("Dear Mail Crawler,"
            //	+ "\n\n No spam to my email, please!");

            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText(messagebody);

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            DataSource source = new FileDataSource(attachment);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(attachment.getName());
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }*/


    public static void main(String[] args) {

        String recipient = "lordjoe2000@gmail.com <lordjoe2000@gmail.com>";
        if(args.length > 0)
            recipient = args[0];
        String subjectline = "Your BLAST Analysis is complete";
        String messagebody = "The results are attached!";

        sendMail(recipient, subjectline, messagebody, null);
        //   sendMailWithAttachment(recipient, subjectline, messagebody,results);
        System.out.println("Done");
    }
}

