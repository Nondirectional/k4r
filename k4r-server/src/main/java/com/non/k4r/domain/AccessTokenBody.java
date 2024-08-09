package com.non.k4r.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class AccessTokenBody implements Serializable {
    private Long userId;

    private String token;
    @Serial
    private static final long serialVersionUID = 1L;
}
