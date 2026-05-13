/**
 * @Author: 
 * @Date: 2024-11-27 13:30:42
 * @LastEditors: 
 * @LastEditTime: 2024-11-27 13:30:44
 * @FilePath: C:/Users/chamodha kulananda/AndroidStudioProjects/DB_LuxeVista_Resort/app/src/androidTest/java/com/example/db_luxevista_resort/ExampleInstrumentedTest.java
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.db_luxevista_resort;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.db_luxevista_resort", appContext.getPackageName());
    }
}