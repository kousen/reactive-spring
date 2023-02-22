package com.oreilly.restclient.json;

// Records:
// - are final
// - are immutable
// - generate an equals() method, a hashCode() method, and a toString() method
// - use a constructor before the braces
public record Assignment(String name, String craft) {
}
