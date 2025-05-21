### Introduction

Android diary app with a theme matching the diary app in popular anime movie *Your Name*

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

### Credits

The code is based on DaxiaK's [MyDiary](https://github.com/DaxiaK/MyDiary)
