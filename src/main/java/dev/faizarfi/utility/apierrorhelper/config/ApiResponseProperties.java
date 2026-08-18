package dev.faizarfi.utility.apierrorhelper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api-response")
public class ApiResponseProperties {

    // Enable or disable the API response wrapping globally. Default is true.
    private boolean enable = true;

    private boolean includeStacktrace = false;

    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isIncludeStacktrace() {
        return includeStacktrace;
    }

    public void setIncludeStacktrace(boolean includeStacktrace) {
        this.includeStacktrace = includeStacktrace;
    }

}
