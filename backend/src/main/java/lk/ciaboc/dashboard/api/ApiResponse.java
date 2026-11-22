package lk.ciaboc.dashboard.api;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        boolean success,
        T data,
        String error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(Instant.now(), true, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(Instant.now(), false, null, message);
    }
}
