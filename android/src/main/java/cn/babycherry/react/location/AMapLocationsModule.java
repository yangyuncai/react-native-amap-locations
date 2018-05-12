package cn.babycherry.react.location;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AMapLocationsModule extends ReactContextBaseJavaModule {
  private final static String AMapLocationEvent = "AMapLocationChange";
  private static AMapLocationClient aMapWatchLocationClient = null;
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
        put("AMapLocationEvent", AMapLocationEvent);
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
  private WritableMap semanticsAMapLocation(AMapLocation aMapLocation) {
    WritableMap result = Arguments.createMap();

    if (aMapLocation != null) {
      if (aMapLocation.getErrorCode() == 0) {
        // transform location meta
        WritableMap coords = Arguments.createMap();

        coords.putDouble("latitude", aMapLocation.getLatitude());
        coords.putDouble("longitude", aMapLocation.getLongitude());
        coords.putDouble("altitude", aMapLocation.getAltitude());
        coords.putString("address", aMapLocation.getAddress());

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

    return result;
  }

  private AMapLocationClientOption setAMapOnceLocationOptions(final ReadableMap options) {
    String mode = options.getString("mode");
    String purpose = options.getString("purpose");

    AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
    // 设置参数
    aMapLocationClientOption
        .setLocationMode(AMapLocationClientOption.AMapLocationMode.valueOf(mode))
        .setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.valueOf(purpose));

    return aMapLocationClientOption;
  }

  private AMapLocationClientOption setAMapWatchLocationOptions(final ReadableMap options) {
    String mode = options.getString("mode");
    String purpose = options.getString("purpose");
    Number interval = options.getInt("interval");

    AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
    // 设置参数
    aMapLocationClientOption
        .setLocationMode(AMapLocationClientOption.AMapLocationMode.valueOf(mode))
        .setInterval(interval.longValue());

    if (purpose != null) {
      aMapLocationClientOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.valueOf(purpose));
    }

    return aMapLocationClientOption;
  }

  @ReactMethod
  public void getCurrentPositionAsync(final ReadableMap options, final Promise promise) {
    final AMapLocationClientOption aMapLocationClientOption = setAMapOnceLocationOptions(options);
    final AMapLocationClient aMapLocationClient = new AMapLocationClient(getReactApplicationContext());

    // 所有正常调用，Promise 皆为 resolve
    // 获取数据后销毁定位服务实例，多次调用将重新实例化定位服务
    AMapLocationListener aMapLocationListener = new AMapLocationListener() {
      @Override
      public void onLocationChanged(AMapLocation aMapLocation) {
        aMapLocationClient.onDestroy();
        promise.resolve(semanticsAMapLocation(aMapLocation));
      }
    };

    // 单次定位
    aMapLocationClientOption.setOnceLocation(true);
    // 设置定位回调
    aMapLocationClient.setLocationOption(aMapLocationClientOption);
    aMapLocationClient.setLocationListener(aMapLocationListener);
    aMapLocationClient.startLocation();
  }

  @ReactMethod
  public void watchCurrentPositionAsync(ReadableMap options) {
    final AMapLocationClientOption aMapLocationClientOption = setAMapWatchLocationOptions(options);
    final AMapLocationClient aMapLocationClient = new AMapLocationClient(getCurrentActivity());
    // SDK 无法定位认定为正常，JavaScript 端进行错误代码判断
    AMapLocationListener aMapLocationListener = new AMapLocationListener() {
      @Override
      public void onLocationChanged(AMapLocation aMapLocation) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(AMapLocationEvent, semanticsAMapLocation(aMapLocation));
      }
    };

    // 定位 watch 逻辑
    aMapLocationClient.setLocationOption(aMapLocationClientOption);
    aMapLocationClient.setLocationListener(aMapLocationListener);
    aMapLocationClient.startLocation();
  }

  @ReactMethod
  public void stopWatchCurrentPositionAsync() {
    if (aMapWatchLocationClient != null) {
      aMapWatchLocationClient.stopLocation();
      aMapWatchLocationClient.onDestroy();
    }
  }
}