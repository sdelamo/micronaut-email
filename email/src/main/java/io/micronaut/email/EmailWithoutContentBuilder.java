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
package io.micronaut.email;

import io.micronaut.core.annotation.NonNull;

import java.util.function.Consumer;

/**
 * Represents an email without builder.
 * @author Sergio del Amo
 * @since 1.0.0
 * @param <B> The Email builder
 * @param <E> The Email representation
 */
public interface EmailWithoutContentBuilder<B, E> {

    /**
     *
     * @param trackLinks Whether email links should be tracked
     * @return Email Builder
     */
    @NonNull
    B trackLinks(@NonNull TrackLinks trackLinks);

    /**
     * Email links should be tracked in HTML emails.
     * @return Email Builder
     */
    @NonNull
    B trackLinksInHtml();

    /**
     * Email links should be tracked in Text emails.
     * @return Email Builder
     */
    @NonNull
    B trackLinksInText();

    /**
     * Email links should be tracked in HTML and Text emails.
     * @return Email Builder
     */
    @NonNull
    B trackLinksInHtmlAndText();

    /**
     *
     * @param from sender email address
     * @return Email builder
     */
    @NonNull
    B from(@NonNull String from);

    /**
     *
     * @param from sender email address
     * @return Email builder
     */
    @NonNull
    B from(@NonNull Contact from);

    /**
     *
     * @param replyTo reply to email address
     * @return Email builder
     */
    @NonNull
    B replyTo(@NonNull String replyTo);

    /**
     *
     * @param replyTo reply to contact
     * @return Email builder
     */
    @NonNull
    B replyTo(@NonNull Contact replyTo);

    /**
     *
     * @param to Email recipient
     * @return Email builder
     */
    @NonNull
    B to(@NonNull String to);

    /**
     *
     * @param to Email recipient
     * @return Email builder
     */
    @NonNull
    B to(@NonNull Contact to);

    /**
     *
     * @param cc carbon copy recipient
     * @return Email builder
     */
    @NonNull
    B cc(@NonNull Contact cc);

    /**
     *
     * @param bcc Blind carbon copy recipient
     * @return Email builder
     */
    @NonNull
    B bcc(@NonNull Contact bcc);

    /**
     *
     * @param subject Email's subject
     * @return Email builder
     */
    @NonNull
    B subject(@NonNull String subject);

    /**
     *
     * @param cc carbon copy email address
     * @return Email builder
     */
    @NonNull
    B cc(@NonNull String cc);

    /**
     *
     * @param bcc blind carbon copy email address
     * @return Email builder
     */
    @NonNull
    B bcc(@NonNull String bcc);

    /**
     *
     * @param trackOpens Whether the email needs to track the opening.
     * @return Email builder
     */
    @NonNull
    B trackOpens(boolean trackOpens);

    /**
     *
     * @param attachment Email's attachment
     * @return Email builder
     */
    @NonNull
    B attachment(@NonNull Attachment attachment);

    /**
     *
     * @param attachment Email attachment builder
     * @return Email Builder
     */
    @NonNull
    B attachment(@NonNull Consumer<Attachment.Builder> attachment);

    /**
     *
     * @return Builds an Email
     * @throws IllegalArgumentException If recipients are empty or subject or sender are missing
     */
    @NonNull
    E build() throws IllegalArgumentException;
}
