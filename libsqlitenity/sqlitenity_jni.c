#include "sqlitenity.h"

#include "jni.h"

#include <stdio.h>

#define JAVA(name) JNICALL Java_net_folivo_sqlitenity_raw_bindings_BindingsKt_##name
#define JAVACRITICAL(name) JavaCritical_net_folivo_sqlitenity_raw_bindings_BindingsKt_##name

#define MODE_WRITE 0
#define MODE_READ JNI_ABORT

#define CRITICAL(name, mode, fun) \
  ({ \
    void* c##name = (*env)->GetPrimitiveArrayCritical(env, name, NULL); \
    int32_t result = fun; \
    (*env)->ReleasePrimitiveArrayCritical(env, name, c##name, mode); \
    result; \
  })

#define CRITICAL2(name1, mode1, name2, mode2, fun) CRITICAL(name1, mode1, CRITICAL(name2, mode2, fun))

#ifdef __ANDROID__
  #define ARRAY(type, name) type* name
  #define METHOD(name) JAVACRITICAL(name)
  #define JAVA_API static
  #define JAVACRITICAL_API static
#else
  #define ARRAY(type, name) jint name##_size, type* name
  #define METHOD(name) JAVA(name)
  #define JAVA_API static
  #define JAVACRITICAL_API JNIEXPORT
#endif

#define ARG(type, cast) (type, cast)

#define ARG_GET_TYPE(type, cast) type
#define ARG_GET_CAST(type, cast) (cast)

#define TYPE(arg) ARG_GET_TYPE arg
#define CAST(arg) ARG_GET_CAST arg

#define WRAP_JNI1(ret_type, method_name, func_name, a1) \
    JAVA_API ret_type JAVA(method_name)(JNIEnv* env, jclass clazz, TYPE(a1) arg1) { \
        return (ret_type) func_name(CAST(a1) arg1); \
    } \
    JAVACRITICAL_API ret_type JAVACRITICAL(method_name)(TYPE(a1) arg1) { \
        return (ret_type) func_name(CAST(a1) arg1); \
    }

#define WRAP_JNI2(ret_type, method_name, func_name, a1, a2) \
    JAVA_API ret_type JAVA(method_name)(JNIEnv* env, jclass clazz, TYPE(a1) arg1, TYPE(a2) arg2) { \
        return (ret_type) func_name(CAST(a1) arg1, CAST(a2) arg2); \
    } \
    JAVACRITICAL_API ret_type JAVACRITICAL(method_name)(TYPE(a1) arg1, TYPE(a2) arg2) { \
        return (ret_type) func_name(CAST(a1) arg1, CAST(a2) arg2); \
    }

#define WRAP_JNI3(ret_type, method_name, func_name, a1, a2, a3) \
    JAVA_API ret_type JAVA(method_name)(JNIEnv* env, jclass clazz, TYPE(a1) arg1, TYPE(a2) arg2, TYPE(a3) arg3) { \
        return (ret_type) func_name(CAST(a1) arg1, CAST(a2) arg2, CAST(a3) arg3); \
    } \
    JAVACRITICAL_API ret_type JAVACRITICAL(method_name)(TYPE(a1) arg1, TYPE(a2) arg2, TYPE(a3) arg3) { \
        return (ret_type) func_name(CAST(a1) arg1, CAST(a2) arg2, CAST(a3) arg3); \
    }

WRAP_JNI1(jlong, sqlitenity_1errstr, sqlitenity_errstr, ARG(jint, int32_t))

WRAP_JNI1(jint, sqlitenity_1out_1of_1memory, sqlitenity_out_of_memory , ARG(jlong, sqlitenity_stmt*))
WRAP_JNI1(jint, sqlitenity_1no_1row        , sqlitenity_no_row        , ARG(jlong, sqlitenity_stmt*))
WRAP_JNI2(jint, sqlitenity_1invalid_1column, sqlitenity_invalid_column, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))

WRAP_JNI1(jint   , sqlitenity_1autocommit  , sqlitenity_autocommit  , ARG(jlong, sqlitenity*))
WRAP_JNI1(void   , sqlitenity_1close       , sqlitenity_close       , ARG(jlong, sqlitenity*))

WRAP_JNI1(jint, sqlitenity_1reset   , sqlitenity_reset   , ARG(jlong, sqlitenity_stmt*))
WRAP_JNI1(jint, sqlitenity_1step    , sqlitenity_step    , ARG(jlong, sqlitenity_stmt*))
WRAP_JNI1(jint, sqlitenity_1finalize, sqlitenity_finalize, ARG(jlong, sqlitenity_stmt*))

WRAP_JNI3(jint, sqlitenity_1bind_1double, sqlitenity_bind_double, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t), ARG(jdouble, double))
WRAP_JNI3(jint, sqlitenity_1bind_1long  , sqlitenity_bind_long  , ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t), ARG(jlong, int64_t))
WRAP_JNI2(jint, sqlitenity_1bind_1null  , sqlitenity_bind_null  , ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))

