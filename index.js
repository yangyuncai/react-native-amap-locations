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
  Hight_Accuracy: 'Hight_Accuracy',
};

const LocationPurpose = {
  SignIn: 'SignIn',
  Sport: 'Sport',
  Transport: 'Transport',
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

  nextOptions.mode = !!Reflect.get(LocationMode, mode)
    ? mode
    : LocationMode.Hight_Accuracy;

  nextOptions.purpose = !!Reflect.get(LocationPurpose, purpose)
    ? purpose
    : LocationPurpose.SignIn;

  return nextOptions;
}

/**
 * @description - get position one time
 * @param {Options} options
 */
async function getCurrentPositionAsync(options = {}) {
  try {
    const validOptions = await validate(options);
    const location = await AMapLocations.getCurrentPositionAsync(validOptions);

    // 原生代码无视定位错误，JS内部进行处理
    return location.code === 0 ? Promise.resolve(location) : Promise.reject(location);
  } catch (err) {
    return Promise.reject({
      code: -2,
      description: err.message
    });
  }
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
  watchCurrentPositionAsync,
};
