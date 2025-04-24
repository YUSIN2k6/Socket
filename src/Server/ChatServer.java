package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private static final int PORT = 1234;
	private static final Map<DataOutputStream, String> clients = new HashMap<>();

	public static void main(String[] args) {
		System.out.println("Chat Server is running on port " + PORT);
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			while (true) {
				new ClientHandler(serverSocket.accept()).start();
			}
		} catch (IOException e) {
			System.out.println("Server error: " + e.getMessage());
		}
	}

	private static class ClientHandler extends Thread {
		private Socket socket;
		private DataOutputStream out;
		private DataInputStream in;
		private String username;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());

				byte messageType = in.readByte();
				if (messageType == 0) {
					String firstMessage = in.readUTF();
					if (firstMessage != null && firstMessage.startsWith("USERNAME:")) {
						username = firstMessage.substring(9);
						if (username.isEmpty())
							username = "Anonymous";
					} else {
						username = "Anonymous";
					}
				}

				synchronized (clients) {
					clients.put(out, username);
				}

				while (true) {
					messageType = in.readByte();
					if (messageType == 0 || messageType == 4) { // Tin nhắn text hoặc mã hóa
						String message = in.readUTF();
						String formattedMessage = username + ": " + message;
						synchronized (clients) {
							for (DataOutputStream writer : clients.keySet()) {
								writer.writeByte(messageType);
								writer.writeUTF(formattedMessage);
								writer.flush();
							}
						}
					} else if (messageType == 1 || messageType == 2 || messageType == 5) { // File, ảnh, hoặc tin nhắn
																							// thoại
						String fileName = in.readUTF();
						long fileSize = in.readLong();
						byte[] fileContent = new byte[(int) fileSize];
						in.readFully(fileContent);
						synchronized (clients) {
							for (DataOutputStream writer : clients.keySet()) {
								if (!clients.get(writer).equals(username)) {
									writer.writeByte(messageType);
									writer.writeUTF(fileName);
									writer.writeLong(fileSize);
									writer.write(fileContent);
									writer.flush();
								}
							}
						}
					} else if (messageType == 3) { // Ảnh bí mật
						String fileName = in.readUTF();
						long fileSize = in.readLong();
						byte[] fileContent = new byte[(int) fileSize];
						in.readFully(fileContent);
						String secretMessage = in.readUTF();
						synchronized (clients) {
							for (DataOutputStream writer : clients.keySet()) {
								if (!clients.get(writer).equals(username)) {
									writer.writeByte(3);
									writer.writeUTF(fileName);
									writer.writeLong(fileSize);
									writer.write(fileContent);
									writer.writeUTF(secretMessage);
									writer.flush();
								}
							}
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Client error: " + e.getMessage());
			} finally {
				if (out != null) {
					synchronized (clients) {
						clients.remove(out);
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}