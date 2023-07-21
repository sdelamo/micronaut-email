package io.micronaut.email.sendgrid

import com.fasterxml.jackson.databind.json.JsonMapper
import com.sendgrid.Request
import io.micronaut.email.Email
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class SendgridEmailComposerSpec extends Specification {

    @Inject
    SendgridEmailComposer sendgridEmailComposer

    void "from, to, only the last reply to and subject are put to the mime message"() {
        given:
        String from = "sender@example.com"
        String to = "receiver@example.com"
        String replyTo1 = "sender.reply.to.one@example.com"
        String replyTo2 = "sender.reply.to.two@example.com"
        String subject = "Apple Music"
        String body = "Lore ipsum body"

        Email email = Email.builder()
                .from(from)
                .to(to)
                .replyTo(replyTo1)
                .replyTo(replyTo2)
                .subject(subject)
                .body(body)
                .build()
        when:
        Request request = sendgridEmailComposer.compose(email)
        Map map = new JsonMapper().readValue(request.body, Map)

        then:
        map["from"]["email"] == from
        map["subject"] == subject
        map["personalizations"][0]["to"][0]["email"] == to
        map["personalizations"][0]["subject"] == subject
        map["content"][0]["type"] == "text/plain"
        map["content"][0]["value"] == body
        map["reply_to"]["email"] == replyTo2
    }
}