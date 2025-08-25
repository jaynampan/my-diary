### Introduction

Android diary app with a theme matching the diary app in popular anime movie *Your Name* based on
MyDiary project by DaxiaK which is unfortunately not maintained anymore. This project wants to offer
a recreation and enhancement of it with modern architecture and tech stack.

### Features

- Diary Writing
- Simple Memos
- Custom Themes

![Example Photo 1](screenshot/Screenshot_main.jpg)
![Example Photo 2](screenshot/Screenshot_diary.jpg)

### Progress & Todos

- [x] Migrate codes from Java to Kotlin
- [ ] Migrate to Compose
- [ ] Implement diary backing up

### Build Notes

- Used mirrors in settings.gradle.kts and gradle/wrapper/gradle-wrapper.properties
- In gradle.properties the maximum RAM is set to 4GB, adjust it if needed. And both gradle
  configuration and build cache are enable to speed up build process
- Java toolchain version is set to 21, you can adjust to jvm that's >= 11 and compatible with gradle
- Dependency updates plugin usage:
  ````bash
  ./gradlew dependencyUpdates
  ````
  
  or find it in Android Studio gradle panel: app > help > dependencyUpdates
  
  see [ben-manes/gradle-versions-plugin](https://github.com/ben-manes/gradle-versions-plugin) for detailed configuration.


### Credits

The code is based on DaxiaK's [MyDiary](https://github.com/DaxiaK/MyDiary)
