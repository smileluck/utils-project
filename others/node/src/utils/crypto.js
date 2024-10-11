const crypto = require('crypto')
const CryptoJS = require('crypto-js')


/**
 * MD5加密
 * @param text 明文
 * @param secret 密钥
 * @return
 */
function encryptByMd5(text, secret) {
    let hash;
    const algorithm = 'md5';

    if (secret) {
        hash = crypto.createHmac(algorithm, secret).update(text).digest('hex');
    } else {
        hash = crypto.createHash(algorithm).update(text).digest('hex');
    }

    return hash;
}

/**
 * AES/ECB/PKCS7Padding加密
 * @param content 接口参数拼接URI, key1=value1&key2=value2
 * @param secretKey 密钥
 * @param iv 偏移量
 * @return
 */
function encryptByAESPKCS7(content, secretKey, iv) {
    iv = CryptoJS.enc.Utf8.parse(iv);
    content = CryptoJS.enc.Utf8.parse(content);
    secretKey = CryptoJS.enc.Utf8.parse(secretKey);

    const ciphertext = CryptoJS.AES.encrypt(content, secretKey, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
    });


    // console.log(11, ciphertext.toString())

    return ciphertext.toString();
    // return encryptByMd5(ciphertext.toString());
}

/**
 * AES/ECB/PKCS7Padding解密
 * @param content 密文
 * @param secretKey 秘钥
 * @param iv 偏移量
 * @return
 */
function decryptByAESPKCS7(content, secretKey, iv) {
    iv = CryptoJS.enc.Utf8.parse(iv);
    secretKey = CryptoJS.enc.Utf8.parse(secretKey);

    const ciphertext = CryptoJS.AES.decrypt(content, secretKey, {
        iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
    });

    return ciphertext.toString(CryptoJS.enc.Utf8);
}


/**
 * SHA256计算
 * @param {*} text 
 * @returns 
 */
function sha256(text) {
    return CryptoJS.SHA256(text).toString(CryptoJS.enc.Hex)
    // return CJsha256(text).toString(CryptoJS.enc.Utf8)
}

module.exports = { encryptByAESPKCS7, sha256 }

function strToByteArray(str) {
    var array = new Uint8Array(str.length);
    for (var i = 0; i < str.length; i++) {
        array[i] = str.charCodeAt(i);
    }
    return array;
}

function bytesToHexString(bytes) {
    return Array.from(bytes).map(b => ('00' + b.toString(16)).slice(-2)).join('');
}