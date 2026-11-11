# Java Web Sunucusu (Port: 1989)

Bu proje, harici veya 3. parti hiçbir kütüphane kullanılmadan tamamen saf Java (`java.net.ServerSocket`, `java.io.*`) kullanılarak geliştirilmiş çok iş parçacıklı (multi-threaded) bir HTTP/1.1 Web Sunucusudur.

## Öğrenci Bilgileri
- **Ad Soyad:** Halime Hilal Öztürk
- **Öğrenci No:** 1250505050
- **Ders:** YAZ16203 - Nesneye Yönelik Programlama

## Özellikler
- **Saf Java:** Harici bağımlılık (Spring, Tomcat, Jetty vb.) içermez.
- **1989 Portu:** Sunucu varsayılan olarak `1989` numaralı port üzerinden gelen bağlantıları dinler.
- **Çoklu İstek Desteği:** Her gelen istemci bağlantısı ayrı bir `Thread` üzerinde işlenir.
- **Kişisel Profil Sayfası:** Tarayıcıdan erişildiğinde H1 (Ad Soyad), H2 (Öğrenci No) ve CSS ile biçimlendirilmiş biyografi sayfası sunar.

## Çalıştırma
```bash
javac SimpleHttpServer.java
java SimpleHttpServer
