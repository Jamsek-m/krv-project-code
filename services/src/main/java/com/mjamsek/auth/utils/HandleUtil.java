package com.mjamsek.auth.utils;

import com.mjamsek.auth.lib.Client;
import com.mjamsek.rest.exceptions.ValidationException;

import javax.ws.rs.BadRequestException;
import java.util.Locale;

public class HandleUtil {
    
    private static final String HANDLE_REGEX = "^[a-z0-9-_]+$";
    
    public static String validateOrCreateHandle(String handle, String name) {
        if (handle == null || handle.isBlank()) {
            return HandleUtil.toHandle(name);
        } else {
            HandleUtil.validateHandle(handle, "handle", Client.class.getSimpleName());
            return handle;
        }
    }
    
    public static String toHandle(String value) {
        value = value.toLowerCase(Locale.ROOT).trim();
        value = value.replaceAll(" +", " ");
        value = value.replaceAll("\\s", "-");
        return value.replaceAll("/[^a-z0-9-_]/", "");
    }
    
    public static void validateHandle(String handle) throws BadRequestException {
        if (!handle.matches(HANDLE_REGEX)) {
            throw new ValidationException("Handle does not conform to required form!")
                .withDescription("Handle does not conform to required form!")
                .isValidationError();
        }
    }
    
    public static void validateHandle(String handle, String fieldName) throws BadRequestException {
        if (!handle.matches(HANDLE_REGEX)) {
            throw new ValidationException("Handle does not conform to required form!")
                .withDescription("Handle does not conform to required form!")
                .withField(fieldName)
                .isValidationError();
        }
    }
    
    public static void validateHandle(String handle, String fieldName, String entity) throws BadRequestException {
        if (!handle.matches(HANDLE_REGEX)) {
            throw new ValidationException("Handle does not conform to required form!")
                .withDescription("Handle does not conform to required form!")
                .withField(fieldName)
                .withEntity(entity)
                .isValidationError();
        }
    }
}
