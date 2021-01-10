package com.cyf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 陈一锋
 * @date 2021/1/10 18:28
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hello implements Serializable {

    private String message;
    private String description;
}
