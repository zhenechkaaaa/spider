package ru.nsu.odnostorontseva.spider;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Response {
    String message;
    List<String> successors;
}
