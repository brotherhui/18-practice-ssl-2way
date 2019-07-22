please read https://community.developer.visa.com/t5/Developer-Tools/What-is-Mutual-Authentication/ba-p/5757

#Mutual Authentication 
```
This is used to test 2 ways SSL by adding the root CA certification into server side (trust store)and add sub CA certification in to client side(key store).
To make sure if client can access server without adding the sub CA certification into server

please refer to ![Alt text](https://github.com/brotherhui/18-practice-ssl-rootca/blob/master/SSL-2ways.jpg?raw=true)

<p>
1. rootca sign intermediate
2. intermediate sign thirdlevel
3. put only rootca and intermediate into server truststore
4. use thirdlevel in client's keystore
</p>

#### Try
In this way, anyone can access http://localhost:8080/door?uri=https://localhost:8443/user


#### Conclusion
```
3 level 2ways ssl is working fine with this 
Type | Client | Server
Keystore | thirdlevel | rootca
truststore | rootca pubkey | rootca pubkey and intermediate pubkey 
```

#### 根证书的制作
//all password is changeit
<pre><code>
1. 生成一个私钥， 这个私钥被保存在pem格式的文件中， pem格式的文件还可以直接保存证书
mkdir private 
openssl genrsa -aes256 -out private/rootca.key.pem 2048

2、利用这个私钥生成证书签发申请（ca.csr）
openssl req -new -key private/rootca.key.pem -out private/rootca.csr -subj "/C=CN/ST=LiaoNing/L=DL/O=company/OU=center/CN=*.brotherhui.com"
req          产生证书签发申请命令
-new         表示新请求
-key         密钥,这里为private/rootca.key.pem文件
-out         输出路径,这里为private/rootca.csr文件
-subj        指定用户信息。这里使用泛域名"*.brotherhui.com"
得到根证书签发申请文件后，我们可以将其发生给CA机构签发，当然我们也可以自行签发根证书。

3、签发根证书（自行签发根证书, 自签证书）, 生成一个证书文件， 证书中会包含公钥
mkdir certs
openssl x509 -req -days 10000 -sha1 -extensions v3_ca -signkey private/rootca.key.pem -in private/rootca.csr -out certs/rootca.cer -CAcreateserial
x509        签发X.509格式证书命令。
-req        表示证书输入请求。
-days       表示有效天数,这里为10000天。
-shal       表示证书摘要算法,这里为SHA1算法。
-extensions 表示按OpenSSL配置文件v3_ca项添加扩展。
-signkey    表示自签名密钥,这里为private/rootca.key.pem。
-in         表示输入文件,这里为private/rootca.csr。
-out        表示输出文件,这里为certs/rootca.cer。




#### 中间证书的制作
1. 生成中间证书
openssl genrsa -aes256 -out private/intermediate.key.pem 2048

2、生成中间证书签发申请（intermediate.csr）
openssl req -new -key private/intermediate.key.pem -out private/intermediate.csr -subj "/C=CN/ST=LiaoNing/L=DL/O=company/OU=center/CN=intermediate.brotherhui.com"

req          产生证书签发申请命令
-new         表示新请求
-key         密钥,这里为private/intermediate.key.pem文件
-out         输出路径,这里为private/intermediate.csr文件
-subj        指定用户信息。这里使用中间域名"intermediate.brotherhui.com"
得到根证书签发申请文件后，我们可以将其发生给CA机构签发，当然我们也可以自行签发根证书。

3、签发中间证书（根证书签发）, 相当于公钥
openssl x509 -req -days 10000 -sha1 -extensions v3_ca -in private/intermediate.csr -out certs/intermediate.cer -CA certs/rootca.cer -CAkey private/rootca.key.pem  -CAcreateserial 
x509        签发X.509格式证书命令。
-req        表示证书输入请求。
-days       表示有效天数,这里为10000天。
-shal       表示证书摘要算法,这里为SHA1算法。
-extensions 表示按OpenSSL配置文件v3_ca项添加扩展。
-signkey    表示自签名密钥,这里为private/rootca.key.pem。
-in         表示输入文件,这里为private/intermediate.csr。
-out        表示输出文件,这里为certs/intermediate.cer。
-CA         表示签名用的证书
-CAkey      表示签名用的证书的key



#### 三级证书的制作
1. 生成三级证书
openssl genrsa -aes256 -out private/thirdlevel.key.pem 2048

2、生成三级证书签发申请（thirdlevel.csr）
openssl req -new -key private/thirdlevel.key.pem -out private/thirdlevel.csr -subj "/C=CN/ST=LiaoNing/L=DL/O=company/OU=center/CN=thirdlevel.intermediate.brotherhui.com"
req          产生证书签发申请命令
-new         表示新请求
-key         密钥,这里为private/thirdlevel.key.pem文件
-out         输出路径,这里为private/thirdlevel.csr文件
-subj        指定用户信息。这里使用中间域名"thirdlevel.intermediate.brotherhui.com"
得到根证书签发申请文件后，我们可以将其发生给CA机构签发，当然我们也可以自行签发根证书。

3、签发三级证书（中间证书签发）, 相当于公钥
openssl x509 -req -days 10000 -sha1 -extensions v3_ca -in private/thirdlevel.csr -out certs/thirdlevel.cer -CA certs/intermediate.cer -CAkey private/intermediate.key.pem  -CAcreateserial 
x509        签发X.509格式证书命令。
-req        表示证书输入请求。
-days       表示有效天数,这里为10000天。
-shal       表示证书摘要算法,这里为SHA1算法。
-extensions 表示按OpenSSL配置文件v3_ca项添加扩展。
-in         表示输入文件,这里为private/thirdlevel.csr。
-out        表示输出文件,这里为certs/thirdlevel.cer。



#### keystore, truststore制作
目标， 实现2 way ssl (mutual auth), 需要将rootca和intermediate的证书链放到服务器端的truststore, 而不将thirdlevel的证书放入从而达到客户端可以2 wayssl的目的

1. Server-keystore 如果要以根证书提供服务， 将其转化为keystore（转化为PKCS#12编码格式）
mkdir keystore
openssl pkcs12 -export -inkey private/rootca.key.pem -in certs/rootca.cer -out keystore/server-keystore.p12
pkcs12          PKCS#12编码格式证书命令。
-export         表示导出证书。
-cacerts        表示仅导出CA证书。
-inkey          表示输入密钥,这里为private/rootca.key.pem
-in             表示输入文件,这里为certs/rootca.cer
-out            表示输出文件,这里为certs/rootca.p12
个人信息交换文件（PKCS#12） 可以作为密钥库或信任库使用，我们可以通过KeyTool查看密钥库的详细信息。


2、查看密钥库信息， 这个密钥库中刚生成的时候可以保存的是私钥和证书对priviateKeyEntry. 请注意， 这种方式生成的p12文件， 跟使用keygenpair直接生成的jks格式的密钥库文件性质是一样的 只是keygenpair一次性生成jks文件并保存密钥和证书对。 openssl的方式是先生成密钥， 然后证书然后倒入到密钥库。
keytool -list -keystore keystore/server-keystore.p12 -storetype pkcs12 -v -storepass changeit
注意，这里参数-storetype值为“pkcs12”。
我们已经构建了根证书（rootca.cer）,我们可以使用根证书签发服务器证书和客户证书。
>
Keystore type: PKCS12
Keystore provider: SunJSSE
Your keystore contains 1 entry
Alias name: 1
Creation date: Feb 16, 2018
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=*.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Issuer: CN=*.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Serial number: a018f913d62f450d
Valid from: Fri Feb 16 10:21:25 CST 2018 until: Tue Jul 04 10:21:25 CST 2045
Certificate fingerprints:
         MD5:  AD:31:BA:A1:E5:B3:F9:41:32:4C:97:6C:D7:38:07:B0
         SHA1: AB:17:28:03:80:63:96:E7:0B:B0:56:23:E9:2B:78:7B:D4:EE:33:28
         SHA256: A1:06:2B:C6:0E:AB:EA:9D:3D:26:D8:B5:FA:71:41:F1:89:B3:06:2D:82:8A:C0:2F:CE:2B:C2:22:30:D8:99:45
         Signature algorithm name: SHA1withRSA
         Version: 1
*******************************************
*******************************************
>


3. Client-TrustStore 如果要以根证书提供服务端服务， 如果是client端双向ssl访问的话， 需要把根证书放到client端的client truststore。 这里的证书都是trustedCertEntry
keytool -importcert -alias 1 -file certs/rootca.cer -keypass changeit -keystore keystore/client-truststore.jks -storepass changeit -noprompt


4. Client-Keystore 只是用thirdlevel的clientkeystore
openssl pkcs12 -export  -aes256  -inkey private/intermediate.key.pem -in certs/intermediate.cer -out keystore/intermediate-keystore.p12
keytool -list -keystore keystore/intermediate-keystore.p12 -storetype pkcs12 -v -storepass changeit

>
Keystore type: PKCS12
Keystore provider: SunJSSE
Your keystore contains 1 entry
Alias name: 1
Creation date: Feb 16, 2018
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=intermediate.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Issuer: CN=*.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Serial number: a13aee4a9cd9e4cf
Valid from: Fri Feb 16 13:22:25 CST 2018 until: Tue Jul 04 13:22:25 CST 2045
Certificate fingerprints:
         MD5:  AD:F6:0E:47:7B:41:93:3D:9D:89:CD:52:4B:15:B9:82
         SHA1: A2:99:A9:F3:99:41:AA:A8:6D:2E:E0:AC:91:31:E4:4A:FA:48:9C:3C
         SHA256: BF:26:7E:7D:DC:BE:C4:C9:F1:BB:7E:77:29:A1:CC:FA:61:B5:96:4B:55:EF:45:81:41:73:AB:78:21:F0:A3:BA
         Signature algorithm name: SHA1withRSA
         Version: 1
*******************************************
>

openssl pkcs12 -export  -aes256  -inkey private/thirdlevel.key.pem -in certs/thirdlevel.cer -out keystore/client-keystore.p12
keytool -list -keystore keystore/client-keystore.p12 -storetype pkcs12 -v -storepass changeit
>
Keystore type: PKCS12
Keystore provider: SunJSSE
Your keystore contains 1 entry
Alias name: 1
Creation date: Feb 16, 2018
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=thirdlevel.intermediate.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Issuer: CN=intermediate.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Serial number: 8cdc5d74877a8bfe
Valid from: Fri Feb 16 13:22:58 CST 2018 until: Tue Jul 04 13:22:58 CST 2045
Certificate fingerprints:
         MD5:  DD:51:AA:A2:4B:3D:01:82:90:D5:95:4E:E8:F7:F3:E3
         SHA1: 2D:49:26:8C:3B:BC:4C:A3:E7:C8:0B:AA:4C:1F:D0:B8:BA:55:C7:01
         SHA256: C0:43:0D:AF:A7:FF:AB:B2:58:4B:66:9B:A7:D7:7F:3E:14:F5:B6:7E:DA:9C:DA:E2:CF:87:AF:B5:43:84:BC:97
         Signature algorithm name: SHA1withRSA
         Version: 1
*******************************************
>

5. Server-TrustStore 只保存rootca和intermediate的证书
keytool -importcert -alias rootca -file certs/rootca.cer -keypass changeit -keystore keystore/server-truststore.jks -storepass changeit -noprompt
keytool -importcert -alias intermediate -file certs/intermediate.cer -keypass changeit -keystore keystore/server-truststore.jks -storepass changeit -noprompt

keytool -list -keystore keystore/server-truststore.jks -storetype jks -v -storepass changeit

>
Keystore type: JKS
Keystore provider: SUN
Your keystore contains 2 entries
Alias name: rootca
Creation date: Feb 16, 2018
Entry type: trustedCertEntry
Owner: CN=*.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Issuer: CN=*.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Serial number: 89d1ae8240361070
Valid from: Fri Feb 16 13:21:51 CST 2018 until: Tue Jul 04 13:21:51 CST 2045
Certificate fingerprints:
         MD5:  D4:DD:BD:FB:B0:18:5C:5E:6B:53:19:E2:1B:0F:C3:F3
         SHA1: B3:8B:42:ED:FB:BA:B9:3E:E7:1A:88:75:55:88:90:D0:BF:E4:87:8A
         SHA256: 64:89:EB:87:51:E0:5F:8D:F8:51:93:F4:58:E5:EF:01:8A:1C:2F:44:6A:4B:65:4C:CD:EA:F8:CB:05:8E:6C:44
         Signature algorithm name: SHA1withRSA
         Version: 1
*******************************************
*******************************************
Alias name: intermediate
Creation date: Feb 16, 2018
Entry type: trustedCertEntry
Owner: CN=intermediate.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Issuer: CN=*.brotherhui.com, OU=center, O=company, L=DL, ST=LiaoNing, C=CN
Serial number: a13aee4a9cd9e4cf
Valid from: Fri Feb 16 13:22:25 CST 2018 until: Tue Jul 04 13:22:25 CST 2045
Certificate fingerprints:
         MD5:  AD:F6:0E:47:7B:41:93:3D:9D:89:CD:52:4B:15:B9:82
         SHA1: A2:99:A9:F3:99:41:AA:A8:6D:2E:E0:AC:91:31:E4:4A:FA:48:9C:3C
         SHA256: BF:26:7E:7D:DC:BE:C4:C9:F1:BB:7E:77:29:A1:CC:FA:61:B5:96:4B:55:EF:45:81:41:73:AB:78:21:F0:A3:BA
         Signature algorithm name: SHA1withRSA
         Version: 1
*******************************************
*******************************************
>


