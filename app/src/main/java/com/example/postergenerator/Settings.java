package com.example.postergenerator;

import android.net.Uri;

// static class which act as settings.
public class Settings
{
    public static boolean spaceOn = true;
    public static boolean lineOn = true;
    public static boolean cropOn = false;
    public static String backgroundColor = "black";
    public static String fileType = "jpeg";
    public static Uri photoUri = null; // used for both display and settings. Makes it easier to do than an intent due to the possibility of going from main -> presets -> settings
}//Settings
