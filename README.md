
# react-native-status-bar

## Getting started

`$ npm install react-native-status-bar --save`

### Mostly automatic installation

`$ react-native link react-native-status-bar`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import im.shi.statusbarmanager.RNStatusbarManagerPackage;` to the imports at the top of the file
  - Add `new RNStatusbarManagerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-status-bar'
  	project(':react-native-status-bar').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-status-bar/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-status-bar')
  	```


## Usage

#### Attention:
Anyway,the statusBar in this moudule is not the system statusBar.
the theme activity has should not set to fitsSystemWindow=true,you must set this:
```angular2html
fitsSystemWindow=false
```

###Example
```java

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        RNStatusbarManagerModule.translucentStatusBar(this, true);
        View content = LayoutInflater.from(this).inflate(R.layout.test, null);
        RNStatusbarManagerModule.steepStatusbarView(this, content, android.R.color.white);
        
    }
}
```
### This set avtivity to full screen with transparent background  statusbar and dark(or not) word（icon） in statusbar。 
```
 RNStatusbarManagerModule.translucentStatusBar(this, true);//or false
```
### if you want set statusBar to color white backgroud,you should do this below:
```
 RNStatusbarManagerModule.steepStatusbarView(this, content, android.R.color.white);
```
### if you want set statusBar to color with alpha,you should do this below:
```
RNStatusbarManagerModule.steepStatusbarView(this,content,0xcc,0x88);
```
### if you want set activity looks like fitsSystemWindow = false or not ,you should do this below:
```
RNStatusbarManagerModule.steepStatusbarView(this,content,false);//or true
```



