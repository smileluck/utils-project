package main

import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"fmt"
)

// AEC/CBC/PKCS7Padding

func main() {
	key := "" // 长度为 16、 24 或 32
	iv := ""                  // 长度固定为 aes.BlockSize ，16位

	s := ``

	result, err := Encrypt(s, key, iv)
	if err != nil {
		fmt.Print(err)
	}

	fmt.Println(result)

	result2, err2 := EncryptSHA2562(result)
	if err2 != nil {
		fmt.Print(err2)
	}
	fmt.Print(result2)

	// raw, err := Decrypt(result, key, iv)
	// if err != nil {
	// fmt.Print(err)
	// }

	// fmt.Println(raw)
}

// Encrypt 加密
//
// plainText: 加密目标字符串
// key: 加密Key
// iv: 加密iv(AES时固定为16位)
func Encrypt(plainText string, key string, iv string) (string, error) {
	data, err := aesCBCEncrypt([]byte(plainText), []byte(key), []byte(iv))
	if err != nil {
		return "", err
	}

	return base64.StdEncoding.EncodeToString(data), nil
}

// Decrypt 解密
//
// cipherText: 解密目标字符串
// key: 加密Key
// iv: 加密iv(AES时固定为16位)
func Decrypt(cipherText string, key string, iv string) (string, error) {
	data, err := base64.StdEncoding.DecodeString(cipherText)
	if err != nil {
		return "", err
	}

	dnData, err := aesCBCDecrypt(data, []byte(key), []byte(iv))
	if err != nil {
		return "", err
	}

	return string(dnData), nil
}

// aesCBCEncrypt AES/CBC/PKCS7Padding 加密
func aesCBCEncrypt(plaintext []byte, key []byte, iv []byte) ([]byte, error) {
	// AES
	block, err := aes.NewCipher(key)
	if err != nil {
		panic(err)
	}

	// PKCS7 填充
	plaintext = paddingPKCS7(plaintext, aes.BlockSize)

	// CBC 加密
	mode := cipher.NewCBCEncrypter(block, iv)
	mode.CryptBlocks(plaintext, plaintext)

	return plaintext, nil
}

// aesCBCDecrypt AES/CBC/PKCS7Padding 解密
func aesCBCDecrypt(ciphertext []byte, key []byte, iv []byte) ([]byte, error) {
	// AES
	block, err := aes.NewCipher(key)
	if err != nil {
		panic(err)
	}

	if len(ciphertext)%aes.BlockSize != 0 {
		panic("ciphertext is not a multiple of the block size")
	}

	// CBC 解密
	mode := cipher.NewCBCDecrypter(block, iv)
	mode.CryptBlocks(ciphertext, ciphertext)

	// PKCS7 反填充
	result := unPaddingPKCS7(ciphertext)
	return result, nil
}

// PKCS7 填充
func paddingPKCS7(plaintext []byte, blockSize int) []byte {
	paddingSize := blockSize - len(plaintext)%blockSize
	paddingText := bytes.Repeat([]byte{byte(paddingSize)}, paddingSize)
	return append(plaintext, paddingText...)
}

// PKCS7 反填充
func unPaddingPKCS7(s []byte) []byte {
	length := len(s)
	if length == 0 {
		return s
	}
	unPadding := int(s[length-1])
	return s[:(length - unPadding)]
}

// EncryptSHA256 hashes the given data with a secret using SHA256
func EncryptSHA2562(data string) (string, error) {
	// Combine data and secret
	// combinedData := data + secret

	// Create a new SHA256 hash object
	hash := sha256.New()

	// Write combined data to the hash
	hash.Write([]byte(data))

	// Return the hexadecimal-encoded hash
	return hex.EncodeToString(hash.Sum(nil)), nil
}
