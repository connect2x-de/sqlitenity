package de.connect2x.sqlitenity.interop

const val SQLITE_OK = 0
const val SQLITE_MISUSE = 21
const val SQLITE_RANGE = 25
const val SQLITE_ROW = 100
const val SQLITE_DONE = 101

const val SQLITE_INTEGER = 1
const val SQLITE_FLOAT = 2
const val SQLITE_TEXT = 3
const val SQLITE_BLOB = 4
const val SQLITE_NULL = 5

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_errstr")
@ExternalSymbolName("sqlitenity_errstr")
external fun errStr(errcode: Int): NativePointer

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_out_of_memory")
@ExternalSymbolName("sqlitenity_out_of_memory")
external fun outOfMemory(pStmt: NativePointer): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_no_row")
@ExternalSymbolName("sqlitenity_no_row")
external fun noRow(pStmt: NativePointer): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_invalid_column")
@ExternalSymbolName("sqlitenity_invalid_column")
external fun invalidColumn(pStmt: NativePointer, iCol: Int): Int

@FastNative
@ModuleImport("sqlitenity", "sqlitenity_open")
@ExternalSymbolName("sqlitenity_open")
external fun open(filename: ByteArrayPointer, ppConn: NativePointerPointer): Int

@FastNative
@ModuleImport("sqlitenity", "sqlitenity_prepare")
@ExternalSymbolName("sqlitenity_prepare")
external fun prepare(
    pConn: NativePointer,
    sql: CharArrayPointer,
    size: Int,
    ppStmt: NativePointerPointer,
): Int

@FastNative
@ModuleImport("sqlitenity", "sqlitenity_autocommit")
@ExternalSymbolName("sqlitenity_autocommit")
external fun autoCommit(pConn: NativePointer): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_close")
@ExternalSymbolName("sqlitenity_close")
external fun close(pConn: NativePointer)

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_reset")
@ExternalSymbolName("sqlitenity_reset")
external fun reset(pStmt: NativePointer): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_step")
@ExternalSymbolName("sqlitenity_step")
external fun step(pStmt: NativePointer): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_finalize")
@ExternalSymbolName("sqlitenity_finalize")
external fun finalize(pStmt: NativePointer): Int

@FastNative
@ModuleImport("sqlitenity", "sqlitenity_bind_blob")
@ExternalSymbolName("sqlitenity_bind_blob")
external fun bindBlob(pStmt: NativePointer, iCol: Int, blob: ByteArrayPointer, size: Int): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_bind_double")
@ExternalSymbolName("sqlitenity_bind_double")
external fun bindDouble(pStmt: NativePointer, iCol: Int, real: Double): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_bind_long")
@ExternalSymbolName("sqlitenity_bind_long")
external fun bindLong(pStmt: NativePointer, iCol: Int, integer: Long): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_bind_null")
@ExternalSymbolName("sqlitenity_bind_null")
external fun bindNull(pStmt: NativePointer, iCol: Int): Int

@FastNative
@ModuleImport("sqlitenity", "sqlitenity_bind_text")
@ExternalSymbolName("sqlitenity_bind_text")
external fun bindText(pStmt: NativePointer, iCol: Int, text: CharArrayPointer, size: Int): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_clear_bindings")
@ExternalSymbolName("sqlitenity_clear_bindings")
external fun clearBindings(pStmt: NativePointer): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_count")
@ExternalSymbolName("sqlitenity_column_count")
external fun columnCount(pStmt: NativePointer): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_double")
@ExternalSymbolName("sqlitenity_column_double")
external fun columnDouble(pStmt: NativePointer, iCol: Int): Double

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_long")
@ExternalSymbolName("sqlitenity_column_long")
external fun columnLong(pStmt: NativePointer, iCol: Int): Long

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_blob")
@ExternalSymbolName("sqlitenity_column_blob")
external fun columnBlob(pStmt: NativePointer, iCol: Int): NativePointer

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_text")
@ExternalSymbolName("sqlitenity_column_text")
external fun columnText(pStmt: NativePointer, iCol: Int): NativePointer

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_blob_size")
@ExternalSymbolName("sqlitenity_column_blob_size")
external fun columnBlobSize(pStmt: NativePointer, iCol: Int): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_text_size")
@ExternalSymbolName("sqlitenity_column_text_size")
external fun columnTextSize(pStmt: NativePointer, iCol: Int): Int

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_name")
@ExternalSymbolName("sqlitenity_column_name")
external fun columnName(pStmt: NativePointer, iCol: Int): NativePointer

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_column_type")
@ExternalSymbolName("sqlitenity_column_type")
external fun columnType(pStmt: NativePointer, iCol: Int): Int

@FastNative
@ModuleImport("sqlitenity", "sqlitenity_blob_copy")
@ExternalSymbolName("sqlitenity_blob_copy")
external fun blobCopy(dst: ByteArrayPointer, src: NativePointer, size: Int)

@FastNative
@ModuleImport("sqlitenity", "sqlitenity_text_copy")
@ExternalSymbolName("sqlitenity_text_copy")
external fun textCopy(dst: CharArrayPointer, src: NativePointer, size: Int)

@CriticalNative
@ModuleImport("sqlitenity", "sqlitenity_strlen")
@ExternalSymbolName("sqlitenity_strlen")
external fun strLen(pText: NativePointer): Int
