package com.function;

import org.apache.commons.lang3.StringUtils;

public class AccountSettings {
  public static String MASTER_KEY = System.getProperty("ACCOUNT_KEY",
      StringUtils.defaultString(StringUtils.trimToNull(
          System.getenv().get("ACCOUNT_KEY")),
          "2QBpn7mhziRf0LTBZErMjWEDwDCxfGfFIM647D5sOVaBfxGRvX40xsJkcBqUz9l55W5TwfIHrf8cCtltyUVKHA=="));

  public static String HOST = System.getProperty("ACCOUNT_HOST",
      StringUtils.defaultString(StringUtils.trimToNull(
          System.getenv().get("ACCOUNT_HOST")),
          "https://travel-project.documents.azure.com:443/"));

}
