
This is used to test 2 ways SSL by adding the root CA certification into server side (trust store)and add sub CA certification in to client side(key store).
To make sure if client can access server without adding the sub CA certification into server


#### Try
In this way, anyone can access http://localhost:8080/door?uri=https://localhost:8443/user


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
You can see that, we must add all the sub ca from the client into server-trust