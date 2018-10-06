#define LOG_TAG "HLHooker"

#include <jni.h>
#include <iostream>
#include <android/log.h>
#include <sys/stat.h>
#include <fstream>
#include <fcntl.h>
#include "include/AndHook.h"
#define LOGE(...) do { __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__); } while(0)
#define AKHook(X) AKHookFunction(reinterpret_cast<void *>(X), reinterpret_cast<void *>(my_##X), reinterpret_cast<void **>(&sys_##X));
using std::string;
using std::exception;
using std::ios_base;
//using std::basic_ifstream::open;
/**
 * Convert JString to C++ String
 * @param env
 * @param jStr
 * @return
 */
static string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    string ret = string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

/**
 * Convert C++ String to JString
 * @param env
 * @param cString
 * @return
 */
static jstring string2jstring(JNIEnv *env , string cString){
    const jclass strClass = env->FindClass("java/lang/String");
    const jmethodID constctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    const jstring encoding = env->NewStringUTF("GBK");

    jbyteArray bytes = env->NewByteArray(cString.length());
    env->SetByteArrayRegion(bytes, 0, cString.length(), (jbyte*)cString.c_str());
    jstring str = (jstring)env->NewObject(strClass, constctorID, bytes, encoding);
    return str;
}

void gen_random(char *s, const int len) {
    static const char alphanum[] =
            "0123456789"
            "abcdefghijklmnopqrstuvwxyz";

    for (int i = 0; i < len; ++i) {
        s[i] = alphanum[rand() % (sizeof(alphanum) - 1)];
    }
    s[len] = 0;
}

static const char *mapsFile = "/sdcard/HLDATA/maps";
static const char *cmdlineFile = "/sdcard/HLDATA/cmdline";
static const char *versionFile = "/sdcard/HLDATA/version";
static const char *cpuInfo = "/sdcard/HLDATA/cpuinfo";
static const char *myUUID = "/sdcard/HLDATA/uuid";
static const char *myMessage = "Hello World! Im a Cheater.";
static const char *myARM = "armv7l";
static const char *myABI = "armv7-a";
static int(*sys_access)(const char *pathname, int mode);
static int my_access(const char *pathname, int mode)
{
    try {
        string fPath(pathname);
        if((fPath.find("bin/su") != string::npos) || (fPath.find("xposed") != string::npos)){
            return -1;
        }
        if((fPath.find("Supersu") != string::npos) || (fPath.find("XposedBridge") != string::npos)){
            return -1;
        }
        if((fPath.find("Superuser") != string::npos) || (fPath.find("chainfire") != string::npos)){
            return -1;
        }
        if((fPath.find("Suser") != string::npos) || (fPath.find("vbox") != string::npos)){
            return -1;
        }
        if((fPath.find("superuser") != string::npos) || (fPath.find("_x86") != string::npos)){
            return -1;
        }
        return sys_access(pathname, mode);
    }catch(exception& e) {
        return sys_access(pathname, mode);
    }
}

static int(*sys_stat)(const char *pathname, const struct stat *buff);
static int my_stat(const char *pathname, const struct stat *buff){
    try {
        string fPath(pathname);
        if((fPath.find("bin/su") != string::npos) || (fPath.find("xposed") != string::npos)){
            return -1;
        }
        if((fPath.find("Supersu") != string::npos) || (fPath.find("XposedBridge") != string::npos)){
            return -1;
        }
        if((fPath.find("Superuser") != string::npos) || (fPath.find("chainfire") != string::npos)){
            return -1;
        }
        if((fPath.find("Suser") != string::npos) || (fPath.find("vbox") != string::npos)){
            return -1;
        }
        if((fPath.find("superuser") != string::npos) || (fPath.find("_x86") != string::npos)){
            return -1;
        }
        return sys_stat(pathname, buff);
    }catch(exception& e) {
        return sys_stat(pathname, buff);
    }
}

static int(*sys_lstat)(const char *pathname, const struct stat *buff);
static int my_lstat(const char *pathname, const struct stat *buff){
    try {
        string fPath(pathname);
        if((fPath.find("bin/su") != string::npos) || (fPath.find("xposed") != string::npos)){
            return -1;
        }
        if((fPath.find("Supersu") != string::npos) || (fPath.find("XposedBridge") != string::npos)){
            return -1;
        }
        if((fPath.find("Superuser") != string::npos) || (fPath.find("chainfire") != string::npos)){
            return -1;
        }
        if((fPath.find("Suser") != string::npos) || (fPath.find("vbox") != string::npos)){
            return -1;
        }
        if((fPath.find("superuser") != string::npos) || (fPath.find("_x86") != string::npos)){
            return -1;
        }
        return sys_lstat(pathname, buff);
    }catch(exception& e) {
        return sys_lstat(pathname, buff);
    }
}

static char* (*sys_strstr)(const char *mainstr, const char *substr);
static char* my_strstr(const char *mainstr, const char *substr){
    try {
        string fPath(mainstr);

        if(fPath.find("xposed") != string::npos){
            return sys_strstr(myMessage, substr);
        }
        return sys_strstr(mainstr, substr);
    }catch(exception& e) {
        return sys_strstr(mainstr, substr);
    }
}

static char* (*sys_bionic_strstr)(const char *mainstr, const char *substr);
static char* my_bionic_strstr(const char *mainstr, const char *substr){
    try {
        string fPath(mainstr);

        if(fPath.find("xposed") != string::npos){
            return sys_strstr(myMessage, substr);
        }
        return sys_strstr(mainstr, substr);
    }catch(exception& e) {
        return sys_strstr(mainstr, substr);
    }
}

