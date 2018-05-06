/**
 * @description - react-native-amap-locations API
 * @author - yang.yuncai <383234388@qq.com>
 */

// external
import { NativeModules } from 'react-native';

// scope
const { AMapLocations } = NativeModules;
const LocationMode = {
  Battery_Saving: 'Battery_Saving',
  Device_Sensors: 'Device_Sensors',
  Hight_Accuracy: 'Hight_Accuracy'
};

const LocationPurpose = {
  SignIn: 'SignIn',
  Sport: 'Sport',
  Transport: 'Transport'
};

/**
 * @typedef {object} Options
 *
 * @property {string} mode
 * @property {string} protocol
 * @property {string} purpose
 */

/**
 * @description - normalize options
 *
 * @param {Options} options
 *
 * @return {Options}
 */
function validate(options) {
  const nextOptions = {};
  const { mode, purpose } = options;

  nextOptions.mode = !!Reflect.get(LocationMode, mode) ?
    mode :
    LocationMode.Hight_Accuracy;

  nextOptions.purpose = !!Reflect.get(LocationPurpose, purpose) ?
    purpose :
    LocationPurpose.SignIn;

  return nextOptions;
}

/**
 * @description - get position one time
 * @param {Options} options
 */
function getCurrentPositionAsync(options = {}) {
  const validOptions = validate(options);

  return AMapLocations.getCurrentPositionAsync(validOptions);
}

/**
 *
 * @param {object} options
 * @param {Function} callback
 */
function watchCurrentPositionAsync(options, callback) {
}

export default {
  getCurrentPositionAsync,
  watchCurrentPositionAsync
};
