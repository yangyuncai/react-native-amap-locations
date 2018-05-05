
package cn.babycherry.react.location;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class AMapLocationsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public AMapLocationsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "AmapLocations";
  }
}