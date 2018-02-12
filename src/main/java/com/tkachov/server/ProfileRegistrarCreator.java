package com.tkachov.server;

import java.util.Optional;

public class ProfileRegistrarCreator {
    private static ProfileRegistratorImpl profileRegistrator;

    public static ProfileRegistratorImpl createOrGetRegistrator() {
        return Optional.ofNullable(profileRegistrator).orElse(profileRegistrator = new ProfileRegistratorImpl());
    }
}
