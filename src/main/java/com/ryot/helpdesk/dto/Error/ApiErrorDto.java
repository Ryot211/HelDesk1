package com.ryot.helpdesk.dto.Error;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorDto {

    private int status;
    private String error;
    private String mensaje;
    private String path;
    private LocalDateTime timestamp;

}
