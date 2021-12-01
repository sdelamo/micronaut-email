/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.email.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.email.Attachment;
import io.micronaut.email.Contact;
import io.micronaut.email.EmailSender;
import io.micronaut.email.Email;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <a href="https://sendgrid.com">SendGrid</a> implementation of {@link EmailSender}.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Named(SendgridEmailSender.NAME)
@Singleton
public class SendgridEmailSender implements EmailSender {
    /**
     * {@link SendgridEmailSender} name.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String NAME = "sendgrid";
    private static final Logger LOG = LoggerFactory.getLogger(SendgridEmailSender.class);

    private final SendGrid sendGrid;

    /**
     *
     * @param sendGridConfiguration SendGrid Configuration
     */
    public SendgridEmailSender(SendGridConfiguration sendGridConfiguration) {
        sendGrid = new SendGrid(sendGridConfiguration.getApiKey());
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @Override
    public void send(@NonNull @NotNull @Valid Email email) {
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Sending email to {}", email.getTo());
            }
            send(createRequest(createMail(email)));
        } catch (IOException ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error sending email", ex);
            }
        }
    }

    @NonNull
    private Mail createMail(@NonNull Email email) {
        Mail mail = new Mail();
        com.sendgrid.helpers.mail.objects.Email from = new com.sendgrid.helpers.mail.objects.Email();
        from.setEmail(email.getFrom().getEmail());
        if (email.getFrom().getName() != null) {
            from.setName(email.getFrom().getName());
        }
        mail.from = from;
        mail.addPersonalization(createPersonalization(email));
        contentOfEmail(email).ifPresent(mail::addContent);

        if (email.getAttachments() != null) {
            for (Attachment att : email.getAttachments()) {
                mail.addAttachments(new Attachments.Builder(att.getFilename(), new ByteArrayInputStream(att.getContent()))
                        .withType(att.getContentType())
                        .withContentId(att.getId())
                        .build());
            }
        }
        return mail;
    }

    @NonNull
    private Personalization createPersonalization(@NonNull Email email) {
        Personalization personalization = new Personalization();
        personalization.setSubject(email.getSubject());
        for (Contact contactTo : email.getTo()) {
            com.sendgrid.helpers.mail.objects.Email to = new com.sendgrid.helpers.mail.objects.Email();
            to.setEmail(contactTo.getEmail());
            if (contactTo.getName() != null) {
                to.setName(contactTo.getName());
            }
            personalization.addTo(to);
        }

        if (email.getCc() != null) {
            for (Contact cc : email.getCc()) {
                com.sendgrid.helpers.mail.objects.Email ccEmail = new com.sendgrid.helpers.mail.objects.Email();
                ccEmail.setEmail(cc.getEmail());
                if (cc.getName() != null) {
                    ccEmail.setName(cc.getName());
                }
                personalization.addCc(ccEmail);
            }
        }

        if (email.getBcc()  != null) {
            for (Contact bcc : email.getBcc()) {
                com.sendgrid.helpers.mail.objects.Email bccEmail = new com.sendgrid.helpers.mail.objects.Email();
                bccEmail.setEmail(bcc.getEmail());
                if (bcc.getName() != null) {
                    bccEmail.setName(bcc.getName());
                }
                personalization.addBcc(bccEmail);
            }
        }
        return personalization;
    }

    @NonNull
    private Request createRequest(@NonNull Mail mail)  throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        return request;
    }

    private void send(@NonNull Request request) throws IOException {
        Response response = sendGrid.api(request);
        if (LOG.isInfoEnabled()) {
            LOG.info("Status Code: {}", String.valueOf(response.getStatusCode()));
            LOG.info("Body: {}", response.getBody());
            LOG.info("Headers {}", response.getHeaders()
                    .keySet()
                    .stream()
                    .map(key -> key.toString() + "=" + response.getHeaders().get(key))
                    .collect(Collectors.joining(", ", "{", "}")));
        }

    }

    @NonNull
    private Optional<Content> contentOfEmail(@NonNull Email email) {
        if (email.getText() != null) {
            return Optional.of(new Content("text/plain", email.getText()));
        }
        if (email.getHtml() != null) {
            return Optional.of(new Content("text/html", email.getHtml()));
        }
        return Optional.empty();
    }
}
