package com.example.livechatapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


// @HiltAndroidApp is a Hilt-specific annotation that triggers Hilt's code generation
// Isko Humne Application Level Pr Initialize kr dia basically Application mainActivity wala h
@HiltAndroidApp
class LCApplication : Application()