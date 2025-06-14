package com.devmam.zalomini.exception.customizeException;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CallException extends RuntimeException {
    private Object data;
    public CallException(String message) {
        super(message);
    }
    public CallException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallException(String message, Object data) {
        super(message);
        this.data = data;
    }

}
