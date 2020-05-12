<h1 align="center">SimpleCodeEditor</h1></br>
<p align="center">SimpleCodeEditor with JavaScript Code Highlighter ⚡⚡</p></br>
<p align="center">
  <a href="https://github.com/sungbin5304/SimpleCodeEditor/blob/master/LICENSE"><img alt="License" src="https://img.shields.io/badge/License-Apache2-blue"/></a>
  <a href="https://jitpack.io/#sungbin5304/SimpleCodeEditor"><img alt="Download" src="https://jitpack.io/v/sungbin5304/SimpleCodeEditor.svg"/></a>
  <a href="https://github.com/sungbin5304/SimpleCodeEditor"><img alt="Title" src="https://img.shields.io/badge/Simple-EIDTOR-ff69b4"/></a>
  <a href="https://github.com/sungbin5304/SimpleCodeEditor"><img alt="Highlight" src="https://img.shields.io/badge/Highlight-JS-yellow"/></a>
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
<com.sungbin.texteditor.library.ui.SimpleCodeEditor
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/edit"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:sce_lineNumberColor="@color/colorPrimaryDark"
        app:sce_lineColor="@color/colorPrimaryDark"
        app:sce_selectLineColor="@color/colorPrimaryDark"/>
```

## all attribute
| Attribute | Description| Default |
| ------------- | ------------- | ------------- |
| `sce_lineColor` | Set line color | `Color.GRAY` |
| `tsce_lineNumberColor` | Set line number color | `Color.GRAY` |
| `sce_selectLineColor` | set focused line background color | `Color.CYAN` |
| `sce_applyHighlighter` | Set JavaScript Highlighter (It may cause freezing for long string) | `true` |
| `sce_readOnly` | Set editor read-only | `false` |
| `sce_reservedColor` | Set JavaScript Highlighter Reserved word color | `Color.argb(255, 21, 101, 192)` |
| `sce_numberColort` | Set JavaScript Highlighter Number color | `Color.argb(255, 191, 54, 12)` |
| `sce_stringColor` | Set JavaScript Highlighter String color | `Color.argb(255, 255, 160, 0)` |
| `sce_annotationColor` | Set JavaScript Highlighter Annotation color | `Color.argb(255, 139, 195, 74)` |

## all methods
```kotlin
.editor (get EditText from SimpleCodeEditor)
.applyHighlight = boolean
.editor.isEnabled = boolean (true is set editor Read-Only)

- undo()
- redo()
- findText(string: String, ignoreUpper: Boolean = false) (will return ArrayList<ArrayList<Int>>, Int ArrayList have LineNumber and Index value)
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
You can see License at [this page](https://github.com/sungbin5304/SimpleCodeEditor/blob/master/LICENSE).

# Thanks
Thanks [DarkTornado](https://github.com/DarkTornado). (supported [JavaScript Highlighter](https://github.com/DarkTornado/AndroidCodeHighlighter))

# Happy Coding :)
