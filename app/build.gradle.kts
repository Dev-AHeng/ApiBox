plugins {
    alias(libs.plugins.androidApplication)

    id("com.google.dagger.hilt.android")
}

android {
    namespace = "my.zjh.box"
    compileSdk = 35

    // 资源前缀防止冲突
    resourcePrefix = "app_"

    defaultConfig {
        applicationId = "my.zjh.box"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
//                arguments["AROUTER_MODULE_NAME"] = "app"  // 确保使用正确的模块名
                arguments["AROUTER_MODULE_NAME"] = project.name

            }
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true

        // Gradle 插件 7.0.0 开始，出于性能优化的考虑，默认情况下不再自动生成 BuildConfig 类。这是为了加快构建速度
        buildConfig = true
    }

    buildTypes {
        debug {
            isDebuggable = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isDebuggable = false // 确保在 Release 模式下 debuggable 为 false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    // 添加用户模块依赖
    implementation(project(":common"))
    implementation(project(":module_user"))
    implementation(project(":model_sanhaiapi"))
    implementation(project(":model_guiguiapi"))

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ARouter依赖
    implementation(libs.arouter.api)
    annotationProcessor(libs.arouter.compiler)

//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
//    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:2.12")

    // Hilt依赖项注入
    implementation("com.google.dagger:hilt-android:2.51.1")
    annotationProcessor("com.google.dagger:hilt-android-compiler:2.51.1")
}


hilt {
    enableExperimentalClasspathAggregation = true
    enableAggregatingTask = false
}