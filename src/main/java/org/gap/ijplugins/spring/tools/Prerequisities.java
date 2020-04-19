package org.gap.ijplugins.spring.tools;

import java.util.Optional;

public final class Prerequisities {

  public static boolean isJava8() {
    return Optional.ofNullable(System.getProperty("java.version")).map(v -> v.startsWith("1.8"))
        .orElseGet(() -> Boolean.FALSE).booleanValue();
  }

  public static boolean isBelowJava8() {
    return !isJava8() && Optional.ofNullable(System.getProperty("java.version"))
        .map(v -> v.startsWith("1.")).orElseGet(() -> Boolean.FALSE).booleanValue();
  }
}
