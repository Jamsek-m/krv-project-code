package com.mjamsek.auth.api.servlets.utils;

import javax.servlet.ServletException;
import java.io.IOException;

@FunctionalInterface
public interface ConsentSupplier {
    boolean onRequiredConsent() throws IOException, ServletException;
}
