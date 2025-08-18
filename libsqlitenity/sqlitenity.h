#ifndef SQLITENITY_H
#define SQLITENITY_H

#ifndef SQLITENITY_API
  #define SQLITENITY_API __attribute__((always_inline))
#endif

#include <stdint.h>

typedef struct sqlite3      sqlitenity;
typedef struct sqlite3_stmt sqlitenity_stmt;

SQLITENITY_API const char *sqlitenity_errstr  (int errcode);

SQLITENITY_API int32_t sqlitenity_out_of_memory  (sqlitenity_stmt *pStmt);
SQLITENITY_API int32_t sqlitenity_no_row         (sqlitenity_stmt *pStmt);
SQLITENITY_API int32_t sqlitenity_invalid_column (sqlitenity_stmt *pStmt, int iCol);

SQLITENITY_API int32_t sqlitenity_open (const char *filename, sqlitenity **ppConn);

SQLITENITY_API int32_t sqlitenity_prepare    (sqlitenity *pConn, const void *sql, int32_t size, sqlitenity_stmt **ppStmt);
SQLITENITY_API int32_t sqlitenity_autocommit (sqlitenity *pConn);
SQLITENITY_API void    sqlitenity_close      (sqlitenity *pConn);

SQLITENITY_API int32_t sqlitenity_reset    (sqlitenity_stmt *pStmt);
SQLITENITY_API int32_t sqlitenity_step     (sqlitenity_stmt *pStmt);
SQLITENITY_API int32_t sqlitenity_finalize (sqlitenity_stmt *pStmt);

SQLITENITY_API int32_t sqlitenity_bind_blob   (sqlitenity_stmt *pStmt, int32_t iCol, const void *blob, int32_t size);
SQLITENITY_API int32_t sqlitenity_bind_double (sqlitenity_stmt *pStmt, int32_t iCol, double real);
SQLITENITY_API int32_t sqlitenity_bind_long   (sqlitenity_stmt *pStmt, int32_t iCol, int64_t integer);
SQLITENITY_API int32_t sqlitenity_bind_null   (sqlitenity_stmt *pStmt, int32_t iCol);
SQLITENITY_API int32_t sqlitenity_bind_text   (sqlitenity_stmt *pStmt, int32_t iCol, const void *text, int32_t size);

SQLITENITY_API int32_t     sqlitenity_clear_bindings (sqlitenity_stmt *pStmt);
SQLITENITY_API int32_t     sqlitenity_column_count   (sqlitenity_stmt *pStmt);

SQLITENITY_API double      sqlitenity_column_double (sqlitenity_stmt *pStmt, int32_t iCol);
SQLITENITY_API int64_t     sqlitenity_column_long   (sqlitenity_stmt *pStmt, int32_t iCol);
SQLITENITY_API const void* sqlitenity_column_blob   (sqlitenity_stmt *pStmt, int32_t iCol);
SQLITENITY_API const void* sqlitenity_column_text   (sqlitenity_stmt *pStmt, int32_t iCol);

SQLITENITY_API int32_t     sqlitenity_column_blob_size (sqlitenity_stmt *pStmt, int32_t iCol);
SQLITENITY_API int32_t     sqlitenity_column_text_size (sqlitenity_stmt *pStmt, int32_t iCol);

SQLITENITY_API const void* sqlitenity_column_name   (sqlitenity_stmt *pStmt, int32_t iCol);
SQLITENITY_API int32_t     sqlitenity_column_type   (sqlitenity_stmt *pStmt, int32_t iCol);

SQLITENITY_API void sqlitenity_blob_copy (void* dst, const void* src, int32_t size);
SQLITENITY_API void sqlitenity_text_copy (void* dst, const void* src, int32_t size);

SQLITENITY_API int32_t sqlitenity_strlen (const char* string);

#endif // SQLITENITY_H