WRAP_JNI1(jint, sqlitenity_1clear_1bindings, sqlitenity_clear_bindings, ARG(jlong, sqlitenity_stmt*))
WRAP_JNI1(jint, sqlitenity_1column_1count  , sqlitenity_column_count  , ARG(jlong, sqlitenity_stmt*))

WRAP_JNI2(jdouble, sqlitenity_1column_1double, sqlitenity_column_double, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))
WRAP_JNI2(jlong  , sqlitenity_1column_1long  , sqlitenity_column_long  , ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))

WRAP_JNI2(jlong, sqlitenity_1column_1blob, sqlitenity_column_blob, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))
WRAP_JNI2(jlong, sqlitenity_1column_1text, sqlitenity_column_text, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))
WRAP_JNI2(jlong, sqlitenity_1column_1name, sqlitenity_column_name, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))

WRAP_JNI2(jint, sqlitenity_1column_1blob_1size, sqlitenity_column_blob_size, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))
WRAP_JNI2(jint, sqlitenity_1column_1text_1size, sqlitenity_column_text_size, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))

WRAP_JNI2(jint, sqlitenity_1column_1type, sqlitenity_column_type, ARG(jlong, sqlitenity_stmt*), ARG(jint, int32_t))

WRAP_JNI1(jint, sqlitenity_1strlen, sqlitenity_strlen, ARG(jlong, const char*))

// Custom implementations

JAVACRITICAL_API jint JAVACRITICAL(sqlitenity_1open)(ARRAY(jbyte, filename), ARRAY(jlong, ppConn)) {
  return sqlitenity_open((const char*) filename, (sqlitenity**) ppConn);
}

JAVA_API jint JAVA(sqlitenity_1open)(JNIEnv*env, jclass clazz, jbyteArray filename, jlongArray ppConn) {
  return CRITICAL2(filename, MODE_READ, ppConn, MODE_WRITE, (
     sqlitenity_open((const char *) cfilename, (sqlitenity**) cppConn)
  ));
}

JAVACRITICAL_API jint JAVACRITICAL(sqlitenity_1prepare)(jlong pConn, ARRAY(jchar, sql), int32_t size, ARRAY(jlong, ppStmt)) {
  return sqlitenity_prepare((sqlitenity *)pConn, (const void *)sql, size, (sqlitenity_stmt **)ppStmt);
}

JAVA_API jint JAVA(sqlitenity_1prepare)(JNIEnv *env, jclass clazz, jlong pConn, jcharArray sql, jint size, jlongArray ppStmt) {
  return CRITICAL2(sql, MODE_READ, ppStmt, MODE_WRITE, (
     sqlitenity_prepare((sqlitenity*)pConn, (const char *) csql, size, (sqlitenity_stmt**) cppStmt)
  ));
}

JAVACRITICAL_API jint JAVACRITICAL(sqlitenity_1bind_1blob)(jlong pStmt, jint iCol, ARRAY(jbyte, blob), jint size) {
  return sqlitenity_bind_blob((sqlitenity_stmt *)pStmt, iCol, (const void *)blob, size);
}

JAVA_API jint JAVA(sqlitenity_1bind_1blob)(JNIEnv* env, jclass clazz, jlong pStmt, jint iCol, jbyteArray blob, jint size) {
  return CRITICAL(blob, MODE_READ, (sqlitenity_bind_blob((sqlitenity_stmt *)pStmt, iCol, (const void *)cblob, size)));
}

JAVACRITICAL_API jint JAVACRITICAL(sqlitenity_1bind_1text)(jlong pStmt, jint iCol, ARRAY(jchar, text), jint size) {
  return sqlitenity_bind_text((sqlitenity_stmt *)pStmt, iCol, (const void *)text, size);
}

JAVA_API jint JAVA(sqlitenity_1bind_1text)(JNIEnv* env, jclass clazz, jlong pStmt, jint iCol, jcharArray text, jint size) {
  return CRITICAL(text, MODE_READ, (sqlitenity_bind_text((sqlitenity_stmt *)pStmt, iCol, (const void *)ctext, size)));
}

JAVACRITICAL_API void JAVACRITICAL(sqlitenity_1blob_1copy)(ARRAY(jbyte, dst), jlong src, jint size) {
  return sqlitenity_blob_copy((void *)dst, (const void *)src, size);
}

JAVA_API void JAVA(sqlitenity_1blob_1copy)(JNIEnv *env, jclass clazz, jbyteArray dst, jlong src, jint size) {
  CRITICAL(dst, MODE_WRITE, ({ sqlitenity_blob_copy(cdst, (const void *)src, size); 0; }));
}

JAVACRITICAL_API void JAVACRITICAL(sqlitenity_1text_1copy)(ARRAY(jchar, dst), jlong src, jint size) {
  return sqlitenity_text_copy((void *)dst, (const void *)src, size);
}

JAVA_API void JAVA(sqlitenity_1text_1copy)(JNIEnv *env, jclass clazz, jcharArray dst, jlong src, jint size) {
  CRITICAL(dst, MODE_WRITE, ({ sqlitenity_text_copy(cdst, (const void *)src, size); 0; }));
}

