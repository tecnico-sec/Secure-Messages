package pt.tecnico;

import java.io.*;
import java.net.*;

public class DatagramClient {

	/** Buffer size for receiving a UDP packet. */
	private static final int BUFFER_SIZE = 65_507;

	public static void main(String[] args) throws IOException {
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port text%n", DatagramClient.class.getName());
			return;
		}

		// First argument is the server host name
		final String serverHost = args[0];
		// Second argument is the server port
		// Convert port from String to int
		final int serverPort = Integer.parseInt(args[1]);
		final InetAddress serverAddress = InetAddress.getByName(serverHost);

		// Concatenate following arguments using a string builder
		StringBuilder sb = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			sb.append(args[i]);
			if (i < args.length - 1) {
				sb.append(" ");
			}
		}
		final String clientText = sb.toString();

		// Create socket (we are not specifying a client port but we could)
		DatagramSocket socket = new DatagramSocket();

		// Text is sent to server as bytes
		byte[] clientBuffer = clientText.getBytes();
		System.out.printf("%d bytes to send%n", clientBuffer.length);

		DatagramPacket clientPacket = new DatagramPacket(clientBuffer, clientBuffer.length, serverAddress, serverPort);
		System.out.printf("Send to: %s:%d %n", serverHost, serverPort);
		socket.send(clientPacket);
		System.out.println("Sent packet: " + clientPacket);

		byte[] serverBuffer = new byte[BUFFER_SIZE];
		DatagramPacket serverPacket = new DatagramPacket(serverBuffer, serverBuffer.length);
		System.out.println("Wait for packet to arrive...");
		socket.receive(serverPacket);

		System.out.println("Received packet: " + serverPacket);
		String serverText = new String(serverPacket.getData(), 0, serverPacket.getLength());
		System.out.println("Received text: " + serverText);

		socket.close();
		System.out.println("Socket closed");
	}

}
