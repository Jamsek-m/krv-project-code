package com.mjamsek.auth.services;

import java.util.Map;

public interface TemplateService {

    String renderHtml(String htmlTemplate, Map<String, Object> params);

}