static const JNINativeMethod methods[] = {
    { "sqlitenity_errstr",         "(I)J",     (void*) METHOD(sqlitenity_1errstr) },
    { "sqlitenity_out_of_memory",  "(J)I",     (void*) METHOD(sqlitenity_1out_1of_1memory) },
    { "sqlitenity_no_row",         "(J)I",     (void*) METHOD(sqlitenity_1no_1row) },
    { "sqlitenity_invalid_column", "(JI)I",    (void*) METHOD(sqlitenity_1invalid_1column) },

    { "sqlitenity_open",           "([B[J)I", (void*) JAVA(sqlitenity_1open) },
    { "sqlitenity_autocommit",     "(J)I",    (void*) JAVA(sqlitenity_1autocommit) },
    { "sqlitenity_prepare",        "(J[CI[J)I", (void*) JAVA(sqlitenity_1prepare) },
    { "sqlitenity_close",          "(J)V",     (void*) METHOD(sqlitenity_1close) },

    { "sqlitenity_reset",          "(J)I",     (void*) METHOD(sqlitenity_1reset) },
    { "sqlitenity_step",           "(J)I",     (void*) METHOD(sqlitenity_1step) },
    { "sqlitenity_finalize",       "(J)I",     (void*) METHOD(sqlitenity_1finalize) },

    { "sqlitenity_bind_blob",      "(JI[BI)I", (void*) JAVA(sqlitenity_1bind_1blob) },
    { "sqlitenity_bind_double",    "(JID)I",   (void*) METHOD(sqlitenity_1bind_1double) },
    { "sqlitenity_bind_long",      "(JIJ)I",   (void*) METHOD(sqlitenity_1bind_1long) },
    { "sqlitenity_bind_null",      "(JI)I",    (void*) METHOD(sqlitenity_1bind_1null) },
    { "sqlitenity_bind_text",      "(JI[CI)I", (void*) JAVA(sqlitenity_1bind_1text) },

    { "sqlitenity_clear_bindings", "(J)I",     (void*) METHOD(sqlitenity_1clear_1bindings) },
    { "sqlitenity_column_count",   "(J)I",     (void*) METHOD(sqlitenity_1column_1count) },

    { "sqlitenity_column_double",  "(JI)D",    (void*) METHOD(sqlitenity_1column_1double) },
    { "sqlitenity_column_long",    "(JI)J",    (void*) METHOD(sqlitenity_1column_1long) },
    { "sqlitenity_column_blob",    "(JI)J",    (void*) METHOD(sqlitenity_1column_1blob) },
    { "sqlitenity_column_text",    "(JI)J",    (void*) METHOD(sqlitenity_1column_1text) },

    { "sqlitenity_column_blob_size", "(JI)I",  (void*) METHOD(sqlitenity_1column_1blob_1size) },
    { "sqlitenity_column_text_size", "(JI)I",  (void*) METHOD(sqlitenity_1column_1text_1size) },

    { "sqlitenity_column_name",    "(JI)J",    (void*) METHOD(sqlitenity_1column_1name) },
    { "sqlitenity_column_type",    "(JI)I",    (void*) METHOD(sqlitenity_1column_1type) },

    { "sqlitenity_blob_copy",       "([BJI)V",(void*) JAVA(sqlitenity_1blob_1copy) },
    { "sqlitenity_text_copy",       "([CJI)V",(void*) JAVA(sqlitenity_1text_1copy) },

    { "sqlitenity_strlen",          "(J)I",   (void*) METHOD(sqlitenity_1strlen) },
};


static jstring getSystemProperty(JNIEnv* env, const char* name) {
    jclass systemClass = (*env)->FindClass(env, "java/lang/System");
    if (systemClass == NULL) return NULL;

    jmethodID getProperty = (*env)->GetStaticMethodID(
        env, systemClass, "getProperty",
        "(Ljava/lang/String;)Ljava/lang/String;");
    if (getProperty == NULL) return NULL;

    jstring jname = (*env)->NewStringUTF(env, name);
    return (jstring)(*env)->CallStaticObjectMethod(env, systemClass, getProperty, jname);
}


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  JNIEnv *env;
  jstring jClazzName;
  const char* clazzName;
  jclass clazz;
  jint rc;

  if ((*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_6) != JNI_OK)
    return JNI_ERR;

  if ((jClazzName = getSystemProperty(env, "sqlitenity.class")) == NULL)
    return JNI_ERR;

  if ((clazzName = (*env)->GetStringUTFChars(env, jClazzName, NULL)) == NULL)
    return JNI_ERR;

  if ((clazz = (*env)->FindClass(
           env, clazzName)) == NULL)
    return JNI_ERR;
  
  (*env)->ReleaseStringUTFChars(env, jClazzName, clazzName);

  rc = (*env)->RegisterNatives(env, clazz, methods,
                               sizeof(methods) / sizeof(JNINativeMethod));
  (*env)->DeleteLocalRef(env, clazz);

  if (rc != 0)
    return JNI_ERR;

  return JNI_VERSION_1_6;
}


