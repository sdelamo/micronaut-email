package io.micronaut.email.postmark


import io.micronaut.email.Email
import io.micronaut.email.test.MailTestUtils
import io.micronaut.email.EmailSender
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Requires
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@MicronautTest(startApplication = false)
class PostmarkEmailSenderSpec extends Specification {

    @Inject
    EmailSender emailSender

    @Requires({env["POSTMARK_API_TOKEN"] && env["GMAIL_USERNAME"] && env["GMAIL_PASSWORD"]})
    void "Functional test of postmark integration"() {
        given:
        String subject = "[Postmark] Test"
        String to = System.getenv("GMAIL_USERNAME")
        when:
        emailSender.send(Email.builder()
                .from("marketing@micronaut.io")
                .to(to)
                .subject(subject).text("Hello world")
                .build())
        then:
        new PollingConditions(timeout: 30).eventually {
            1 == MailTestUtils.countAndDeleteInboxEmailsBySubject(to, System.getenv("GMAIL_PASSWORD"), subject)
        }
    }
}
