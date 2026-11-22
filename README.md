# ESPLink

App Android (`fatima.tech.espnetsmart`, versão 1.1 / versionCode 3) que controla um
dispositivo ESP pela rede Wi-Fi local via WebSocket. O projeto original foi perdido;
este repositório foi reconstruído a partir do `base.apk` com o jadx 1.5.7.

## Compilar

Requisitos: JDK 17 ou 21 e Android SDK com a plataforma 36.

    ./gradlew assembleDebug

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

O envio de e-mail do "Contact Us" usa uma conta SMTP do Gmail. As credenciais ficam
fora do Git, em `local.properties`:

    esplink.mail.user=conta@gmail.com
    esplink.mail.password=senha-de-app

Sem elas o app compila e roda, mas o envio do formulário falha em silêncio.

## Módulos

Tudo está no módulo Gradle `app`, pacote `fatima.tech.espnetsmart`.

| Área | Classes | O que faz |
|---|---|---|
| Tela principal | `MainActivity` | Menu lateral, 4 abas, conexão WebSocket, envio e tratamento das mensagens JSON, varredura e conexão Wi-Fi, temas |
| Aba PUMP | `Tab1` | Bomba d'água: liga/desliga, modo (manual/auto/timer), timers, nível do tanque |
| Aba LIGHTS | `Tab2` | 4 interruptores de luz, cada um com estado, modo e timers |
| Aba SENSORS | `Tab3` | Energia e sensores (medidores em arco) |
| Aba LOGS | `Tab4` | Log das mensagens trocadas e envio manual de comandos |
| Configurações | `SettingsActivity`, `AppCompatPreferenceActivity` | Preferências do app (tema, sincronização, nomes dos interruptores) e do dispositivo (Wi-Fi, IP, portas, mDNS, tanque, bateria, sensor de luz), lidas e gravadas no ESP |
| Rede | `NetworkUtils` | Estado do Wi-Fi, varredura e conexão a redes |
| Fale conosco | `ContactUsActivity`, `SendMail`, `Config` | Formulário enviado por SMTP (JavaMail) |
| Telas estáticas | `HelpActivity`, `AboutActivity`, `RateItDialogFragment` | Ajuda, sobre e pedido de avaliação |
| Views próprias | `ColorArcProgressBar`, `TriToggleButton`, `ScrollAwareFABBehavior`, `QuickReturnFooterBehavior`, `CircularViewPagerHandler` | Componentes visuais |
| Utilitários | `Util`, `Data` | Preferências, menus, cores, cálculo de tempos |

### Protocolo com o dispositivo

WebSocket em `ws://<ServIP>:<sockPort>` (padrão `192.168.0.222:81`), mensagens JSON
entre `<` e `>` com o campo `action`:

- enviadas pelo app: `refresh`, `btnState`, `btnMode`, `btnTimerOn`, `btnTimerOff`,
  `setTime`, `getSett`, `setSett`, `resetSett`;
- recebidas: as mesmas como eco/estado, mais `servResp`, `Pong` e `logClients`.

## Bibliotecas

Declaradas em `app/build.gradle`: Android Support Library 25.3.1 (appcompat, design,
support-v4/v13, cardview, recyclerview, percent), material-dialogs 0.9.4.4,
Java-WebSocket 1.3.7 e JavaMail para Android 1.6.7.

`cn.fanrunqi:waveprogress:1.0.1` não existe mais em repositório público; sua única
classe (`WaveProgressView`) fica como código-fonte em `app/src/main/java/cn/fanrunqi`.

## Diferenças em relação ao APK original

- JavaMail: o APK trazia o port antigo `javamail-android`; aqui é `com.sun.mail:android-mail`.
- Java-WebSocket: versão exata do APK não identificada; usada a 1.3.7 (mesma API).
- Compilado com SDK 36 (o original usava 25); `targetSdk` continua 25.
- Senha do SMTP removida do código-fonte.
