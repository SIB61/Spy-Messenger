package com.sib4u.spymessenger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserNameValidator {
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
    private Pattern pattern;
    private Matcher matcher;

    public UserNameValidator() {
        pattern = Pattern.compile ( USERNAME_PATTERN );
    }

    /**
     * Validate username with regular expression
     *
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public boolean validate(final String username) {

        matcher = pattern.matcher ( username );
        return matcher.matches ( );

    }
}

