/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\shengshoubo\\Desktop\\CoffeeMachine\\app\\src\\main\\aidl\\com\\netease\\vendor\\service\\IRemoteConn.aidl
 */
package com.netease.vendor.service;
public interface IRemoteConn extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.netease.vendor.service.IRemoteConn
{
private static final java.lang.String DESCRIPTOR = "com.netease.vendor.service.IRemoteConn";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.netease.vendor.service.IRemoteConn interface,
 * generating a proxy if needed.
 */
public static com.netease.vendor.service.IRemoteConn asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.netease.vendor.service.IRemoteConn))) {
return ((com.netease.vendor.service.IRemoteConn)iin);
}
return new com.netease.vendor.service.IRemoteConn.Stub.Proxy(obj);
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
case TRANSACTION_isTaskRunning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isTaskRunning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_stopRunningTask:
{
data.enforceInterface(DESCRIPTOR);
this.stopRunningTask();
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.netease.vendor.service.IRemoteConnCall _arg0;
_arg0 = com.netease.vendor.service.IRemoteConnCall.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.netease.vendor.service.IRemoteConnCall _arg0;
_arg0 = com.netease.vendor.service.IRemoteConnCall.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_send:
{
data.enforceInterface(DESCRIPTOR);
com.netease.vendor.service.Remote _arg0;
if ((0!=data.readInt())) {
_arg0 = com.netease.vendor.service.Remote.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.send(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.netease.vendor.service.IRemoteConn
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
@Override public boolean isTaskRunning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isTaskRunning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void stopRunningTask() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopRunningTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(com.netease.vendor.service.IRemoteConnCall remoteConn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((remoteConn!=null))?(remoteConn.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.netease.vendor.service.IRemoteConnCall remoteConn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((remoteConn!=null))?(remoteConn.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void send(com.netease.vendor.service.Remote remote) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((remote!=null)) {
_data.writeInt(1);
remote.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_send, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_isTaskRunning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stopRunningTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_send = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public boolean isTaskRunning() throws android.os.RemoteException;
public void stopRunningTask() throws android.os.RemoteException;
public void registerCallback(com.netease.vendor.service.IRemoteConnCall remoteConn) throws android.os.RemoteException;
public void unregisterCallback(com.netease.vendor.service.IRemoteConnCall remoteConn) throws android.os.RemoteException;
public void send(com.netease.vendor.service.Remote remote) throws android.os.RemoteException;
}
