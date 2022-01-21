package io.micronaut.email.sendgrid

import io.micronaut.email.Attachment
import io.micronaut.email.Email
import io.micronaut.email.EmailSender
import io.micronaut.email.test.CiUtils
import io.micronaut.email.test.MailTestUtils
import io.micronaut.email.test.SpreadsheetUtils
import io.micronaut.http.MediaType
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Requires
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@MicronautTest(startApplication = false)
class SendGridEmailSenderAttachmentSpec extends Specification {

    @Inject
    EmailSender emailSender

    @Requires({env["SENDGRID_API_KEY"] && env["GMAIL_USERNAME"] && env["GMAIL_PASSWORD"] && (!CiUtils.runningOnCI() || (CiUtils.runningOnCI() && jvm.isJava11()))})
    void "Functional test of Email with Attachment for SendGrid integration"() {
        given:
        String subject = "[Sendgrid] Attachment Test" + UUID.randomUUID().toString()
        String gmail = System.getenv("GMAIL_USERNAME")

        when:
        emailSender.send(Email.builder()
                .from(gmail)
                .to(gmail)
                .subject(subject)
                .text("Hello world")
                .attachment(Attachment.builder()
                        .filename("monthlyreports.xlsx")
                        .contentType(MediaType.MICROSOFT_EXCEL_OPEN_XML)
                        .content(SpreadsheetUtils.spreadsheet())
                        .build()))
        then:
        new PollingConditions(initialDelay: 10, delay: 20, timeout: 300).eventually {
            1 == MailTestUtils.countAndDeleteInboxEmailsBySubject(subject)
        }
    }
}
