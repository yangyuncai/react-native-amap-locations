/**
 * @description - 高德 SDK API 封装
 * @author - yang.yuncai <383235388@qq.com>
 */

// external
import { DeviceEventEmitter, NativeModules } from 'react-native';
// internal
import { validateOnceLocation, validateWatchLocation } from './AMapValidate';

// scope
const { AMapLocations } = NativeModules;
const { AMapLocationEvent } = AMapLocations;

// LocationMode
// Battery_Saving, Device_Sensors, Hight_Accuracy;

// LocationPurpose
// SignIn, Sport, Transport;

export default class AMapLocationManager {
  constructor() {
    this.hasActiveLocationService = false;
  }

  /**
   * @description - get position one time
   *
   * @param {Options} options
   */
  async getCurrentPositionAsync(options = {}) {
    try {
      const validOptions = await validateOnceLocation(options);
      const location = await AMapLocations.getCurrentPositionAsync(
        validOptions
      );

      // 原生代码无视定位错误，JS内部进行处理
      return location.code === 0
        ? Promise.resolve(location)
        : Promise.reject(location);
    } catch (err) {
      return Promise.reject({
        code: -2,
        description: err.message,
      });
    }
  }

  /**
   * @description - add AMapLocationChange listener
   *
   *  @param {function} callback
   *
   * @return {{unsubscribe: unsubscribe}}
   */
  connectAMapChangeEvent(callback) {
    const subscription = DeviceEventEmitter.addListener(AMapLocationEvent, (location) => {
      callback(location);
    });

    return {
      unsubscribe: () => {
        // 取消事件监听
        DeviceEventEmitter.removeSubscription(subscription);

        // 销毁定位
        this.stopWatchCurrentPositionAsync();
      }
    }
  }

  /**
   * @description - get position with interval
   *
   * @param {object} options
   * @param {Function} callback
   *
   * @return {Subscription}
   */
  watchCurrentPositionAsync(options, callback) {
    if (this.hasActiveLocationService) {
      return this.connectAMapChangeEvent(callback);
    } else {
      const validOptions = validateWatchLocation(options);

      // 启动定位服务
      AMapLocations.watchCurrentPositionAsync(validOptions);

      this.hasActiveLocationService = true;

      return this.connectAMapChangeEvent(callback);
    }
  }

  /**
   * @description - cleanup operation trace
   *
   * @return {void}
   */
  stopWatchCurrentPositionAsync() {
    this.hasActiveLocationService = false;

    // 销毁定位实例
    AMapLocations.stopWatchCurrentPositionAsync();
  }
}
