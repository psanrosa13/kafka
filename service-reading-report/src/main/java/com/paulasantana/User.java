package com.paulasantana;

public class User {

  public static String uuid;

  public User(String uuid) {
    this.uuid = uuid;
  }

  public String getReportPath() {
    return "target/" + uuid + "/report.txt";
  }

  public static String getUuid() {
    return uuid;
  }
}
