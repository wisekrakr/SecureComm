![Master](https://github.com/ipphone/core/workflows/Master/badge.svg)
<img src="https://img.shields.io/badge/Java-build%20with%20Java-blue"/>
![version](https://img.shields.io/badge/version-0.0.3-blue)

    
                              CommUniWise Secure: java secure chat and file transfer project
                                    https://github.com/wisekrakr/SecureComm



<a href="https://twitter.com/intent/follow?screen_name=wisekrakr">
        <img src="https://img.shields.io/twitter/follow/wisekrakr?style=social&logo=twitter"
            alt="follow on Twitter"></a>
            
            

Open source (tcp) chat and file transfer application.
Both Server and Client side are incorporated.
The Server allows clients to make a secure connection, the server sends information to the client
to create a "session key" (Secret Key). With this key we can create encrypt and decrypt "messages". 
The Server creates ClientHandlers for multithreading, so multiple clients are able to connect to the server and talk to each other.
The messages are send with as a protocol buffer. Google describes them as "language-neutral, platform-neutral, extensible mechanism for serializing structured data".
--This way of sending data will most likely change in the future. It will probably still be used in the authentication protocol.--

The Client has its own JavaFX GUI inspired by this project: https://github.com/DomHeal/JavaFX-Chat. However the GUI was created with my own small SwingFX
library for creating Swing JFrames with JFXPanel aestictics and functionality:  https://github.com/wisekrakr/SwingFX

The Server has its own "terminal" GUI, created with lanterna. 

#### Work in progress
 * Client authentication with the Server.
 * Multiplexing, so we can chat and transfer multiple files simultaneously.
 * Remove proto buffer
 * Put certificate encryption back in.... with ssl connection, truststore etc.
 * Ability to create multiple chat rooms.
 * Database for authentication (optional)
 * Test on *asterisk server for fun.



#### LICENSE



#### SPECIFICATION

CommUniWise Secure is compatible with the following specifications:
 - RSA asymmetric encryption, 2048 bit
 - AES symmetric encryption, 128 bit
 
 The application initiliazes an algorithm-independent javax.crypto KeyGenerator to then create a Secret Key, with 128 as keysize.

#### PREREQUISITES

This software has been developed using Oracle Java Development Kit
version 7.

#### MAVEN DEPENDENCIES


 > *These are the dependencies used in the Server side of the project:
 ```groovy
   <dependencies>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>3.15.3</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcpkix-jdk15on</artifactId>
            <version>1.68</version>
        </dependency>
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.1</version>
        </dependency>
        <dependency>
            <groupId>com.googlecode.lanterna</groupId>
            <artifactId>lanterna</artifactId>
            <version>3.1.1</version>
        </dependency>
    </dependencies>
```
 > *These are the dependencies used in the Client side of the project:
 ```groovy
     <dependencies>
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.68</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>3.15.3</version>
        </dependency>
        <dependency>
            <groupId>org.fxmisc.richtext</groupId>
            <artifactId>richtextfx</artifactId>
            <version>0.6.10</version>
        </dependency>
        <dependency>
            <groupId>org.controlsfx</groupId>
            <artifactId>controlsfx</artifactId>
            <version>8.40.10</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.11</version>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>19.0</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>11.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>11.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-media</artifactId>
            <version>11.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-swing</artifactId>
            <version>11.0.1</version>
        </dependency>

    </dependencies>
```

#### USAGE

Server side:
- ** _Start up the server_
- ** _The Server will wait for clients to connect_
- ** _The Server will create a ClientHandler and put the ClientHandler in a hashmap with the client ID as key_
- ** _The ClientHandler will receive messages and the ServerListener will send those messages to the right clients_
- ** _The Server will close the connection with the client when the client quits._
- ** _The Server can be stopped with the ServerTerminal, all connections that are currently open will be closed_

Client side:
- ** _Start up a client and give a username, hostname and port and connect to the Server. (Authentication is a W.I.P.)_
- ** _If the Client has a unique ID, the main application will open and the user can then connect to the chatroom._
- ** _The Client can send text based messages, voice messages and direct messages._
- ** _The Client can open voice messages by clicking on the message in the chat pane. A tiny audio player will open and the client can listen to the message._
- ** _The Client can change its status._
- ** _The Client can also transfer files with its own file transfer GUI._ Since the file transfer does not work well enough, this will be a future adding.


## AUTHOR

David Buendia Cosano davidiscodinghere@gmail.com

