# spring-kafka-basics
Project that exemplifies how to produce and consume Kafka messages with Spring Boot

# Locally enabling ssl connection to Kafka
- For this tutorial, consider the following informations:
-- Directory where we're working in: '/c/Kafka/ssl'
-- We need to download the openssl configuration available in the following link: https://code.google.com/archive/p/openssl-for-windows/downloads and unizp it in c/openssl/ directory. 
-- The Keystore password will be password
-- The PEM pass phrase will be passwordpem
-- The client truststore password will be password

## Generating the KeyStore
- The below command is to generate the **keyStore**.
```
keytool -keystore server.keystore.jks -alias localhost -validity 365 -genkey -keyalg RSA
```
- Enter the keystore password [password]

## Generating CA
- The below command will generate the ca cert(SSL cert) and private key. This is normally needed if we are self signing the request.
```
..\..\'Program Files'\Git\usr\bin\openssl.exe req -new -x509 -keyout ca-key -out ca-cert -days 365 -config "/c/openssl/openssl.cnf" -subj "/CN=local-security-CA"
```
- Enter the PEM pass phrase [passwordpem]

## Generating Certificate Signing Request(CSR)
- The below command will create a cert-file as a result of executing the command.
```
keytool -keystore server.keystore.jks -alias localhost -certreq -file cert-file
```
- Enter the keystore password
	
## Signing the certificate
- The below command takes care of signing the CSR and then it spits out a file cert-signed
```
..\..\'Program Files'\Git\usr\bin\openssl.exe x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial -passin pass:passwordpem
```

- To view the content inside the file cert-signed, run the below command.
```
keytool -printcert -v -file cert-signed
```

## Adding the Signed Cert in to the KeyStore file
- First, adding the ca-cert file
```
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert
```
- Type 'yes' to trust the certificate


- Now, adding the cert-signed file

```
keytool -keystore server.keystore.jks -alias localhost -import -file cert-signed
```
- Type 'yes' to trust the certificate

## Verify the content of server.keystore.jks file
```
keytool -list -v -keystore server.keystore.jks
```
Verify that your keystore contains 2 entries

## Adding the SSL cert into our Kafka Cluster
- For this, we need to update the server.properties file
```
vim config/server.properties
```

- With the following content:
```
ssl.keystore.location=C:\\Kafka\\ssl\\server.keystore.jks
ssl.keystore.password=password
ssl.key.password=password
ssl.endpoint.identification.algorithm=
```

- And in the 'listeners' variable, add the SSL host:
```
listeners=PLAINTEXT://localhost:9092, SSL://localhost:9095
```

## Accessing SSL Enabled Topics using Console Producers/Consumers [2WayAuthentication]
- The below command takes care of generating the truststore for us and adds the CA-Cert in to it.

-- Enable the client authentication at the cluster end by generating the server.truststore.jks
```
keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert
```
-- Enter the server truststore password [password]
-- - Type 'yes' to trust the certificate

-- Enable the server authentication at the client end by generating the client.truststore.jks
```
keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
```
-- Enter the client truststore password [password]
-- Type 'yes' to trust the certificate


- Add the **ssl.client.auth** property in the **server.properties** file.

```
ssl.truststore.location=C:\\Kafka\\ssl\\server.truststore.jks
ssl.truststore.password=password
ssl.client.auth=required
```

-- Create a client.keystore.jks. Hint: It can be the same as the server.keystore.jks, just changing the name
```
cp server.keystore.jks client.keystore.jks
```

- Now it's just needed to add the following informations in the application.yml file:

-- consumer:
```
spring:
  kafka:
    consumer:
	  # other configs
      ssl:
        trust-store-location: file:C:\\Kafka\\ssl\\client.truststore.jks
        trust-store-password: password
        key-store-location: file:C:\\Kafka\\ssl\\client.keystore.jks
        key-store-password: password
      properties:
        protocol: SSL
        security:
          protocol: SSL
        ssl.endpoint.identification.algorithm:
```

-- producer: 
```
spring:
  kafka:
    producer:
	  # other configs
      ssl:
        trust-store-location: file:C:\\Kafka\\ssl\\client.truststore.jks
        trust-store-password: password
        key-store-location: file:C:\\Kafka\\ssl\\client.keystore.jks
        key-store-password: password
      properties:
        protocol: SSL
        security:
          protocol: SSL
        ssl.endpoint.identification.algorithm:
```