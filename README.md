# 📡 Broadcast Server

A command-line chat-style broadcast server, written in **Java** with real WebSockets — start it as a server, connect to it as a client from as many terminals as you like, and every message any client sends gets broadcast to everyone else connected.

**Project URL:** https://roadmap.sh/projects/broadcast-server

## 📖 About

### Project Overview

This is a hands-on coding challenge focused on real-time, two-way communication — the kind HTTP alone can't easily do, because HTTP connections close after a single request/response. A WebSocket connection, by contrast, stays open, so a server can push a message to a client at any time, not just in reply to a request. This project uses that to build a simple broadcast/chat server: any client that's connected gets every message any other client sends, as it happens.

**Core requirements of the challenge:**

- A single CLI app, usable two ways:
  - `broadcast-server start` — starts the server
  - `broadcast-server connect` — connects to the server as a client
- The server listens for incoming connections on a configurable (or hardcoded) port
- When a client sends a message, the server broadcasts it to every connected client
- Client connects and disconnects are handled gracefully
- A client can connect, send messages, and receive broadcasts from others
- Multiple clients can be connected and messaging at once
- Errors are handled, and the server shuts down gracefully

This README documents how this implementation approaches each of those requirements, and what actually happens when you run it.

## ✨ Features

| Command | Mode | Description |
|---|---|---|
| `start` | Server | Starts listening for client connections on port `8887` |
| `connect` | Client | Connects to the running server and opens a chat session |

- 💬 Real WebSocket protocol — not a custom text protocol, so any standard WebSocket client (even a browser) can connect and chat
- 📢 True broadcast — any message a client sends reaches every other connected client, via the library's built-in `broadcast()`
- 👋 Join/leave notifications — every client is told when someone connects or disconnects
- 🔌 Multiple simultaneous clients, each in their own terminal session
- 🛑 Graceful server shutdown — `Ctrl+C` cleanly closes every client connection instead of just killing the process
- 🚪 `exit` command on the client — leave the chat cleanly without force-killing the terminal
- ⚠️ Error handling throughout — a client trying to connect to a server that isn't running gets a clear message instead of a crash

## 🔁 How It Works

1. Run `start` in one terminal — the server begins listening on port `8887`.
2. Run `connect` in another terminal — a WebSocket connection opens to the server, and everyone else connected is notified someone joined.
3. Type a message and hit Enter — it's sent to the server, which immediately broadcasts it to every connected client (including the sender).
4. Open more terminals and `connect` again — each one is a fully independent client, and messages from any one reach all the others in real time.
5. Type `exit` in a client to leave the chat cleanly, or `Ctrl+C` the server to shut it down — either way, connected clients are notified rather than just losing the connection unexpectedly.

## 💻 Sample Run

**Terminal 1 — server:**
```
$ broadcast-server start
Server started on port 8887
New client connected: /127.0.0.1:53134
New client connected: /127.0.0.1:53335
Message from /127.0.0.1:53134: Hey everyone!
```

**Terminal 2 — client A:**
```
$ broadcast-server connect
Connected to server!
/127.0.0.1:53335 joined the chat.
Hey everyone!
```

**Terminal 3 — client B:**
```
$ broadcast-server connect
Connected to server!
Hey everyone!
```

Client A's message shows up for both A (its own send) and B, in real time, with no polling involved.

## 🛠 Tech stack

- **Java 11+**
- **Gradle** — build tool and dependency manager
- [`Java-WebSocket`](https://github.com/TooTallNate/Java-WebSocket) — provides `WebSocketServer` and `WebSocketClient`, handling the WebSocket handshake and framing so the app logic can focus on `onOpen` / `onMessage` / `onClose` / `onError`
- `slf4j-simple` — a minimal logging backend, just to keep the library quiet on startup

## 🚀 Getting Started

### Prerequisites

- JDK 11 or later
- Gradle

### Build

```bash
gradle build
```

### Run the server

```bash
gradle run --args="start"
```

### Run a client

In a **separate** terminal:

```bash
gradle run --args="connect"
```

Open as many additional terminals as you like and repeat the `connect` command to add more clients.

> **Note on `Ctrl+C` with `gradle run`:** on Windows, `gradle run` wraps the Java process in a batch script, so `Ctrl+C` is intercepted by that script rather than reaching the app's shutdown hook. For a clean graceful-shutdown test, build a standalone install instead:
> ```bash
> gradle installDist
> .\build\install\broadcast-server\bin\broadcast-server.bat start
> .\build\install\broadcast-server\bin\broadcast-server.bat connect
> ```
> Running it this way lets `Ctrl+C` reach the Java process directly.

### Leaving a chat

Type `exit` in a connected client to disconnect cleanly, or `Ctrl+C` the server terminal to shut the whole server down (every connected client is notified).

## 📁 Project Structure

```
.
├── build.gradle           # Dependencies, plugins, and build configuration
├── settings.gradle        # Project name
└── src/
    └── main/
        └── java/
            └── app/
                ├── App.java      # CLI entry point — dispatches to start/connect
                ├── Server.java   # extends WebSocketServer — connection + broadcast logic
                └── Client.java   # extends WebSocketClient — send/receive + the chat loop
```

## ⚠️ Known limitation

If the server is stopped while a client is still connected, that client correctly detects the disconnect (`Disconnected from server.`). However, if the client is left idle and more text is typed into it afterward, it still tries to send that message before noticing the connection is gone, producing a `Could not send message` line rather than exiting immediately. It does exit cleanly on the *next* input after that. This doesn't affect the core broadcast functionality — it's a minor rough edge in an already-rare scenario (typing into a client after its server has already gone down) rather than something that comes up in normal use.

## 🧠 What I Learned

Building this project helped me understand:

- Why WebSockets exist — the difference between HTTP's one-shot request/response model and a persistent, two-way connection
- Extending an abstract class to plug application logic into a library's event lifecycle (`onOpen`, `onMessage`, `onClose`, `onError`, `onStart`) rather than writing a request loop by hand
- That a WebSocket library runs its networking on its own background thread, so a client can receive messages at any moment — even mid-`Scanner.nextLine()` — without the app explicitly managing threads itself
- The difference between a non-blocking call (`start()`) and a blocking one (`connectBlocking()`), and why that distinction matters for program flow
- Thread-safe state sharing between threads using `volatile`, after running into a stale-read problem where one thread's connection-closed update wasn't being seen by another thread's loop
- Implementing a graceful shutdown with `Runtime.getRuntime().addShutdownHook(...)`, and that `Ctrl+C` doesn't always reach a Java process directly — on Windows, `gradle run`'s batch wrapper intercepts it, which `gradle installDist` sidesteps
- Testing a multi-client, real-time system by hand, using several terminals at once, rather than a single linear request/response flow

---

Project reference: https://roadmap.sh/projects/broadcast-server