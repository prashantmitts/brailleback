/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/prashant/Desktop/eyes-free/braille/client/src/com/googlecode/eyesfree/braille/translate/ITranslatorService.aidl
 */
package com.googlecode.eyesfree.braille.translate;
public interface ITranslatorService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.googlecode.eyesfree.braille.translate.ITranslatorService
{
private static final java.lang.String DESCRIPTOR = "com.googlecode.eyesfree.braille.translate.ITranslatorService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.googlecode.eyesfree.braille.translate.ITranslatorService interface,
 * generating a proxy if needed.
 */
public static com.googlecode.eyesfree.braille.translate.ITranslatorService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.googlecode.eyesfree.braille.translate.ITranslatorService))) {
return ((com.googlecode.eyesfree.braille.translate.ITranslatorService)iin);
}
return new com.googlecode.eyesfree.braille.translate.ITranslatorService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setCallback:
{
data.enforceInterface(DESCRIPTOR);
com.googlecode.eyesfree.braille.translate.ITranslatorServiceCallback _arg0;
_arg0 = com.googlecode.eyesfree.braille.translate.ITranslatorServiceCallback.Stub.asInterface(data.readStrongBinder());
this.setCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getTableInfos:
{
data.enforceInterface(DESCRIPTOR);
com.googlecode.eyesfree.braille.translate.TableInfo[] _result = this.getTableInfos();
reply.writeNoException();
reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
return true;
}
case TRANSACTION_checkTable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.checkTable(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_translate:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
com.googlecode.eyesfree.braille.translate.TranslationResult _result = this.translate(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_backTranslate:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _result = this.backTranslate(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.googlecode.eyesfree.braille.translate.ITranslatorService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Sets a callback to be called when the service is ready to translate.
     * Using any of the other methods in this interface before the
     * callback is called with a successful status will return
     * failure.
     */
@Override public void setCallback(com.googlecode.eyesfree.braille.translate.ITranslatorServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Returns a list of translation tables that are available on the system.
     */
@Override public com.googlecode.eyesfree.braille.translate.TableInfo[] getTableInfos() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.googlecode.eyesfree.braille.translate.TableInfo[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTableInfos, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArray(com.googlecode.eyesfree.braille.translate.TableInfo.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Makes sure that the given table id is valid and that the
     * table compiles.
     */
@Override public boolean checkTable(java.lang.String tableId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tableId);
mRemote.transact(Stub.TRANSACTION_checkTable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Translates {@code text} into braille according to the given
     * {@code tableId}.  {@code cursorPosition}, if non-negative, will be
     * mapped to the corresponding position in the translation result.
     * Returns null on fatal translation errors.
     */
@Override public com.googlecode.eyesfree.braille.translate.TranslationResult translate(java.lang.String text, java.lang.String tableId, int cursorPosition) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.googlecode.eyesfree.braille.translate.TranslationResult _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(text);
_data.writeString(tableId);
_data.writeInt(cursorPosition);
mRemote.transact(Stub.TRANSACTION_translate, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.googlecode.eyesfree.braille.translate.TranslationResult.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Translates braille cells into text according to the given table
     * id.  Returns null on fatal translation errors.
     */
@Override public java.lang.String backTranslate(byte[] cells, java.lang.String tableId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(cells);
_data.writeString(tableId);
mRemote.transact(Stub.TRANSACTION_backTranslate, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_setCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getTableInfos = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_checkTable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_translate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_backTranslate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
/**
     * Sets a callback to be called when the service is ready to translate.
     * Using any of the other methods in this interface before the
     * callback is called with a successful status will return
     * failure.
     */
public void setCallback(com.googlecode.eyesfree.braille.translate.ITranslatorServiceCallback callback) throws android.os.RemoteException;
/**
     * Returns a list of translation tables that are available on the system.
     */
public com.googlecode.eyesfree.braille.translate.TableInfo[] getTableInfos() throws android.os.RemoteException;
/**
     * Makes sure that the given table id is valid and that the
     * table compiles.
     */
public boolean checkTable(java.lang.String tableId) throws android.os.RemoteException;
/**
     * Translates {@code text} into braille according to the given
     * {@code tableId}.  {@code cursorPosition}, if non-negative, will be
     * mapped to the corresponding position in the translation result.
     * Returns null on fatal translation errors.
     */
public com.googlecode.eyesfree.braille.translate.TranslationResult translate(java.lang.String text, java.lang.String tableId, int cursorPosition) throws android.os.RemoteException;
/**
     * Translates braille cells into text according to the given table
     * id.  Returns null on fatal translation errors.
     */
public java.lang.String backTranslate(byte[] cells, java.lang.String tableId) throws android.os.RemoteException;
}
