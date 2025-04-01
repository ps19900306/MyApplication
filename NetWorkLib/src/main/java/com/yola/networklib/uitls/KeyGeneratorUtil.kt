package com.yola.networklib.uitls

import com.google.gson.Gson
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64;

object KeyGeneratorUtil {


    private const val PUBLIC_KEY: String =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuYUN1buCpcCWF4gtrGgtivVitCv+kr/nqkQTE3sp7sSJCsspAmUJR+jtbIT7eQ9+7SK87xxugrOVekrQEATkZxrG1z3Mv1e0CRX9MLdOMQqNYOwdHbBoUbOhO8CyJpm963LbRGI4e3saidjh6ndLNYKG9Vlg/X60M0tEJrI4NUCJuzoktvTLi72jkJoawCmmmH7mBBl6gMHRTczXWnr4kW13r9F11r5Bt3sK/ISuf0IKwq/sOA1IK3to+yDGnMUt6BlzeCL6sKCLftmzg4vIHBAsTcJBPrVehk3NBP5RHfuX1TDMuDSDWNPBZX/mzO34SQTwxyU6KMbZTheElVui3QIDAQAB"

    private val mGson by lazy { Gson() }


    private val md by lazy { MessageDigest.getInstance("MD5") }

    fun md5(input: String): String {
        return md.digest(input.toByteArray()).joinToString("") {
            "%02x".format(it)
        }
    }


    // 验签方法
    @Throws(Exception::class)
    fun verify(data: Any?, code: Int, signature: String): Boolean {
        if (data == null) {
            return verify(code.toString(), signature)
        } else if (data is String) {
            return verify(data + code, signature)
        } else {
            return verify(mGson.toJson(data) + code, signature)
        }
    }


    // 验签方法
    @Throws(Exception::class)
    fun verify(data: String, signature: String): Boolean {
        // 加载公钥
        val encodedKey: ByteArray = Base64.getDecoder().decode(PUBLIC_KEY)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeyObj = keyFactory.generatePublic(X509EncodedKeySpec(encodedKey))

        // 初始化Signature实例为验证模式
        val sig: Signature = Signature.getInstance("SHA256withRSA")
        sig.initVerify(publicKeyObj)

        // 更新待验证的数据
        sig.update(data.toByteArray())

        // 验证签名
        return sig.verify(Base64.getDecoder().decode(signature))
    }
}