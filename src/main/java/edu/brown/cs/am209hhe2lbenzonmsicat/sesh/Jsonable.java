package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public interface Jsonable {
  public static Gson GSON = new Gson();

  /**
   * @return The object as a Map.
   */
  public Map<String, Object> toMap();

  /**
   * @return the object as a JSON string
   */
  public default JsonElement toJson() {
    return GSON.toJsonTree(toMap());
  }

}
