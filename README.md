
# react-native-react-native-amap-locations

## Getting started

`$ npm install react-native-react-native-amap-locations --save`

### Mostly automatic installation

`$ react-native link react-native-react-native-amap-locations`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import cn.babycherry.react.location.AMapReactNativeAmapLocationsPackage;` to the imports at the top of the file
  - Add `new AMapReactNativeAmapLocationsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-react-native-amap-locations'
  	project(':react-native-react-native-amap-locations').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-react-native-amap-locations/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-react-native-amap-locations')
  	```


## Usage
```javascript
import AMapReactNativeAmapLocations from 'react-native-react-native-amap-locations';

// TODO: What to do with the module?
AMapReactNativeAmapLocations;
```
  