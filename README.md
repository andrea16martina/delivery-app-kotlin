# Delivery App – Kotlin

Native Android application for real-time food delivery and restaurant browsing. Built with Kotlin and Jetpack Compose.

## Overview

The app provides an intuitive UI for exploring the 20 nearest restaurants, each displayed as a card with image, description, and action buttons.

Key features:

- Restaurant List – view nearby restaurants with dynamic data
- Restaurant Detail – review menu items and finalize orders
- User Profile – manage account information
- Live Map Tracking – track order location in real time using MapBox

## Tech Stack

- Kotlin  
- Jetpack Compose  
- MapBox SDK (live tracking)  
- LiveData & Coroutines  
- Navigation Component  
- Android Architecture Components (ViewModel, Repository pattern)


## Local setup

Requirements: Android Studio (latest), Kotlin, Gradle

Clone the repository:

```bash
git clone https://github.com/andrea16martina/delivery-app-kotlin.git
cd delivery-app-kotlin
```

Open the project in Android Studio and run on an emulator or physical device.

Make sure to:
- Obtain a MapBox access token and configure it in local.properties or AndroidManifest.xml
-  Use the latest Android Studio version

## Suggested next steps

-Add unit and UI tests for critical features
- Implement offline caching for restaurant data
- Add notifications for order updates
- Optimize MapBox integration for battery efficiency

## Contact & License

Author: [Andrea Martina](https://andreamartina.vercel.app) 
License: MIT

