Instituto Superior Técnico, Universidade de Lisboa

**Network and Computer Security**

# Lab guide: Secure Messages

## Introduction

This laboratory assignment uses UDP sockets for communication, the JSON data format for data representation and the Java cryptographic functions to implement secure messages that provide different services, including confidentiality and integrity.

### Goals

- Use the cryptographic functions of the Java platform to implement different types of secure messages
- Analyze messages before and after their protection
- Demonstrate effectiveness of protection with attacks

## 0. Setup

For the laboratory you will need one machine with a Java development environment installed.
You should have JDK (*Java Developer Kit*) 8 or later. You can use Linux or Windows.

If you decide on using the SEED Labs VM, you may have to update it and install Maven.

```bash
sudo apt update && sudo apt upgrade && sudo apt install maven
```

To try the examples, the Java code needs to be compiled and executed.

Put the lab files in a working folder with write permissions, like `/tmp/SecureMessages`, for example, and change your working directory to it.

```bash
cd /tmp/SecureMessages
```

You should compile the code using [Maven](https://maven.apache.org/).
Maven retrieves libraries from the Internet and properly configures the classpath for compilation and execution.  
To compile:

```bash
mvn clean compile
```

After successfull compilation, you can execute a class with a main method.  
To execute a specific class with command-line arguments:

```bash
mvn exec:java -Dmainclass=pt.tecnico.CryptoExample -Dexec.args="keys/secret.key"
```

`-D` is the Maven syntax to redefine a property.  
`mainclass` defines the property that sets the class to execute.  
`exec.args` defines the property that sets the command-line arguments.  
You can also modify the class and arguments directly in the `pom.xml` file.

## 1. Code examples

Several examples are included in this laboratory guide, one for each software building block required for the secure messages.
Let us go over them first, before starting the exercise.

### 1.1 UDP sockets

For the communication we will use UDP (*User Datagram Protocol*).
This protocol sends and receives individual packets, without an established connection.

The classes [DatagramServer](src/pt/tecnico/DatagramServer.java) and [DatagramClient](src/pt/tecnico/DatagramClient.java) demonstrate how to use UDP sockets in Java.

First, you should start the server:

```bash
mvn exec:java -Dmainclass=pt.tecnico.DatagramServer -Dexec.args="8000"
```

The server will receive packets on UDP port 8000.
You can use an alternative port, as long as it is not reserved or being used by other applications.

The server runs in a loop that receives a packet and sends back a reply.

To start the client, sending the message `World!` to the server:

```bash
mvn exec:java -Dmainclass=pt.tecnico.DatagramClient -Dexec.args="localhost 8000 World!"
```

Study the source code, in particular, the socket and packet creation, and how to send and receive packets.
The packet buffer is important, as it limits the maximum size of data that can be received.
UDP packets are limited to around 64 KBytes.

```java
DatagramSocket socket = new DatagramSocket(port);
socket.receive(packet);
String text = new String(packet.getData(), 0, packet.getLength());
```

Data is sent and received in binary.
Data interpretation is left to the applications.
In this case, the bytes are converted to String using the default encoding.
This is simple but insufficient for sending structured data.

Reference: [The Java Tutorials: All About Datagrams](https://docs.oracle.com/javase/tutorial/networking/datagrams/index.html)

### 1.2 JSON

For the data representation communication we will use a text-based format, very widely used, called JSON (*JavaScript Object Notation*).
We opt for a text-based format to allow easier debugging, even though it is less efficient than binary formats.

JSON can represent data with multiple fields, with multiple hierarchical levels `{ }` and with support for arrays `[ ]`.

The following JSON represents a message, with information fields, and the message body.  

```json
{
    "info": {
        "from": "Alice",
        "to": [ "Bob", "Charlie" ]
    },
    "body": "Hello friends!"
}
```

For parsing and building JSON in Java, we propose the use of the [GSON library](https://github.com/google/gson).
The library allows the serialization of Java objects, but we use only the JSON parser and object model.
The class [JsonExample](src/pt/tecnico/JsonExample.java) shows how to parse and build JSON documents in Java.  

```java
JsonObject rootJson = JsonParser.parseString​(jsonString).getAsJsonObject();
JsonElement bodyJson = rootJson.get("body");
String bodyValue = bodyJson.getAsString();
```

To run the example:

```bash
mvn exec:java -Dmainclass=pt.tecnico.JsonExample
```

The UDP server and client classes were modified to exchange messages in JSON format.  
The modified classes are called [JsonServer](src/pt/tecnico/JsonServer.java) and [JsonClient](src/pt/tecnico/JsonClient.java).

Reference:

- [JSON Parser tutorial](http://tutorials.jenkov.com/java-json/gson-jsonparser.html)
- [GSON JavaDoc](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/module-summary.html)

### 1.3 JCA

The JCA (*Java Cryptography Architecture*) is part of the Java platform and provides abstractions for secure random number generation, key generation and management, certificates and certificate validation, encryption (symmetric/asymmetric block/stream ciphers), message digests (hashes), and digital signatures.

The [CryptoExample](src/pt/tecnico/CryptoExample.java) performs three things:

- reads a secret key from a file;
- ciphers some plaintext;
- computes the hash of the same plaintext.

To run the example:

```bash
mvn exec:java -Dmainclass=pt.tecnico.CryptoExample -Dexec.args="keys/secret.key"
```

The code can be extended to also read asymmetric keys and to use them.

#### Base64 encoding

The result of criptographic functions is a binary data that cannot be printed to the console or included in JSON messages.
This happens because some binary values do not map to valid text characters.

Base64 is an encoding of binary data using valid text characters that can be used to store binary fields in text fields.
[Base64Example](src/pt/tecnico/Base64Example.java) shows how bytes are encoded to a Base64 string using only valid characters and later decoded back to the original bytes.

```java
String encodedString = Base64.getEncoder().encodeToString(originalBytes);

byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
```

Base64 is **not** encryption, as no key is required to encode or decode.

Reference: [Base64](https://en.wikipedia.org/wiki/Base64)

## 2. Code exercise

The exercise will start from the [JsonServer](src/pt/tecnico/JsonServer.java) and [JsonClient](src/pt/tecnico/JsonClient.java) classes.
They should be copied and renamed to **`SecureServer`** and **`SecureClient`**, both in the filenames and in the Java source code.

To run the newly created server:

```bash
mvn compile exec:java -Dmainclass=pt.tecnico.SecureServer -Dexec.args="8000"
```

Open another console to run the client:

```bash
mvn compile exec:java -Dmainclass=pt.tecnico.SecureClient -Dexec.args="localhost 8000"
```

### 2.1 Add information field to message

The goal of this step is to add a new field to `info`, called `to`, that will transport the identifier of the intended receiver for the message.  
For example, if Alice is sending a message to Bob, then the new field `to` should contain `Bob`.

The message format in JSON contains two parts: `info` to carry additional information about the message and `body` with the actual message.

Add the new field in the client and then receive it and print it on the server.
This will show you how to add and retrieve new data from the JSON message structure.

### 2.2 Add integrity protection

In this step you should add integrity protection to the message.
You can opt for secret key cryptography or public key cryptography.
Take note of the reasons for your choice. Both have advantages and disadvantages.
Test keys of each type are available in the [keys](keys/) folder.

The client should protect the message to send.  
The server must verify if the received message was not tampered.

### 2.3 Add freshness protection

In this step you should add freshness tokens to the message to prevent *replay attacks*.

There are several alternatives for the freshness token.
Select the one you consider the most suitable for this case and take note of their advantages and disadvantages.

The client should add a freshness token to the message to send.  
The server must verify the token in the received message.  
If the received message is not fresh, it should be rejected by the server.

## 3. Attacks

Hopefully the integrity and freshness mechanisms have been correctly implemented.
How can we be confident in their effectiveness unless we try to attack them?

In this step, we will add a conceptual *man-in-the-middle* that will "intercept" the message and try to modify it.
For simplicity, we will not deploy an actual middle server. We will just add malicious lines of code to the client or the server.

### 3.1 Tamper with data

Modify the `body` of the sent message by adding or removing some text.
Were the changes detected by the server?

Also try to modify the information fields (`info`).
Was the protection still effective?

Fix any vulnerabilities that you detect in your code.

### 3.2 Tamper with freshness tokens

Try to have a replayed message be accepted by the server.
Were you successful? If so, how is it possible?

Again, fix the vulnerabilities in your server code, if possible.

## 4. Code exercise continuation

### 4.1 Add confidentiality protection

Keeping the integrity and freshness protections intact, add a confidentiality protection using a cipher.

Make note of your choice of cipher: the name of the algorithm, the key size, and the block mode.

Are you using an hybrid cipher? Justify your answer.

### 4.2 Assess non-repudation

Can the produced solution prevent the repudation of messages i.e. can the client deny having sent a message? Justify your answer.

## 5. Conclusion

In this laboratory assignment we used UDP sockets with JSON payloads, protected with cryptographic functions, to implement custom secure messages.
A good understanding of the building blocks is important to effectively use real-world secure channels such as TLS or SSH.

----

[SIRS Faculty](mailto:meic-sirs@disciplinas.tecnico.ulisboa.pt)
