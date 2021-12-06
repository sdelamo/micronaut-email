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
import io.micronaut.core.util.StringUtils;

/**
 * Utility classes to validate emails.
 * @author Sergio del Amo
 * @since 1.0.0
 */
public final class EmailValidationUtils {

    /**
     * @param email to be validated
     * @throws IllegalArgumentException if email is not valid
     */
    public static void validate(@NonNull EmailWithoutContent email) throws IllegalArgumentException {
        if (!RecipientsUtils.isValid(email)) {
            throw new IllegalArgumentException("At least one to, cc or bcc recipient must be specified");
        }
        if (email.getFrom() == null || StringUtils.isEmpty(email.getFrom().getEmail())) {
            throw new IllegalArgumentException("you have to specify the sender of the email");
        }
        if (StringUtils.isEmpty(email.getSubject())) {
            throw new IllegalArgumentException("you have to specify the email's subject");
        }
    }
}