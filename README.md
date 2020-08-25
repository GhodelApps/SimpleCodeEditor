<h1 align="center">SimpleCodeEditor</h1></br>
<p align="center">⚡ SimpleCodeEditor with JavaScript Code Highlighter ⚡</p></br>
<p align="center">
  <a href="https://github.com/sungbin5304/SimpleCodeEditor/blob/master/LICENSE"><img alt="License" src="https://img.shields.io/badge/License-Apache2-blue"/></a>
  <a href="https://jitpack.io/#sungbin5304/SimpleCodeEditor"><img alt="Download" src="https://jitpack.io/v/sungbin5304/SimpleCodeEditor.svg"/></a>
  <a href="https://github.com/sungbin5304/SimpleCodeEditor"><img alt="Title" src="https://img.shields.io/badge/Simple-EIDTOR-ff69b4"/></a>
  <a href="https://github.com/sungbin5304/SimpleCodeEditor"><img alt="Highlight" src="https://img.shields.io/badge/Highlighter-JS-yellow"/></a>
</p><br>

<p align="center">
<img src="https://raw.githubusercontent.com/sungbin5304/SimpleCodeEditor/master/preview.png" width="50%"/>
</p>

# Download
```Gradle
repositories {
  mavenCentral()
  google()
  maven { 
    url 'https://jitpack.io' 
  }
}

dependencies {
  implementation 'com.github.sungbin5304:SimpleCodeEditor:{version}'
}
```

# Usage
## xml
```xml
<com.sungbin.texteditor.library.SimpleCodeEditor
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

## all attribute
|Attribute|Descriptionn|Default|Type|
|------------|-------------|-------------| -------------| 
|`sce_lineColor`|Set line color|`lineNumberColor` value (= `Color.BLACK`)|`Color`|
|`sce_lineNumberColor`|Set line number color|`Color.BLACK`|`Color`|
|`sce_lineNumberTextSize`|Set line number text size|13|`Integer`|
|`sce_focusLineColor`|Set focused line background color|`Color.CYAN`|`Color`|
|`sce_applyHighlighter`|Set JavaScript Highlighter (It may cause freezing for long string)|`true`|`Boolean`|
|`sce_reservedColor`|Set JavaScript Highlighter Reserved word color|`Color.argb(255, 21, 101, 192)`|`Color`|
|`sce_numberColort`|Set JavaScript Highlighter Number color|`Color.argb(255, 191, 54, 12)`|`Color`|
|`sce_stringColor`|Set JavaScript Highlighter String color|`Color.argb(255, 255, 160, 0)`|`Color`|
|`sce_annotationColor`|Set JavaScript Highlighter Annotation color|`Color.argb(255, 139, 195, 74)`|`Color`|
|`sce_enableHorizontallyScroll`|Set editor can HorizontallyScrolling|`false`|`Boolean`|`


## all methods
```kotlin
applyHighlight = boolean

undo()
redo()
findText(string: String, ignoreUpper: Boolean = false) (will return ArrayList<ArrayList<Int>>, Int ArrayList have LineNumber and Index value)
```

# Gradle Setting
You should set gradle file like below code.
```gradle
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}
```

# Gradle Error
If you error at gradle `More than one file was found with OS independent path 'META-INF/library_release.kotlin_module'` this, add below code at your gradle.
```gradle
android {
  packagingOptions {
      exclude 'META-INF/library_release.kotlin_module'
  }
}
```

# License
License is [Apache2](https://github.com/sungbin5304/SimpleCodeEditor/blob/master/LICENSE).

# Thanks
Thanks [DarkTornado](https://github.com/DarkTornado). (supported [JavaScript Highlighter](https://github.com/DarkTornado/AndroidCodeHighlighter))

# Happy Coding :)
