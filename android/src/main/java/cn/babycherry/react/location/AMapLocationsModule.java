package cn.babycherry.react.location;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AMapLocationsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public AMapLocationsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "AMapLocations";
  }

  @Override
  public Map<String, Object> getConstants() {
    return Collections.unmodifiableMap(new HashMap<String, Object>() {
      {
        put("LocationMode", getLocationModeTypes());
        put("LocationPurpose", getLocationPurposeTypes());
      }

      private Map<String, Object> getLocationModeTypes() {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {
          {
            put(AMapLocationClientOption.AMapLocationMode.Battery_Saving.name(), String.valueOf(AMapLocationClientOption.AMapLocationMode.Battery_Saving));
            put(AMapLocationClientOption.AMapLocationMode.Device_Sensors.name(), String.valueOf(AMapLocationClientOption.AMapLocationMode.Device_Sensors));
            put(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy.name(), String.valueOf(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy));
          }
        });
      }

      private Map<String, Object> getLocationPurposeTypes() {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {
          {
            put(AMapLocationClientOption.AMapLocationPurpose.SignIn.name(), String.valueOf(AMapLocationClientOption.AMapLocationPurpose.SignIn));
            put(AMapLocationClientOption.AMapLocationPurpose.Sport.name(), String.valueOf(AMapLocationClientOption.AMapLocationPurpose.Sport));
            put(AMapLocationClientOption.AMapLocationPurpose.Transport.name(), String.valueOf(AMapLocationClientOption.AMapLocationPurpose.Transport));
          }
        });
      }
    });
  }

  // 单次定位回调
  private void handleOnceLocationChange(AMapLocation aMapLocation, Promise promise) {
    WritableMap result = Arguments.createMap();

    if (aMapLocation != null) {
      if (aMapLocation.getErrorCode() == 0) {
        // transform location meta
        WritableMap coords = Arguments.createMap();

        coords.putDouble("latitude", aMapLocation.getLatitude());
        coords.putDouble("longitude", aMapLocation.getLongitude());
        coords.putDouble("altitude", aMapLocation.getAltitude());

        // 标准化定位结果
        result.putInt("code", 0);
        result.putString("description", "定位成功");
        result.putMap("coords", coords);
        result.putDouble("timestamp", System.currentTimeMillis());
      } else {
        result.putInt("code", aMapLocation.getErrorCode());
        result.putString("description", aMapLocation.getErrorInfo());
      }
    } else {
      result.putInt("code", -1);
      result.putString("description", "定位结果不存在");
    }

    promise.resolve(result);
  }

  private AMapLocationClientOption setAMapLocationOptions(final ReadableMap options) {
    String mode = options.getString("mode");
    String purpose = options.getString("purpose");

    AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
    // 设置参数
    aMapLocationClientOption
        .setLocationMode(AMapLocationClientOption.AMapLocationMode.valueOf(mode))
        .setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.valueOf(purpose));

    return aMapLocationClientOption;
  }

  @ReactMethod
  public void getCurrentPositionAsync(final ReadableMap options, final Promise promise) {
    AMapLocationClientOption aMapLocationClientOption = setAMapLocationOptions(options);
    // 所有正常调用，Promise 皆为 resolve
    AMapLocationListener aMapLocationListener = new AMapLocationListener() {
      @Override
      public void onLocationChanged(AMapLocation aMapLocation) {
        handleOnceLocationChange(aMapLocation, promise);
      }
    };
    AMapLocationClient aMapLocationClient = new AMapLocationClient(this.reactContext.getCurrentActivity());

    // 单次定位
    aMapLocationClientOption.setOnceLocation(true);
    // 设置定位回调
    aMapLocationClient.setLocationOption(aMapLocationClientOption);
    aMapLocationClient.setLocationListener(aMapLocationListener);
    aMapLocationClient.startLocation();
  }

  @ReactMethod
  public void watchCurrentPositionAsync(ReadableMap options, Callback locationSuccessCallback, Callback locationFailureCallback) {
  }
}