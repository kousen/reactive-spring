package com.oreilly.restclient.json;

import java.util.List;

public record AstroResponse(String message, int number, List<Assignment> people) {
}