static char* (*sys_strcasestr)(const char *mainstr, const char *substr);
static char* my_strcasestr(const char *mainstr, const char *substr){
    try {
        string fPath(mainstr);

        if(fPath.find("xposed") != string::npos){
            return sys_strcasestr(myMessage, substr);
        }

        return sys_strcasestr(mainstr, substr);
    }catch(exception& e) {
        return sys_strcasestr(mainstr, substr);
    }
}

static FILE* (*sys_fopen)(const char *pathname, const char *mode);
static FILE* my_fopen(const char *pathname, const char *mode){
    try {
        string fPath(pathname);

        if((fPath.find("/proc/") != string::npos) && (fPath.find("/maps") != string::npos)){
            return sys_fopen(mapsFile, mode);
        }
        if((fPath.find("/random/uuid") != string::npos) || (fPath.find("/random/boot_id") != string::npos)){
            return sys_fopen(myUUID, mode);
        }

        if((fPath.find("bin/su") != string::npos) || (fPath.find("xposed") != string::npos)){
            return NULL;
        }

        if((fPath.find("Supersu") != string::npos) || (fPath.find("XposedBridge") != string::npos)){
            return NULL;
        }
        if((fPath.find("Superuser") != string::npos) || (fPath.find("chainfire") != string::npos)){
            return NULL;
        }
        if((fPath.find("Suser") != string::npos) || (fPath.find("vbox") != string::npos)){
            return NULL;
        }
        if((fPath.find("superuser") != string::npos) || (fPath.find("_x86") != string::npos)){
            return NULL;
        }

        return sys_fopen(pathname, mode);
    }catch(exception& e) {
        return sys_fopen(pathname, mode);
    }
}

static int (*s_orig_open)(const char *path, int oflags, ... );
static int my_open(const char *path, int oflags, ... ) {
    va_list vl;
    mode_t mode;
    if (oflags & O_CREAT) {
        va_start(vl,oflags);
        mode = (mode_t) va_arg(vl,long);
        va_end(vl);
    }

    if ((path != NULL) && (strcmp (path, "/proc/self/cmdline") == 0)) { // THIS IS SUPER FUCKING IMPORTANT
        if (oflags & O_CREAT) {
            return s_orig_open(path, oflags, mode);
        }
        return s_orig_open(path, oflags);
    }

    try {
        string fPath(path);
        int temp_fd;

        if((fPath.find("/proc") != string::npos) && (fPath.find("/maps") != string::npos)){
            if (oflags & O_CREAT) {
                temp_fd = s_orig_open(mapsFile, oflags, mode);
            } else {
                temp_fd = s_orig_open(mapsFile, oflags);
            }
        }else if((fPath.find("/random/uuid") != string::npos) || (fPath.find("/random/boot_id") != string::npos)){
            if (oflags & O_CREAT) {
                temp_fd = s_orig_open(myUUID, oflags, mode);
            } else {
                temp_fd = s_orig_open(myUUID, oflags);
            }
        } else{
            if (oflags & O_CREAT) {
                return s_orig_open(path, oflags, mode);
            }
            return s_orig_open(path, oflags);
        }
        return temp_fd;
    }catch(exception& e) {
        if (oflags & O_CREAT) {
            return s_orig_open(path, oflags, mode);
        }
        return s_orig_open(path, oflags);
    }
}

static int (*sys_system_property_get)(const char* name, char* value);
static int my_system_property_get(const char* name, char* value){
    try {
        string fPath(name);

        if((fPath.find("su") != string::npos) || (fPath.find("supersu") != string::npos)){
            return 0;
        }

        if((fPath.find("serialno") != string::npos)){
            char fakeserialno[12];
            gen_random(fakeserialno, 12);
            strcpy(value, fakeserialno);
            return strlen(value);
        }
        return sys_system_property_get(name, value);
    }catch(exception& e) {
        return sys_system_property_get(name, value);
    }
}


static void doHook(){
    try {
        const void *image = AKGetImageByName("libc.so");
        if(image == NULL){
            return;
        }
        void *function = AKFindSymbol(image, "__system_property_get");
        if(function == NULL){
            return;
        }
        AKHookFunction(function, reinterpret_cast<void *>(my_system_property_get), reinterpret_cast<void **>(&sys_system_property_get));
        AKCloseImage(image);
    }catch (exception& e){
        LOGE("Hook System Property Get ERROR!!!");
    }
}

// ----------------------------------------------------------------------------

/*
 * This is called by the VM when the shared library is first loaded.
 */

typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    //UnionJNIEnvToVoid uenv;
    //uenv.venv = NULL;
    //jint result = -1;
    //JNIEnv* env = NULL;
    /*
    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("ERROR: GetEnv failed");
        goto bail;
    }
    */
    //env = uenv.env;
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_EVERSION;
    }

    try {
        AKHook(access);
        AKHook(fopen);
        AKHook(stat);
        AKHook(lstat);
        AKHookFunction(reinterpret_cast<void *>(open), reinterpret_cast<void *>(my_open), reinterpret_cast<void **>(&s_orig_open));
        doHook();
    }catch (exception& e){
        LOGE("AndHook ERROR!!!");
    }

    //result = JNI_VERSION_1_6;
    //bail:
    //return result;
    return JNI_VERSION_1_6;
}
