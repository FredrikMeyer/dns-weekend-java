# DNS in a Weekend in Java

From Julia Evan's [Python tutorial](https://implement-dns.wizardzines.com/).

## Features

 - A-record
 - C-record
 
## Build

```
mvn package
```

## Run

```
java -jar target/uber-dns-weekend-1.0-SNAPSHOT.jar --url www.fredrikmeyer.net
```

Output:
```
Asking for: fredrikmeyer.net
Querying 192.36.148.17 for fredrikmeyer.net.
Name server IP: 192.55.83.30
Querying 192.55.83.30 for fredrikmeyer.net.
Name server IP: 192.174.68.10
Querying 192.174.68.10 for fredrikmeyer.net.
Answer: 185.199.110.153
```
