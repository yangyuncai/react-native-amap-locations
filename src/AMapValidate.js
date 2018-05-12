/**
 * @description - 校验基本参数，输出可用参数
 * @author - yang.yuncai <383234388@qq.com>
 */

// external
import { NativeModules } from 'react-native';

// scope
const {
  AMapLocations: { LocationMode, LocationPurpose },
} = NativeModules;

/**
 * @typedef {object} Options
 *
 * @property {string} mode
 * @property {string} purpose
 * @property {number} interval - only watch mode
 */

/**
 * @description - normalize options for once location
 *
 * @param {Options} params
 *
 * @return {Options}
 */
export function validateOnceLocation(params) {
  const options = {};
  const { mode, purpose } = params;

  options.mode = !!Reflect.get(LocationMode, mode)
    ? mode
    : LocationMode.Hight_Accuracy;

  options.purpose = !!Reflect.get(LocationPurpose, purpose)
    ? purpose
    : LocationPurpose.SignIn;

  return options;
}

/**
 * @description - normalize options for interval location
 *
 * @param {Options} params
 *
 * @return {Options}
 */
export function validateWatchLocation(params) {
  const options = {};
  const { mode, purpose, interval } = options;

  options.mode = !!Reflect.get(LocationMode, mode)
    ? mode
    : LocationMode.Hight_Accuracy;

  options.purpose = !!Reflect.get(LocationPurpose, purpose) ? purpose : LocationPurpose.Sport;

  // 默认定位间隔
  options.interval = typeof interval === 'number' ? interval : 2000;

  return options;
}
