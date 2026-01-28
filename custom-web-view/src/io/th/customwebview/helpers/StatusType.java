package io.th.customwebview.helpers;

import java.util.HashMap;
import java.util.Map;

import com.google.appinventor.components.common.OptionList;

public enum StatusType implements OptionList<Integer> {
  Pending(1),
  Running(2),
  Paused(4),
  Successful(8),
  Failed(16);

  private int value;

  StatusType(int value) {
    this.value = value;
  }

  public Integer toUnderlyingValue() {
    return value;
  }

  private static final Map<Integer, StatusType> lookup = new HashMap<>();

  static {
    for (StatusType value : values()) {
      lookup.put(value.toUnderlyingValue(), value);
    }
  }

  public static StatusType fromUnderlyingValue(Integer value) {
    return lookup.get(value);
  }

}
