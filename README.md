# Java Web Sunucusu

Bu proje, harici veya 3. parti hiçbir kütüphane kullanılmadan tamamen saf Java (`java.net.ServerSocket`, `java.io.*`) kullanılarak geliştirilmiş çok iş parçacıklı (multi-threaded) temel bir HTTP Web Sunucusudur.

## Özellikler
- **Saf Java:** Harici bağımlılık (Spring, Tomcat vb.) içermez.
- **Çoklu İstek Desteği:** Her gelen istemci bağlantısı ayrı bir `Thread` üzerinde işlenir.
- **HTTP/1.1 Desteği:** Temel `GET` isteklerini karşılar, durum kodlarını (`200 OK`, `404 Not Found`, `501 Not Implemented`) ve içerik tiplerini (`HTML`, `CSS`, `JS`, `PNG`, `JPG`) yönetir.

## Çalıştırma
```bash
javac SimpleHttpServer.java
java SimpleHttpServer
