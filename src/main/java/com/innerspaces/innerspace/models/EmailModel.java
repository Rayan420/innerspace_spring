package com.innerspaces.innerspace.models;

public class EmailModel {
        private String to;
        private String subject;
        private String templateName;

        private String firstName;

        private String lastName;
        private String username;


    public EmailModel( String username, String to, String subject, String templateName) {
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
        this.username=username;
    }

    public EmailModel(String to, String subject, String templateName, String firstName, String lastName) {
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public EmailModel(String to, String subject, String templateName) {
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


    public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }


}

