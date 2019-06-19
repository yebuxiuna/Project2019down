package com.example.demo.result;

import lombok.Data;

@Data
public class ResultVO<T> {
    String msg;
    T data;
}
