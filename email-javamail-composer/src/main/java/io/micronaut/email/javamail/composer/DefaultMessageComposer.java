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
package io.micronaut.email.javamail.composer;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.email.Attachment;
import io.micronaut.email.Body;
import io.micronaut.email.BodyType;
import io.micronaut.email.Contact;
import io.micronaut.email.Email;
import jakarta.inject.Singleton;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link MessageComposer}.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Singleton
public class DefaultMessageComposer implements MessageComposer {

    public static final String TYPE_TEXT_PLAIN_CHARSET_UTF_8 = "text/plain; charset=UTF-8";
    public static final String TYPE_TEXT_HTML_CHARSET_UTF_8 = "text/html; charset=UTF-8";

    @Override
    @NonNull
    public Message compose(@NonNull Email email,
                           @NonNull Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setSubject(email.getSubject(), "UTF-8");
        message.setFrom(new InternetAddress(email.getFrom().getEmail()));
        if (CollectionUtils.isNotEmpty(email.getTo())) {
            message.setRecipients(Message.RecipientType.TO, contactAddresses(email.getTo()));
        }
        if (CollectionUtils.isNotEmpty(email.getCc())) {
            message.setRecipients(Message.RecipientType.CC, contactAddresses(email.getCc()));
        }
        if (CollectionUtils.isNotEmpty(email.getBcc())) {
            message.setRecipients(Message.RecipientType.CC, contactAddresses(email.getBcc()));
        }
        MimeMultipart multipart = new MimeMultipart();
        for (MimeBodyPart bodyPart : bodyParts(email)) {
            multipart.addBodyPart(bodyPart);
        }
        message.setContent(multipart);
        return message;
    }

    @NonNull
    private Address[] contactAddresses(@NonNull Collection<Contact> contacts) throws AddressException {
        List<Address> addressList = new ArrayList<>();
        for (Contact contact : contacts) {
            addressList.addAll(Arrays.asList(InternetAddress.parse(contact.getEmail())));
        }
        Address[] array = new Address[addressList.size()];
        addressList.toArray(array);
        return array;
    }

    @NonNull
    private List<MimeBodyPart> bodyParts(@NonNull Email email) throws MessagingException {
        List<MimeBodyPart> parts = new ArrayList<>();
        bodyPart(email).ifPresent(parts::add);
        parts.addAll(attachmentBodyParts(email));
        return parts;
    }

    @NonNull
    private Optional<MimeBodyPart> bodyPart(@NonNull Email email) throws MessagingException {
        Body body = email.getBody();
        if (body != null) {
            MimeBodyPart bodyPart = new MimeBodyPart();
            if (body.getType() == BodyType.HTML) {
                bodyPart.setContent(body.get(), TYPE_TEXT_HTML_CHARSET_UTF_8);
            } else if (body.getType() == BodyType.TEXT) {
                bodyPart.setContent(body.get(), TYPE_TEXT_PLAIN_CHARSET_UTF_8);
            }
            return Optional.of(bodyPart);
        }
        return Optional.empty();
    }

    @NonNull
    private List<MimeBodyPart> attachmentBodyParts(@NonNull Email email) throws MessagingException {
        if (email.getAttachments() == null) {
            return Collections.emptyList();
        }
        List<MimeBodyPart> list = new ArrayList<>();
        for (Attachment attachment : email.getAttachments()) {
            MimeBodyPart mimeBodyPart = attachmentBodyPart(attachment);
            list.add(mimeBodyPart);
        }
        return list;
    }

    private MimeBodyPart attachmentBodyPart(@NonNull Attachment attachment) throws MessagingException {
        MimeBodyPart att = new MimeBodyPart();
        DataSource fds = new ByteArrayDataSource(attachment.getContent(), attachment.getContentType());
        att.setDataHandler(new DataHandler(fds));
        String reportName = attachment.getFilename();
        att.setFileName(reportName);
        return att;
    }
}
