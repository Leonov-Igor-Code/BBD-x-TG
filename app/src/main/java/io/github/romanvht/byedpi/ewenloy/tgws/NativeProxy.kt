package io.github.romanvht.byedpi.ewenloy.tgws

import android.util.Log
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer

interface ProxyLibrary : Library {
    companion object {
        val INSTANCE = Native.load("tgwsproxy", ProxyLibrary::class.java) as ProxyLibrary
    }

    fun StartProxy(host: String, port: Int, dcIps: String, secret: String, verbose: Int): Int
    fun StopProxy(): Int
    fun SetPoolSize(size: Int)
    fun SetBufferSizeKb(kb: Int)
    fun SetCfProxyCacheDir(cacheDir: String)
    fun SetCfProxyConfig(enabled: Int, priority: Int, userDomain: String)
    fun SetCfWorkerDomains(domains: String)
    fun SetFakeTlsDomain(domain: String)
    fun SetDisableSecure(v: Int)
    fun SetForceTestDc(v: Int)
    fun SetProxyProtocol(v: Int)
    fun SetSecret(secret: String)
    fun GetSecretWithPrefix(): Pointer?
    fun GetStats(): Pointer?
    fun FreeString(p: Pointer)
}

object NativeProxy {
    private const val TAG = "NativeProxy"

    fun startProxy(host: String, port: Int, dcIps: String, secret: String, verbose: Int): Int {
        return ProxyLibrary.INSTANCE.StartProxy(host, port, dcIps, secret, verbose)
    }

    fun stopProxy(): Int {
        return ProxyLibrary.INSTANCE.StopProxy()
    }

    fun setPoolSize(size: Int) {
        ProxyLibrary.INSTANCE.SetPoolSize(size)
    }

    fun setCfProxyCacheDir(cacheDir: String) {
        ProxyLibrary.INSTANCE.SetCfProxyCacheDir(cacheDir)
    }

    fun setCfProxyConfig(enabled: Boolean, priority: Boolean, userDomain: String) {
        ProxyLibrary.INSTANCE.SetCfProxyConfig(
            if (enabled) 1 else 0,
            if (priority) 1 else 0,
            userDomain
        )
    }

    /**
     * Сеттеры движка из синхронизации с апстримом tg-ws-proxy (2026-10).
     * Пока в APK старая libtgwsproxy.so без этих символов — вызов
     * пропускается с записью в лог; после пересборки .so начинают работать.
     */
    private inline fun optionalSet(name: String, call: () -> Unit) {
        try {
            call()
        } catch (e: UnsatisfiedLinkError) {
            Log.d(TAG, "native $name unavailable (old libtgwsproxy.so?), skipped")
        }
    }

    /** Размер SO_RCVBUF/SO_SNDBUF в КБ (0 — не менять, диапазон 4..4096). */
    fun setBufferSizeKb(kb: Int) = optionalSet("SetBufferSizeKb") {
        ProxyLibrary.INSTANCE.SetBufferSizeKb(kb)
    }

    /** Свои Cloudflare Worker-домены (через запятую/пробел/;, пусто — авто). */
    fun setCfWorkerDomains(domains: String) = optionalSet("SetCfWorkerDomains") {
        ProxyLibrary.INSTANCE.SetCfWorkerDomains(domains)
    }

    /** Домен FakeTLS; пустая строка отключает FakeTLS (секрет остаётся dd). */
    fun setFakeTlsDomain(domain: String) = optionalSet("SetFakeTlsDomain") {
        ProxyLibrary.INSTANCE.SetFakeTlsDomain(domain)
    }

    /** Порт 80 вместо 443 для CF proxy/worker — обход блокировки 443 в мобильных сетях. */
    fun setDisableSecure(disable: Boolean) = optionalSet("SetDisableSecure") {
        ProxyLibrary.INSTANCE.SetDisableSecure(if (disable) 1 else 0)
    }

    fun setForceTestDc(force: Boolean) = optionalSet("SetForceTestDc") {
        ProxyLibrary.INSTANCE.SetForceTestDc(if (force) 1 else 0)
    }

    fun setProxyProtocol(enable: Boolean) = optionalSet("SetProxyProtocol") {
        ProxyLibrary.INSTANCE.SetProxyProtocol(if (enable) 1 else 0)
    }

    fun setSecret(secret: String) = optionalSet("SetSecret") {
        ProxyLibrary.INSTANCE.SetSecret(secret)
    }

    /** Returns the full secret with correct prefix (dd or ee+domain_hex) */
    fun getSecretWithPrefix(): String? {
        val ptr = ProxyLibrary.INSTANCE.GetSecretWithPrefix() ?: return null
        val res = ptr.getString(0)
        ProxyLibrary.INSTANCE.FreeString(ptr)
        return res
    }

    fun getStats(): String? {
        val ptr = ProxyLibrary.INSTANCE.GetStats() ?: return null
        val res = ptr.getString(0)
        ProxyLibrary.INSTANCE.FreeString(ptr)
        return res
    }
}
