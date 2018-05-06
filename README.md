
# react-native-amap-locations

## Getting started

`$ npm install react-native-amap-locations --save`

### Mostly automatic installation

`$ react-native link react-native-amap-locations`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import cn.babycherry.react.location.AMapLocationsPackage;` to the imports at the top of the file
  - Add `new AMapLocationsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-amap-locations'
  	project(':react-native-amap-locations').projectDir = new File(
      rootProject.projectDir, 
      '../node_modules/react-native-amap-locations/android'
    )
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-amap-locations')
  	```


## Usage
```javascript
import AmapLocations from 'react-native-amap-locations';

// one time location
Location.getCurrentPositionAsync(options)

// response
const result = {
  // success --> 0, failure --> other situations
  code: 0,
  coords: {
    altitude: 0, 
    longitude: 121.535718, 
    latitude: 31.059184
  },
  description: '定位成功',
  timestamp: 1525612811812
};
```

```typescript
type LocationMode = "Battery_Saving" | "Device_Sensors" | "Hight_Accuracy";
type LocationPurpose = "SignIn" | "Sport" | "Transport";

interface Options {
    mode: LocationMode,
    purpose: LocationPurpose
}
```

## License
MIT
  
