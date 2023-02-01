// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("hands");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("hands")
//      }
//    }

#include <iostream>
#include <jni.h>

extern "C" JNIEXPORT jdouble JNICALL
Java_com_hands_utils_VectorUtils_dotProductWrap(JNIEnv *env, jclass clazz, jdoubleArray v1,
                                                jdoubleArray v2) {
    //return dot product of v1 and v2
    jdouble *v1Array = env->GetDoubleArrayElements(v1, nullptr);
    jdouble *v2Array = env->GetDoubleArrayElements(v2, nullptr);
    jsize v1Length = env->GetArrayLength(v1);
    jsize v2Length = env->GetArrayLength(v2);
    jdouble result = 0;
    if (v1Length == v2Length) {
        for (int i = 0; i < v1Length; i++) {
            result += v1Array[i] * v2Array[i];
        }
    }
    return result;
}

extern "C" JNIEXPORT jdouble JNICALL
Java_com_hands_utils_VectorUtils_distanceToLineWrap(JNIEnv *env, jclass clazz, jdoubleArray p,
                                                    jdoubleArray p1, jdoubleArray p2) {
    //return distance from p to line p1-p2
    jdouble *pArray = env->GetDoubleArrayElements(p, nullptr);
    jdouble *p1Array = env->GetDoubleArrayElements(p1, nullptr);
    jdouble *p2Array = env->GetDoubleArrayElements(p2, nullptr);
    jsize vLength = env->GetArrayLength(p);
    jsize v1Length = env->GetArrayLength(p1);
    jsize v2Length = env->GetArrayLength(p2);
    jdouble result = 0;
    if (vLength == v1Length && v1Length == v2Length) {
        jdouble *v1 = new jdouble[v2Length];
        jdouble *v2 = new jdouble[v2Length];
        for (int i = 0; i < v2Length; i++) {
            v1[i] = p2Array[i] - p1Array[i];
            v2[i] = pArray[i] - p1Array[i];
        }

        jdoubleArray v1Array = env->NewDoubleArray(v2Length);
        jdoubleArray v2Array = env->NewDoubleArray(v2Length);
        env->SetDoubleArrayRegion(v1Array, 0, v2Length, v1);
        env->SetDoubleArrayRegion(v2Array, 0, v2Length, v2);
        jdouble scalar =
                Java_com_hands_utils_VectorUtils_dotProductWrap(env, clazz, v1Array, v2Array) /
                Java_com_hands_utils_VectorUtils_dotProductWrap(env, clazz, v1Array, v1Array);

        jdouble *projection = new jdouble[v2Length];
        for (int i = 0; i < v2Length; i++) {
            projection[i] = scalar * v1[i];
        }

        jdouble *distanceVector = new jdouble[v2Length];
        for (int i = 0; i < v2Length; i++) {
            distanceVector[i] = v2[i] - projection[i];
        }

        jdouble distance = 0;
        for (int i = 0; i < v2Length; i++) {
            distance += distanceVector[i] * distanceVector[i];
        }

        distance = sqrt(distance);

        result = round(distance * 10000000.0) / 10000000.0;
    }
    return result;
}
// getDistance x, y, z
extern "C" JNIEXPORT jdouble JNICALL
Java_com_hands_utils_Utils_get3DistanceWrap(JNIEnv *env, jclass clazz, jdoubleArray v1,
                                            jdoubleArray v2) {

    // v1 = x1, y1, z1
    // v2 = x2, y2, z2
    jdouble *v1Array = env->GetDoubleArrayElements(v1, nullptr);
    jdouble *v2Array = env->GetDoubleArrayElements(v2, nullptr);
    jsize v1Length = env->GetArrayLength(v1);
    jsize v2Length = env->GetArrayLength(v2);
    jdouble result = 0;
    if (v1Length == v2Length) {
        result = sqrt(pow(v1Array[0] - v2Array[0], 2) + pow(v1Array[1] - v2Array[1], 2) +
                      pow(v1Array[2] - v2Array[2], 2));
    }
    return result;
}

// getDistance x, y
extern "C" JNIEXPORT jdouble JNICALL
Java_com_hands_utils_Utils_get2DistanceWrap(JNIEnv *env, jclass clazz, jdoubleArray v1,
                                            jdoubleArray v2) {

    // v1 = x1, y1
    // v2 = x2, y2
    jdouble *v1Array = env->GetDoubleArrayElements(v1, nullptr);
    jdouble *v2Array = env->GetDoubleArrayElements(v2, nullptr);
    jsize v1Length = env->GetArrayLength(v1);
    jsize v2Length = env->GetArrayLength(v2);
    jdouble result = 0;
    if (v1Length == v2Length) {
        result = sqrt(pow(v1Array[0] - v2Array[0], 2) + pow(v1Array[1] - v2Array[1], 2));
    }
    return result;
}

// isBetween value, low, high
extern "C" JNIEXPORT jboolean JNICALL
Java_com_hands_utils_Utils_isBetweenIntWrap(JNIEnv *env, jclass clazz,
                                            jint value,
                                            jint low,
                                            jint high) {

    return value >= low && value <= high;
}

// indiceMedioAlti riceve un array di double (coordinate Y dei 21 landmarks)
extern "C" JNIEXPORT jboolean JNICALL
Java_com_hands_utils_Utils_indiceMedioAltiWrap(JNIEnv *env, jclass clazz, jdoubleArray y) {

    jdouble *yArray = env->GetDoubleArrayElements(y, nullptr);
    jsize yLength = env->GetArrayLength(y);
    for (int i = 0; i <= 20; i++) {
        if (i == 8 || i == 7 || i == 6 || i == 12 || i == 11 || i == 10) continue;

        // + la y è piccola, + è verso l'alto
        if (yArray[6] > yArray[i] || yArray[7] > yArray[i] || yArray[8] > yArray[i] ||
            yArray[10] > yArray[i] || yArray[11] > yArray[i] || yArray[12] > yArray[i]) {
            return false;
        }
    }
    return true;
}
