#include "sqlite3.h"

#include "sqlitenity.h"
#include <string.h>

SQLITENITY_API const char *sqlitenity_errstr(int errcode) {
  return sqlite3_errstr(errcode);
}

SQLITENITY_API int32_t sqlitenity_out_of_memory(sqlitenity_stmt *pStmt) {
  return sqlite3_errcode(sqlite3_db_handle(pStmt)) == SQLITE_NOMEM;
}

SQLITENITY_API int32_t sqlitenity_no_row(sqlitenity_stmt *pStmt) {
  return sqlite3_stmt_busy(pStmt) == 0;
}

SQLITENITY_API int32_t sqlitenity_invalid_column(sqlitenity_stmt *pStmt,
                                                 int iCol) {
  return iCol < 0 || iCol >= sqlite3_column_count(pStmt);
}

SQLITENITY_API int32_t sqlitenity_open(const char *filename,
                                       sqlitenity **ppConn) {
  return sqlite3_open_v2(filename, ppConn,
                         SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE, NULL);
}

SQLITENITY_API int32_t sqlitenity_autocommit (sqlitenity *pConn) {
  return sqlite3_get_autocommit(pConn);
}

SQLITENITY_API int32_t sqlitenity_prepare(sqlitenity *pConn, const void *sql,
                                          int32_t size,
                                          sqlitenity_stmt **ppStmt) {
  return sqlite3_prepare16_v2(pConn, sql, size, ppStmt, NULL);
}

SQLITENITY_API void sqlitenity_close(sqlitenity *pConn) {
  sqlite3_close_v2(pConn);
}

SQLITENITY_API int32_t sqlitenity_reset(sqlitenity_stmt *pStmt) {
  return sqlite3_reset(pStmt);
}

SQLITENITY_API int32_t sqlitenity_step(sqlitenity_stmt *pStmt) {
  return sqlite3_step(pStmt);
}

SQLITENITY_API int32_t sqlitenity_finalize(sqlitenity_stmt *pStmt) {
  return sqlite3_finalize(pStmt);
}

SQLITENITY_API int32_t sqlitenity_bind_blob(sqlitenity_stmt *pStmt,
                                            int32_t iCol, const void *blob,
                                            int32_t size) {
  return sqlite3_bind_blob(pStmt, iCol, blob, size, SQLITE_TRANSIENT);
}

SQLITENITY_API int32_t sqlitenity_bind_double(sqlitenity_stmt *pStmt,
                                              int32_t iCol, double real) {
  return sqlite3_bind_double(pStmt, iCol, real);
}

SQLITENITY_API int32_t sqlitenity_bind_long(sqlitenity_stmt *pStmt,
                                            int32_t iCol, int64_t integer) {
  return sqlite3_bind_int64(pStmt, iCol, integer);
}

SQLITENITY_API int32_t sqlitenity_bind_null(sqlitenity_stmt *pStmt,
                                            int32_t iCol) {
  return sqlite3_bind_null(pStmt, iCol);
}

SQLITENITY_API int32_t sqlitenity_bind_text(sqlitenity_stmt *pStmt,
                                            int32_t iCol, const void *text,
                                            int32_t size) {
  return sqlite3_bind_text16(pStmt, iCol, text, size, SQLITE_TRANSIENT);
}

SQLITENITY_API int32_t sqlitenity_clear_bindings(sqlitenity_stmt *pStmt) {
  return sqlite3_clear_bindings(pStmt);
}

SQLITENITY_API int32_t sqlitenity_column_count(sqlitenity_stmt *pStmt) {
  return sqlite3_column_count(pStmt);
}

SQLITENITY_API double sqlitenity_column_double(sqlitenity_stmt *pStmt,
                                               int32_t iCol) {
  return sqlite3_column_double(pStmt, iCol);
}

SQLITENITY_API int64_t sqlitenity_column_long(sqlitenity_stmt *pStmt,
                                              int32_t iCol) {
  return sqlite3_column_int64(pStmt, iCol);
}

SQLITENITY_API const void *sqlitenity_column_blob(sqlitenity_stmt *pStmt,
                                                  int32_t iCol) {
  return sqlite3_column_blob(pStmt, iCol);
}

SQLITENITY_API const void *sqlitenity_column_text(sqlitenity_stmt *pStmt,
                                                  int32_t iCol) {
  return sqlite3_column_text16(pStmt, iCol);
}

SQLITENITY_API int32_t sqlitenity_column_blob_size(sqlitenity_stmt *pStmt,
                                                   int32_t iCol) {
  return sqlite3_column_bytes(pStmt, iCol);
}

SQLITENITY_API int32_t sqlitenity_column_text_size(sqlitenity_stmt *pStmt,
                                                   int32_t iCol) {
  return sqlite3_column_bytes16(pStmt, iCol);
}

SQLITENITY_API const void *sqlitenity_column_name(sqlitenity_stmt *pStmt,
                                                  int32_t iCol) {
  return sqlite3_column_name(pStmt, iCol);
}

SQLITENITY_API int32_t sqlitenity_column_type(sqlitenity_stmt *pStmt,
                                              int32_t iCol) {
  return sqlite3_column_type(pStmt, iCol);
}

SQLITENITY_API void sqlitenity_blob_copy(void *dst, const void *src,
                                         int32_t size) {
  memcpy(dst, src, size);
}

SQLITENITY_API void sqlitenity_text_copy(void *dst, const void *src,
                                         int32_t size) {
  memcpy(dst, src, size);
}

SQLITENITY_API int32_t sqlitenity_strlen(const char *string) {
  size_t size = strlen(string);

  if (size > 0x7FFFFFFF)
    return -1;

  return (int32_t)size;
}
