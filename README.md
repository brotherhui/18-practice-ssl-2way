
This is used to test 2 ways SSL by adding the root CA certification into server side (trust store)and add sub CA certification in to client side(key store).
To make sure if client can access server without adding the sub CA certification into server


#### Try
In this way, anyone can access http://localhost:8080/door?uri=https://localhost:8443/user

#### 2 level CA
--rootCA
--ca1 (issued by rootCA)
#### Steps 1: generate a certificate - rootca
> 
keytool -genkeypair -alias rootca -keyalg RSA -dname "CN=www.root.com,OU=company,O=Development,L=China,S=dalian,C=CN"  -keypass s3cr3t -keystore .\server.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 2: it is self signed certificate. So let's call it rootca, export it as rootca.cer
>
keytool -exportcert -alias rootca -file .\rootca.cer -keystore .\server.jks -storepass s3cr3t 
>
#### Steps 3: generate a new certificate for client - ca1
> 
keytool -genkeypair -alias ca1 -keyalg RSA -dname "CN=www.1.com,OU=company,O=Development,L=China,S=dalian,C=CN"  -keypass s3cr3t -keystore .\client.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 4: get a signer request for ca1
>
keytool -certreq -alias ca1 -file ca1.csr   -keypass s3cr3t -keystore .\client.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 5: sign it with rootca, so we get the certificate - ca1
>
keytool -gencert -alias rootca -infile ca1.csr -outfile ca1.cer   -keypass s3cr3t -keystore .\server.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 6: import the certficate - rootca.cer into server-trust.jks
>
keytool -importcert -alias rootca -file rootca.cer  -keypass s3cr3t -keystore .\server-trust.jks -storepass s3cr3t  -storepass s3cr3t  -noprompt
>
#### Steps 7: import the certficate - rootca.cer into client-trust.jks
>
keytool -importcert -alias rootca -file rootca.cer  -keypass s3cr3t -keystore .\client-trust.jks -storepass s3cr3t  -storepass s3cr3t  -noprompt
>
#### Steps 8: import the certficate - ca1.cer into server-trust.jks
>
keytool -importcert -alias ca1 -file ca1.cer  -keypass s3cr3t -keystore .\server-trust.jks -storepass s3cr3t  -storepass s3cr3t  -noprompt
>

#### Conclusion
You can see that, we must add all the sub ca from the client into server-trust when client-auth is set to NEED. only set rootCA into server-trust store will not work.


#### 3 level CA
--rootca2
--ca2 (issued by rootca2)
--clientca2 (issued by ca2)


#### Steps 1: generate a certificate - rootca
> 
keytool -genkeypair -alias rootca2 -keyalg RSA -dname "CN=www.root.com,OU=company,O=Development,L=China,S=dalian,C=CN"  -keypass s3cr3t -keystore .\server-3level.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 2: it is self signed certificate. So let's call it rootca2, export it as rootca2.cer file
>
keytool -exportcert -alias rootca2 -file .\rootca2.cer -keystore .\server-3level.jks -storepass s3cr3t 
>
#### Steps 3: generate a new certificate for client - ca2
> 
keytool -genkeypair -alias ca2 -keyalg RSA -dname "CN=www.2.com,OU=company,O=Development,L=China,S=dalian,C=CN"  -keypass s3cr3t -keystore .\client-inter.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 4: get a signer request for ca2
>
keytool -certreq -alias ca2 -file ca2.csr   -keypass s3cr3t -keystore .\client-inter.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 5: sign it with rootca, so we get the certificate - ca2, get the ca2.cer file
>
keytool -gencert -infile ca2.csr -outfile ca2.cer   -keypass s3cr3t -keystore .\server-3level.jks -storepass s3cr3t -alias rootca2 
>
#### Steps 3: generate a new certificate for client - clientca2
> 
keytool -genkeypair -alias clientca2 -keyalg RSA -dname "CN=www.client.com,OU=company,O=Development,L=China,S=dalian,C=CN"  -keypass s3cr3t -keystore .\client-3level.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 4: get a signer request for ca2
>
keytool -certreq -alias clientca2 -file clientca2.csr   -keypass s3cr3t -keystore .\client-3level.jks -storepass s3cr3t  -storepass s3cr3t
>
#### Steps 5: sign it with ca1, so we get the certificate - clientca1, get the clientca2.cer file
>
keytool -gencert -infile clientca2.csr -outfile clientca2.cer  -keypass s3cr3t -keystore .\client-inter.jks -storepass s3cr3t  -alias ca2
>



#### Steps 6: import the rootca2.cer into server-3level-trust.jks
>
keytool -importcert -alias rootca2 -file rootca2.cer  -keypass s3cr3t -keystore .\server-3level-trust.jks -storepass s3cr3t -noprompt
>
#### Steps 6: import the ca2.cer into server-3level-trust.jks
>
keytool -importcert -alias ca2 -file ca2.cer  -keypass s3cr3t -keystore .\server-3level-trust.jks -storepass s3cr3t   -noprompt
>
#### Steps 7: import the certficate - rootca2.cer into client-3level-trust.jks
>
keytool -importcert -alias rootca2 -file rootca2.cer  -keypass s3cr3t -keystore .\client-3level-trust.jks -storepass s3cr3t   -noprompt
>

#### Steps 7: import the certficate - ca2.cer into client-3level-trust.jks
>
keytool -importcert -alias ca2 -file ca2.cer  -keypass s3cr3t -keystore .\client-3level-trust.jks -storepass s3cr3t  -noprompt
>


#### Steps 7: import the certficate - clientca2.cer into client-3level.jks
>
keytool -importcert -alias clientca2 -file clientca2.cer  -keypass s3cr3t -keystore .\client-3level-final.jks -storepass s3cr3t  -noprompt
>



#### Conclusion
3level also not work. only if we add the 3rd level ca into truststore
