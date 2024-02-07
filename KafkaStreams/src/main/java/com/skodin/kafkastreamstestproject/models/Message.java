package com.skodin.kafkastreamstestproject.models;

public record Message(String from, String to, String payload) {
}
