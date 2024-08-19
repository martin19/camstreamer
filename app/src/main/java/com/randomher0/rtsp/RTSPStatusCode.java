package com.randomher0.rtsp;

public enum RTSPStatusCode {
    // 1xx Informational
    CONTINUE(100, "Continue"),

    // 2xx Success
    OK(200, "OK"),
    CREATED(201, "Created"),
    LOW_ON_STORAGE_SPACE(250, "Low on Storage Space"),

    // 3xx Redirection
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    MOVED_TEMPORARILY(302, "Moved Temporarily"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),

    // 4xx Client Error
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    PARAMETER_NOT_UNDERSTOOD(451, "Parameter Not Understood"),
    CONFERENCE_NOT_FOUND(452, "Conference Not Found"),
    NOT_ENOUGH_BANDWIDTH(453, "Not Enough Bandwidth"),
    SESSION_NOT_FOUND(454, "Session Not Found"),
    METHOD_NOT_VALID_IN_THIS_STATE(455, "Method Not Valid in This State"),
    HEADER_FIELD_NOT_VALID_FOR_RESOURCE(456, "Header Field Not Valid for Resource"),
    INVALID_RANGE(457, "Invalid Range"),
    PARAMETER_IS_READ_ONLY(458, "Parameter Is Read-Only"),
    AGGREGATE_OPERATION_NOT_ALLOWED(459, "Aggregate Operation Not Allowed"),
    ONLY_AGGREGATE_OPERATION_ALLOWED(460, "Only Aggregate Operation Allowed"),
    UNSUPPORTED_TRANSPORT(461, "Unsupported Transport"),
    DESTINATION_UNREACHABLE(462, "Destination Unreachable"),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    RTSP_VERSION_NOT_SUPPORTED(505, "RTSP Version Not Supported"),
    OPTION_NOT_SUPPORTED(551, "Option Not Supported");

    private final int code;
    private final String description;

    RTSPStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
